package controllers;

import com.google.inject.Inject;
import database.WordlistDatabase;
import models.wordlists.*;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.cms_wordbaselist_settings;
import views.html.cms_wordlists;
import views.html.cms_error;
import views.html.cms_logicalwordlist_settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CmsWordListController extends Controller {

    @Inject
    private FormFactory formFactory;

    @Security.Authenticated(CmsSecured.class)
    public Result index() {
        return ok(cms_wordlists.render(WordlistDatabase.getWordBaselists(), WordlistDatabase.getLogicalWordLists()).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result createWordBaselist() {
        Form<WordBaseListAddConfig> config = formFactory.form(WordBaseListAddConfig.class);
        return ok(cms_wordbaselist_settings.render(config, true).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result createLogicalWordList() {
        Form<LogicalWordListAddConfig> config = formFactory.form(LogicalWordListAddConfig.class);
        List<WordBaseList> availableWordBaseLists = WordlistDatabase.getWordBaselists();
        //default values
        config.data().put("preapplyLemmaExtraction", "on");
        return ok(cms_logicalwordlist_settings.render(config, null, availableWordBaseLists, true).body().trim()).as("text/html; charset=utf-8");
    }

    public static class WordBaseListAddConfig {

        private static void fillForm(WordBaseList wordBaseList, Map<String, String> data){
            if(wordBaseList == null){return;}
            data.put("baselistId", wordBaseList.getBaselistId());
            data.put("baselistName", wordBaseList.getBaselistName());
            data.put("description", wordBaseList.getDescription());
            data.put("mappingWordCount", String.valueOf(wordBaseList.getWordCountString()));
        }
    }

    public static class LogicalWordListAddConfig {
        private static void fillForm(LogicalWordList logicalWordList, Map<String, String> data){
            if(logicalWordList == null){return;}
            data.put("logicallistId", logicalWordList.getLogicallistId());
            data.put("logicallistName", logicalWordList.getLogicallistName());
            data.put("description", logicalWordList.getDescription());
            if(logicalWordList.isPreappyLemmaExtraction()){
                data.put("preapplyLemmaExtraction", String.valueOf(logicalWordList.isPreappyLemmaExtraction()));
            }
            //data.put("mappingWordCount", String.valueOf(categoryBaseList.getWordCountString()));
        }
    }

    public static class WordBaseListDeleteConfig{}

    public static class LogicalWordListDeleteConfig{}

    @Security.Authenticated(CmsSecured.class)
    public Result updateWordBaselist() {
        Form<CmsWordListController.WordBaseListAddConfig> form = formFactory.form(CmsWordListController.WordBaseListAddConfig.class).bindFromRequest();
        String id = form.data().get("baselistId");
        boolean isNewLayout = StringUtils.isBlank(id);

        String baselistName = form.data().get("baselistName");
        if(StringUtils.isEmpty(baselistName)){
            return onWordBaselistUpdateError("Word baselist name is not valid.", form, isNewLayout);
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
            WordBaseList existingBaselist = WordlistDatabase.getWordBaselist(id);
            WordBaseList editedBaselist = WordBaseList.createWordBaselist(baselistName, description, uploadedFileName);
            if (existingBaselist == null) {
                importWordListFile(editedBaselist, uploadedFile, uploadedFileName);
                editedBaselist.save();
            } else {
                boolean mappingFileChanged = !existingBaselist.getUploadedFilename().equals(uploadedFileName) && uploadedFile != null;
                if (mappingFileChanged) {
                    importWordListFile(existingBaselist, uploadedFile, uploadedFileName);
                }
                existingBaselist.update(editedBaselist);
                sendWordBaselistUpdatePush(id);
            }

            flash().clear();
            flash("success", "Word baselists saved successfully.");
            return redirect(routes.CmsWordListController.index());
        } catch (RimeFormatException re) {
            String detailMessage = "";
            if ("wrong-header".equals(re.getMessage())){
                detailMessage = "In the header the type must be words";
            }
            return onWordBaselistUpdateError("the uploaded file has a wrong format: "+detailMessage, form, isNewLayout);
        } catch (Exception e) {
            Logger.error("error creating baselist", e);
            return onWordBaselistUpdateError("saving Word Base List failed.", form, isNewLayout);
        }
    }

    @Security.Authenticated(CmsSecured.class)
    public Result updateLogicalWordList() {
        Form<CmsWordListController.LogicalWordListAddConfig> form = formFactory.form(CmsWordListController.LogicalWordListAddConfig.class).bindFromRequest();
        String id = form.data().get("logicallistId");
        boolean isNewLayout = StringUtils.isBlank(id);

        String logicallistName = form.data().get("logicallistName");
        if(StringUtils.isEmpty(logicallistName)){
            return onLogicalWordListUpdateError("Logical Word List name is not valid.", form, isNewLayout);
        }
        String description = form.data().get("description");
        boolean preapplyLemmaExtraction = StringUtils.equals("on", form.data().get("preapplyLemmaExtraction"));

        try {
            LogicalWordList existinLogicalWordList = WordlistDatabase.getLogicalWordList(id);
            LogicalWordList editedLogicalWordList = LogicalWordList.createLogicalWordList(logicallistName, description, preapplyLemmaExtraction);
            if(existinLogicalWordList == null){
                editedLogicalWordList.save();
            }else{
                existinLogicalWordList.update(editedLogicalWordList);
                sendLogicalWordListUpdatePush(id);
            }

            flash().clear();
            flash("success", "Logical Word List saved successfully.");
            // if this is a new list, stay on its edit page to add a baselist (adding baselist is not possible before the first save)
            if (!isNewLayout) {
                return redirect(routes.CmsWordListController.index());
            }
            else {
                return getLogicalWordListDetails(editedLogicalWordList.getLogicallistId());
            }
        } catch (Exception e) {
            Logger.error("error creating logical word list", e);
            return onLogicalWordListUpdateError("saving Logical Word List failed.", form, isNewLayout);
        }
    }

    @Security.Authenticated(CmsSecured.class)
    public Result deleteWordBaselist() {
        Form<CmsWordListController.WordBaseListDeleteConfig> form = formFactory.form(CmsWordListController.WordBaseListDeleteConfig.class).bindFromRequest();
        String id = form.data().get("baselistId");
        if(StringUtils.isEmpty(id)){
            flash().clear();
            flash("error", "Word baselist could not be deleted.");
            return redirect(routes.CmsWordListController.getBaselistDetails(id));
        }
        if(WordlistDatabase.deleteWordBaselist(id)){
            if(WordlistDatabase.isWordBaselistDBEmpty()){
                flash().clear();
                flash("success", "All word baselists successfully deleted. The standard baselist got restored.");
            }else{
                flash().clear();
                flash("success", "Word baselist successfully deleted.");
            }
            return redirect(routes.CmsWordListController.index());
        }else{
            flash().clear();
            flash("error", "The baselist could not be removed from database.");
            return redirect(routes.CmsWordListController.getBaselistDetails(id));
        }
    }

    @Security.Authenticated(CmsSecured.class)
    public Result deleteLogicalWordList() {
        Form<CmsWordListController.LogicalWordListDeleteConfig> form = formFactory.form(CmsWordListController.LogicalWordListDeleteConfig.class).bindFromRequest();
        String id = form.data().get("logicallistId");
        if(StringUtils.isEmpty(id)){
            flash().clear();
            flash("error", "Logical Word List could not be deleted.");
            return redirect(routes.CmsWordListController.getLogicalWordListDetails(id));
        }
        if(WordlistDatabase.deleteLogicalWordList(id)){
            if(WordlistDatabase.isLogicalWordListDBEmpty()){
                flash().clear();
                flash("success", "All logical word lists successfully deleted. The standard logical word list got restored.");
            }else{
                flash().clear();
                flash("success", "Logical Word list layout deleted.");
            }
            return redirect(routes.CmsWordListController.index());
        }else{
            flash().clear();
            flash("error", "The logical word list could not be removed from database.");
            return redirect(routes.CmsWordListController.getLogicalWordListDetails(id));
        }
    }

    @Security.Authenticated(CmsSecured.class)
    public Result getBaselistDetails(String id) {
        Form<WordBaseListAddConfig> form = formFactory.form(WordBaseListAddConfig.class);
        WordBaseList wordBaselist = WordlistDatabase.getWordBaselist(id);
        if(wordBaselist == null){
            return internalServerError(cms_error.render("invalid word base list id"));
        }
        WordBaseListAddConfig.fillForm(wordBaselist, form.data());
        return ok(cms_wordbaselist_settings.render(form, false).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result getLogicalWordListDetails(String id) {
        Form<LogicalWordListAddConfig> form = formFactory.form(LogicalWordListAddConfig.class);
        LogicalWordList logicalWordList = WordlistDatabase.getLogicalWordList(id);
        if(logicalWordList == null){
            return internalServerError(cms_error.render("invalid logical word list id"));
        }
        LogicalWordListAddConfig.fillForm(logicalWordList, form.data());
        List<WordBaseList> availableWordBaselists = WordlistDatabase.getWordBaselists();
        return ok(cms_logicalwordlist_settings.render(form, logicalWordList, availableWordBaselists, false).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result onWordBaselistUpdateError(String error, Form<CmsWordListController.WordBaseListAddConfig> form, boolean isNewBaselist){
        flash().clear();
        flash("error", error);
        return internalServerError(cms_wordbaselist_settings.render(form, isNewBaselist).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result onLogicalWordListUpdateError(String error, Form<CmsWordListController.LogicalWordListAddConfig> form, boolean isNewBaselist){
        flash().clear();
        flash("error", error);
        // TODO should not return empty list here, this is wrong
        return internalServerError(cms_logicalwordlist_settings.render(form, null, null, isNewBaselist).body().trim()).as("text/html; charset=utf-8");
    }

    private void sendWordBaselistUpdatePush(String baselistId){
        // TODO
//        Collection<String> tokens = UserDatabase.getUserPushTokenWithKeyboardLayoutId(baselistId);
//        tokens.forEach(PushSender::sendUpdatePushToDevice);
    }

    private void sendLogicalWordListUpdatePush(String logicallistId){
        // TODO
    }

    /**
     * the file should be like:
     * [type=words]
     * der
     * die
     * das
     * in 2  # only the content before the first space of each line is imported
     * und 2
     * sein 3 V
     * @param wordBaseList where to import the mappings to
     * @param file uploaded file in ResearchIME format
     * @param filename
     */
    private void importWordListFile(WordBaseList wordBaseList, File file, String filename) throws IOException, RimeFormatException {

        // TODO validate RIME format?

        List<Word> words = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

        // very basic format check: is type header correct?
        if (!"[type=words]".equals(line)) {
            throw new RimeFormatException("wrong-header");
        }

            while (line != null) {
                // read next line
                line = reader.readLine();
                if (StringUtils.isEmpty(line)){
                    Logger.warn("skipped empty line");
                    continue;
                }
                words.add(new Word(line.split(" ")[0], wordBaseList));
            }
            reader.close();

        wordBaseList.setWords(words);
    }

    @Security.Authenticated(CmsSecured.class)
    public Result addBaselistToLogicalWordList() {
        String logicallistId = request().getQueryString("logicallistId");
        LogicalWordList logicalWordList = WordlistDatabase.getLogicalWordList(logicallistId);


        String baselistId = request().getQueryString("baselistId");
        WordBaseList wordBaseList = WordlistDatabase.getWordBaselist(baselistId);

        if (logicalWordList.getIncludesWordBaseLists().contains(wordBaseList)){
            throw new IllegalArgumentException("the baselist has already been added to this logical list");
        }

        logicalWordList.getIncludesWordBaseLists().add(wordBaseList);
        logicalWordList.update();

        flash().clear();
        flash("success", "added "+wordBaseList.getBaselistName()+" to "+logicalWordList.getLogicallistName()+" successfully.");

        return redirect(routes.CmsWordListController.getLogicalWordListDetails(logicallistId));
    }

    @Security.Authenticated(CmsSecured.class)
    public Result removeBaselistFromLogicalWordList() {
        String logicallistId = request().getQueryString("logicallistId");
        LogicalWordList logicalWordList = WordlistDatabase.getLogicalWordList(logicallistId);


        String baselistId = request().getQueryString("baselistId");
        WordBaseList wordBaseList = WordlistDatabase.getWordBaselist(baselistId);

        if (!logicalWordList.getIncludesWordBaseLists().contains(wordBaseList)){
            throw new IllegalArgumentException("the baselist has already been added to this logical list");
        }

        logicalWordList.getIncludesWordBaseLists().remove(wordBaseList);
        logicalWordList.update();

        flash().clear();
        flash("success", "removed "+wordBaseList.getBaselistName()+" from "+logicalWordList.getLogicallistName()+" successfully.");

        return redirect(routes.CmsWordListController.getLogicalWordListDetails(logicallistId));
    }



}
