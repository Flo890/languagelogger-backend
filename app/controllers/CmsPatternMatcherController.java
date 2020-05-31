package controllers;

import com.google.inject.Inject;
import database.PatternMatcherDatabase;
import database.WordlistDatabase;
import models.config.PatternMatcherConfig;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.cms_error;
import views.html.cms_patternmatcher_settings;
import views.html.cms_patternmatchers;

import java.util.Map;

public class CmsPatternMatcherController extends Controller {

    @Inject
    private FormFactory formFactory;

    @Security.Authenticated(CmsSecured.class)
    public Result index() {
        return ok(cms_patternmatchers.render(PatternMatcherDatabase.getPatternMatchers()).body().trim()).as("text/html; charset=utf-8");
    }


    @Security.Authenticated(CmsSecured.class)
    public Result getPatternMatcherDetails(String id) {
        Form<CmsPatternMatcherController.PatternMatcherAddConfig> form = formFactory.form(CmsPatternMatcherController.PatternMatcherAddConfig.class);
        PatternMatcherConfig patternMatcherConfig = PatternMatcherDatabase.getPatternMatcher(id);
        if(patternMatcherConfig == null){
            return internalServerError(cms_error.render("invalid patternmatcher id"));
        }
        CmsPatternMatcherController.PatternMatcherAddConfig.fillForm(patternMatcherConfig, form.data());
        return ok(cms_patternmatcher_settings.render(form, false).body().trim()).as("text/html; charset=utf-8");
    }


    @Security.Authenticated(CmsSecured.class)
    public Result createPatternMatcher() {
        Form<CmsPatternMatcherController.PatternMatcherAddConfig> config = formFactory.form(CmsPatternMatcherController.PatternMatcherAddConfig.class);
        //default values
        config.data().put("logRawContent", "on");
        return ok(cms_patternmatcher_settings.render(config, true).body().trim()).as("text/html; charset=utf-8");
    }

    public static class PatternMatcherAddConfig {

        private static void fillForm(PatternMatcherConfig patternMatcherConfig, Map<String, String> data){
            if(patternMatcherConfig == null){return;}
            data.put("regexMatcherId", patternMatcherConfig.getRegexMatcherId());
            data.put("regexMatcherName", patternMatcherConfig.getRegexMatcherName());
            data.put("regex", patternMatcherConfig.getRegex());
            if(patternMatcherConfig.isLogRawContent()){
                data.put("logRawContent", String.valueOf(patternMatcherConfig.isLogRawContent()));
            }
        }
    }

    public static class PatternMatcherDeleteConfig{}

    @Security.Authenticated(CmsSecured.class)
    public Result updatePatternMatcher() {
        Form<CmsPatternMatcherController.PatternMatcherAddConfig> form = formFactory.form(CmsPatternMatcherController.PatternMatcherAddConfig.class).bindFromRequest();
        String id = form.data().get("regexMatcherId");
        boolean isNewLayout = StringUtils.isBlank(id);

        String regexMatcherName = form.data().get("regexMatcherName");
        if(StringUtils.isEmpty(regexMatcherName)){
            return onPatternMatcherUpdateError("pattern matcher name is not valid.", form, isNewLayout);
        }
        String regex = form.data().get("regex");
        if(StringUtils.isEmpty(regex)){
            return onPatternMatcherUpdateError("regex is not valid.", form, isNewLayout);
        }
        boolean logRawContent = StringUtils.equals("on", form.data().get("logRawContent"));

        try {
            PatternMatcherConfig existingPatternMatcherConfig = PatternMatcherDatabase.getPatternMatcher(id);
            PatternMatcherConfig editedPatternMatcherConfig = PatternMatcherConfig.createPatternMatcher(regexMatcherName, regex, logRawContent, true);
            if(existingPatternMatcherConfig == null){
                editedPatternMatcherConfig.save();
            }else{
                existingPatternMatcherConfig.update(editedPatternMatcherConfig);
                sendPatternMatcherUpdatePush(id);
            }

            flash().clear();
            flash("success", "Pattern Matcher saved successfully.");
            return redirect(routes.CmsPatternMatcherController.index());
        } catch (Exception e) {
            Logger.error("error creating logical category list", e);
            return onPatternMatcherUpdateError("saving Pattern Matcher failed.", form, isNewLayout);
        }
    }

    @Security.Authenticated(CmsSecured.class)
    public Result onPatternMatcherUpdateError(String error, Form<CmsPatternMatcherController.PatternMatcherAddConfig> form, boolean isNewPatternMatcher){
        flash().clear();
        flash("error", error);
        // TODO should not return empty list here, this is wrong
        return internalServerError(cms_patternmatcher_settings.render(form, isNewPatternMatcher).body().trim()).as("text/html; charset=utf-8");
    }

    private void sendPatternMatcherUpdatePush(String patternMatcherId){
        // TODO
//        Collection<String> tokens = UserDatabase.getUserPushTokenWithKeyboardLayoutId(baselistId);
//        tokens.forEach(PushSender::sendUpdatePushToDevice);
    }


    @Security.Authenticated(CmsSecured.class)
    public Result deletePatternMatcher() {
        Form<CmsPatternMatcherController.PatternMatcherDeleteConfig> form = formFactory.form(CmsPatternMatcherController.PatternMatcherDeleteConfig.class).bindFromRequest();
        String id = form.data().get("regexMatcherId");
        if(StringUtils.isEmpty(id)){
            flash().clear();
            flash("error", "Pattern Matcher could not be deleted.");
            return redirect(routes.CmsPatternMatcherController.getPatternMatcherDetails(id));
        }
        if(PatternMatcherDatabase.deleteCategoryBaselist(id)){
            if(WordlistDatabase.isCategoryBaselistDBEmpty()){
                flash().clear();
                flash("success", "All pattern matchers successfully deleted.");
            }else{
                flash().clear();
                flash("success", "Pattern Matcher successfully deleted.");
            }
            return redirect(routes.CmsPatternMatcherController.index());
        }else{
            flash().clear();
            flash("error", "The pattern matcher could not be removed from database.");
            return redirect(routes.CmsCategoryListController.getBaselistDetails(id));
        }
    }


}
