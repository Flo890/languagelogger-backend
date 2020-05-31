package controllers;

import com.google.inject.Inject;
import database.WordlistDatabase;
import models.wordlists.CategoryBaseList;
import models.wordlists.LogicalCategoryList;
import models.wordlists.Word2CategoryMapping;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import utils.PushSender;
import utils.UserSortType;
import views.html.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CmsCategoryListController extends Controller {

    @Inject
    private FormFactory formFactory;

    @Security.Authenticated(CmsSecured.class)
    public Result index() {
        return ok(cms_categorylists.render(WordlistDatabase.getCategoryBaselists(), WordlistDatabase.getLogicalCategoryLists()).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result createCategoryBaselist() {
        Form<CategoryBaseListAddConfig> config = formFactory.form(CategoryBaseListAddConfig.class);
        return ok(cms_categorybaselist_settings.render(config, null, true).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result createLogicalCategoryList() {
        Form<LogicalCategoryListAddConfig> config = formFactory.form(LogicalCategoryListAddConfig.class);
        List<CategoryBaseList> availableCategoryBaseLists = WordlistDatabase.getCategoryBaselists();
        //default values
        config.data().put("preapplyLemmaExtraction", "on");
        return ok(cms_logicalcategorylist_settings.render(config, null, availableCategoryBaseLists, true).body().trim()).as("text/html; charset=utf-8");
    }

    public static class CategoryBaseListAddConfig {

        private static void fillForm(CategoryBaseList categoryBaseList, Map<String, String> data){
            if(categoryBaseList == null){return;}
            data.put("baselistId", categoryBaseList.getBaselistId());
            data.put("baselistName", categoryBaseList.getBaselistName());
            data.put("description", categoryBaseList.getDescription());
            data.put("mappingWordCount", String.valueOf(categoryBaseList.getWordCountString()));
        }
    }

    public static class LogicalCategoryListAddConfig {
        private static void fillForm(LogicalCategoryList logicalCategoryList, Map<String, String> data){
            if(logicalCategoryList == null){return;}
            data.put("logicallistId", logicalCategoryList.getLogicallistId());
            data.put("logicallistName", logicalCategoryList.getLogicallistName());
            data.put("description", logicalCategoryList.getDescription());
            if(logicalCategoryList.isPreappyLemmaExtraction()){
                data.put("preapplyLemmaExtraction", String.valueOf(logicalCategoryList.isPreappyLemmaExtraction()));
            }
            //data.put("mappingWordCount", String.valueOf(categoryBaseList.getWordCountString()));
        }
    }

    public static class CategoryBaseListDeleteConfig{}

    public static class LogicalCategoryListDeleteConfig{}

    @Security.Authenticated(CmsSecured.class)
    public Result updateCategoryBaselist() {
        Form<CmsCategoryListController.CategoryBaseListAddConfig> form = formFactory.form(CmsCategoryListController.CategoryBaseListAddConfig.class).bindFromRequest();
        String id = form.data().get("baselistId");
        boolean isNewLayout = StringUtils.isBlank(id);

        String baselistName = form.data().get("baselistName");
        if(StringUtils.isEmpty(baselistName)){
            return onCategoryBaselistUpdateError("Category baselist name is not valid.", form, isNewLayout);
        }
        String description = form.data().get("description");

        Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        final Http.MultipartFormData.FilePart<File> filePart = formData.getFile("baselistFile");

        File uploadedFile = null;
        String uploadedFileName = null;
        if (filePart != null) {
            uploadedFile = filePart.getFile();
            uploadedFileName = filePart.getFilename();
        }



        try {
            CategoryBaseList existingBaselist = WordlistDatabase.getCategoryBaselist(id);
            CategoryBaseList editedBaselist = CategoryBaseList.createCategoryBaselist(baselistName, description, uploadedFileName);
            if (existingBaselist == null) {
                importWord2CategoryFile(editedBaselist, uploadedFile, uploadedFileName);
                editedBaselist.save();
            } else {
                boolean mappingFileChanged = !existingBaselist.getUploadedFilename().equals(uploadedFileName) && uploadedFile != null;
                if (mappingFileChanged) {
                    importWord2CategoryFile(existingBaselist, uploadedFile, uploadedFileName);
                }
                existingBaselist.update(editedBaselist);
                sendCategoryBaselistUpdatePush(id);
            }

            flash().clear();
            flash("success", "Category baselists saved successfully.");
            return redirect(routes.CmsCategoryListController.index());
        } catch (RimeFormatException re) {
            String detailMessage = "";
            if ("wrong-header".equals(re.getMessage())){
                detailMessage = "In the header the type must be word2category";
            }
            return onCategoryBaselistUpdateError("the uploaded file has a wrong format: "+detailMessage, form, isNewLayout);
        } catch (Exception e) {
            Logger.error("error creating baselist", e);
            return onCategoryBaselistUpdateError("saving Category Base List failed.", form, isNewLayout);
        }
    }

    @Security.Authenticated(CmsSecured.class)
    public Result updateLogicalCategoryList() {
        Form<CmsCategoryListController.LogicalCategoryListAddConfig> form = formFactory.form(CmsCategoryListController.LogicalCategoryListAddConfig.class).bindFromRequest();
        String id = form.data().get("logicallistId");
        boolean isNewLayout = StringUtils.isBlank(id);

        String logicallistName = form.data().get("logicallistName");
        if(StringUtils.isEmpty(logicallistName)){
            return onLogicalCategoryListUpdateError("Logical Category List name is not valid.", form, isNewLayout);
        }
        String description = form.data().get("description");
        boolean preapplyLemmaExtraction = StringUtils.equals("on", form.data().get("preapplyLemmaExtraction"));

        try {
            LogicalCategoryList existinLogicalCategoryList = WordlistDatabase.getLogicalCategoryList(id);
            LogicalCategoryList editedLogicalCategoryList = LogicalCategoryList.createLogicalCategoryList(logicallistName, description, preapplyLemmaExtraction);
            if(existinLogicalCategoryList == null){
                editedLogicalCategoryList.save();
            }else{
                existinLogicalCategoryList.update(editedLogicalCategoryList);
                sendLogicalCategoryListUpdatePush(id);
            }

            flash().clear();
            flash("success", "Logical Category List saved successfully.");
            // if this is a new list, stay on its edit page to add a baselist (adding baselist is not possible before the first save)
            if (!isNewLayout) {
                return redirect(routes.CmsCategoryListController.index());
            }
            else {
                return getLogicalCategoryListDetails(editedLogicalCategoryList.getLogicallistId());
            }
        } catch (Exception e) {
            Logger.error("error creating logical category list", e);
            return onLogicalCategoryListUpdateError("saving Logical Category List failed.", form, isNewLayout);
        }
    }

    @Security.Authenticated(CmsSecured.class)
    public Result deleteCategoryBaselist() {
        Form<CmsCategoryListController.CategoryBaseListDeleteConfig> form = formFactory.form(CmsCategoryListController.CategoryBaseListDeleteConfig.class).bindFromRequest();
        String id = form.data().get("baselistId");
        if(StringUtils.isEmpty(id)){
            flash().clear();
            flash("error", "Category baselist could not be deleted.");
            return redirect(routes.CmsCategoryListController.getBaselistDetails(id));
        }
        if(WordlistDatabase.deleteCategoryBaselist(id)){
            if(WordlistDatabase.isCategoryBaselistDBEmpty()){
                flash().clear();
                flash("success", "All category baselists successfully deleted. The standard baselist got restored.");
            }else{
                flash().clear();
                flash("success", "Category baselist successfully deleted.");
            }
            return redirect(routes.CmsCategoryListController.index());
        }else{
            flash().clear();
            flash("error", "The baselist could not be removed from database.");
            return redirect(routes.CmsCategoryListController.getBaselistDetails(id));
        }
    }

    @Security.Authenticated(CmsSecured.class)
    public Result deleteLogicalCategoryList() {
        Form<CmsCategoryListController.LogicalCategoryListDeleteConfig> form = formFactory.form(CmsCategoryListController.LogicalCategoryListDeleteConfig.class).bindFromRequest();
        String id = form.data().get("logicallistId");
        if(StringUtils.isEmpty(id)){
            flash().clear();
            flash("error", "Logical Category List could not be deleted.");
            return redirect(routes.CmsCategoryListController.getLogicalCategoryListDetails(id));
        }
        if(WordlistDatabase.deleteLogicalCategoryList(id)){
            if(WordlistDatabase.isLogicalCategoryListDBEmpty()){
                flash().clear();
                flash("success", "All logical category lists successfully deleted. The standard logical category list got restored.");
            }else{
                flash().clear();
                flash("success", "Logical Category list layout deleted.");
            }
            return redirect(routes.CmsCategoryListController.index());
        }else{
            flash().clear();
            flash("error", "The logical category list could not be removed from database.");
            return redirect(routes.CmsCategoryListController.getLogicalCategoryListDetails(id));
        }
    }

    @Security.Authenticated(CmsSecured.class)
    public Result getBaselistDetails(String id) {
        Form<CategoryBaseListAddConfig> form = formFactory.form(CategoryBaseListAddConfig.class);
        CategoryBaseList categoryBaselist = WordlistDatabase.getCategoryBaselist(id);
        if(categoryBaselist == null){
            return internalServerError(cms_error.render("invalid category base list id"));
        }
        CategoryBaseListAddConfig.fillForm(categoryBaselist, form.data());
        Map<String,Integer> categoryWordCount = WordlistDatabase.getCategoriesWordCountForBaselist(id);
        return ok(cms_categorybaselist_settings.render(form, categoryWordCount, false).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result getLogicalCategoryListDetails(String id) {
        Form<LogicalCategoryListAddConfig> form = formFactory.form(LogicalCategoryListAddConfig.class);
        LogicalCategoryList logicalCategoryList = WordlistDatabase.getLogicalCategoryList(id);
        if(logicalCategoryList == null){
            return internalServerError(cms_error.render("invalid logical category list id"));
        }
        LogicalCategoryListAddConfig.fillForm(logicalCategoryList, form.data());
        List<CategoryBaseList> availableCategoryBaselists = WordlistDatabase.getCategoryBaselists();
        //Map<String,Integer> categoryWordCount = WordlistDatabase.getCategoriesWordCountForBaselist(id);
        return ok(cms_logicalcategorylist_settings.render(form, logicalCategoryList, availableCategoryBaselists, false).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result onCategoryBaselistUpdateError(String error, Form<CmsCategoryListController.CategoryBaseListAddConfig> form, boolean isNewBaselist){
        flash().clear();
        flash("error", error);
        return internalServerError(cms_categorybaselist_settings.render(form, null, isNewBaselist).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result onLogicalCategoryListUpdateError(String error, Form<CmsCategoryListController.LogicalCategoryListAddConfig> form, boolean isNewBaselist){
        flash().clear();
        flash("error", error);
        // TODO should not return empty list here, this is wrong
        return internalServerError(cms_logicalcategorylist_settings.render(form, null, null, isNewBaselist).body().trim()).as("text/html; charset=utf-8");
    }

    private void sendCategoryBaselistUpdatePush(String baselistId){
        // TODO
//        Collection<String> tokens = UserDatabase.getUserPushTokenWithKeyboardLayoutId(baselistId);
//        tokens.forEach(PushSender::sendUpdatePushToDevice);
    }

    private void sendLogicalCategoryListUpdatePush(String logicallistId){
        // TODO
    }

    /**
     *
     * @param categoryBaseList where to import the mappings to
     * @param file uploaded file in ResearchIME format
     * @param filename
     */
    private void importWord2CategoryFile(CategoryBaseList categoryBaseList, File file, String filename) throws IOException, RimeFormatException {

        // TODO validate RIME format?

        List<Word2CategoryMapping> mappings = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();

        // very basic format check: is type header correct?
        if (!"[type=word2category]".equals(line)) {
            throw new RimeFormatException("wrong-header");
        }

            while (line != null) {
                // read next line
                line = reader.readLine();
                if (StringUtils.isEmpty(line)){
                    Logger.warn("skipped empty line");
                    continue;
                }
                String[] lineAsArray;
                if((lineAsArray = line.split("\t")).length == 2){
                    mappings.add(new Word2CategoryMapping(lineAsArray[0], lineAsArray[1], categoryBaseList));
                }
                else {
                    throw new IllegalArgumentException("could not split line "+line);
                }

            }
            reader.close();

        categoryBaseList.setWord2CategoryMappings(mappings);
    }

    @Security.Authenticated(CmsSecured.class)
    public Result addBaselistToLogicalCategoryList() {
        String logicallistId = request().getQueryString("logicallistId");
        LogicalCategoryList logicalCategoryList = WordlistDatabase.getLogicalCategoryList(logicallistId);


        String baselistId = request().getQueryString("baselistId");
        CategoryBaseList categoryBaseList = WordlistDatabase.getCategoryBaselist(baselistId);

        if (logicalCategoryList.getIncludesCategoryBaseLists().contains(categoryBaseList)){
            throw new IllegalArgumentException("the baselist has already been added to this logical list");
        }

        logicalCategoryList.getIncludesCategoryBaseLists().add(categoryBaseList);
        logicalCategoryList.update();

        flash().clear();
        flash("success", "added "+categoryBaseList.getBaselistName()+" to "+logicalCategoryList.getLogicallistName()+" successfully.");

        return redirect(routes.CmsCategoryListController.getLogicalCategoryListDetails(logicallistId));
    }

    @Security.Authenticated(CmsSecured.class)
    public Result removeBaselistFromLogicalCategoryList() {
        String logicallistId = request().getQueryString("logicallistId");
        LogicalCategoryList logicalCategoryList = WordlistDatabase.getLogicalCategoryList(logicallistId);


        String baselistId = request().getQueryString("baselistId");
        CategoryBaseList categoryBaseList = WordlistDatabase.getCategoryBaselist(baselistId);

        if (!logicalCategoryList.getIncludesCategoryBaseLists().contains(categoryBaseList)){
            throw new IllegalArgumentException("the baselist has already been added to this logical list");
        }

        logicalCategoryList.getIncludesCategoryBaseLists().remove(categoryBaseList);
        logicalCategoryList.update();

        flash().clear();
        flash("success", "removed "+categoryBaseList.getBaselistName()+" from "+logicalCategoryList.getLogicallistName()+" successfully.");

        return redirect(routes.CmsCategoryListController.getLogicalCategoryListDetails(logicallistId));
    }



}
