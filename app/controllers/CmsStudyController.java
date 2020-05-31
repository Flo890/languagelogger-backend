/*
 * Copyright (C) 2016 - 2018 ResearchIME Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

import com.google.inject.Inject;
import config.ConfigManager;
import database.KeyboardDatabase;
import models.config.ConfigItem;
import models.config.ConfigModel;
import models.config.KeyboardConfigItem;
import org.apache.commons.lang3.StringUtils;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.TimeItem;
import views.html.cms_study;

import java.util.List;
import java.util.Map;

public class CmsStudyController extends Controller {

    @Inject
    private FormFactory formFactory;

    @Security.Authenticated(CmsSecured.class)
    public Result index() {
        Form<StudyConfig> form = formFactory.form(StudyConfig.class);
        ConfigItem config = ConfigManager.getInstance().getConfig();
        StudyConfig.fillForm(config, form.data());
        return ok(cms_study.render(form, config.getKeyboards(), KeyboardDatabase.getKeyboards()).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result update(){
        Form<StudyConfig> form = formFactory.form(StudyConfig.class).bindFromRequest();
        try {
            TimeItem timeItem = new TimeItem(form.data().get("studyDays"),form.data().get("studyHours"),form.data().get("studyMinutes"));
            ConfigItem config = ConfigManager.getInstance().getConfig();
            config.setStudyName(form.data().get("studyName"));
            config.setStudyWelcomeTitle(form.data().get("infoTitle"));
            config.setStudyWelcomeMessage(form.data().get("infoMessage"));
            config.setShowName(StringUtils.equals("on", form.data().get("showName")));
            config.setShowEndDate(StringUtils.equals("on", form.data().get("showEndDate")));
            config.setShowStatistics(StringUtils.equals("on", form.data().get("showStatistics")));
            config.setShowKeyboardName(StringUtils.equals("on", form.data().get("showKeyboardName")));
            config.setStudyDuration(timeItem.getTimestamp());
            ConfigModel.save(config);
            flash().clear();
            flash("success", "Configuration saved successfully.");
            return redirect(routes.CmsStudyController.index());
        } catch (Exception e) {
            return updateError("Invalid duration.", form);
        }
    }

    @Security.Authenticated(CmsSecured.class)
    private Result updateError(String error, Form<StudyConfig> form){
        flash().clear();
        flash("error", error);
        ConfigItem config = ConfigManager.getInstance().getConfig();
        return internalServerError(cms_study.render(form, config.getKeyboards(), KeyboardDatabase.getKeyboards()).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result deleteKeyboard(){
        Form<StudyAddKeyboard> form = formFactory.form(StudyAddKeyboard.class).bindFromRequest();
        String deleteId = form.data().get("deleteId");
        if(StringUtils.isEmpty(deleteId)){
            return onModifyKeyboardError("Keyboard association could not be deleted.");
        }
        String deletePosText = form.data().get("deletePos");
        int deletePos;
        try{
            deletePos = Integer.parseInt(deletePosText);
            ConfigItem config = ConfigManager.getInstance().getConfig();
            List<KeyboardConfigItem> keyboards = config.getKeyboards();
            if(StringUtils.equals(deleteId, keyboards.get(deletePos).getId())){
                keyboards.remove(deletePos);
            }
            ConfigModel.save(config);
            flash().clear();
            flash("success","Keyboard assignment deleted successfully.");
            return redirect(routes.CmsStudyController.index());
        }catch (Exception e){
            //Logger.warn(e);
        }
        return onModifyKeyboardError("Keyboard association could not be deleted.");
    }

    @Security.Authenticated(CmsSecured.class)
    private Result onModifyKeyboardError(String error){
        flash().clear();
        flash("error", error);
        return redirect(routes.CmsStudyController.index());
    }

    @Security.Authenticated(CmsSecured.class)
    public Result addKeyboard(){
        Form<StudyAddKeyboard> form = formFactory.form(StudyAddKeyboard.class).bindFromRequest();
        String keyboardId = form.data().get("addKeyboardId");
        if(StringUtils.isBlank(keyboardId)){
            return onModifyKeyboardError("Keyboard association could not be created.");
        }
        try {
            TimeItem timeItem = new TimeItem(form.data().get("addKeyboardDays"), form.data().get("addKeyboardHours"), form.data().get("addKeyboardMinutes"));
            ConfigItem config = ConfigManager.getInstance().getConfig();
            config.addKeyboard(new KeyboardConfigItem(keyboardId, timeItem.getTimestamp()));
            ConfigModel.save(config);
            flash().clear();
            flash("success", "Keyboard association created successfully.");
            return redirect(routes.CmsStudyController.index());
        } catch (Exception e) {
            return onModifyKeyboardError("Invalid duration. Keyboard association could not be created.");
        }
    }

    public static class StudyDeleteKeyboard{}

    public static class StudyAddKeyboard{}

    public static class StudyConfig{
        private static void fillForm(ConfigItem config, Map<String, String> data){
            if(config == null){return;}
            data.put("studyName", config.getStudyName());
            data.put("infoTitle", config.getWelcomeTitle());
            data.put("infoMessage", config.getWelcomeMessage());
            if(config.showName()){
                data.put("showName", String.valueOf(config.showName()));
            }
            if(config.showEndDate()){
                data.put("showEndDate", String.valueOf(config.showEndDate()));
            }
            if(config.showStatistics()){
                data.put("showStatistics", String.valueOf(config.showStatistics()));
            }
            if(config.showKeyboardName()){
                data.put("showKeyboardName", String.valueOf(config.showKeyboardName()));
            }
            try {
                TimeItem timeItem = new TimeItem(config.getStudyDuration());
                data.put("studyDays", String.valueOf(timeItem.getDays()));
                data.put("studyHours", String.valueOf(timeItem.getHours()));
                data.put("studyMinutes", String.valueOf(timeItem.getMinutes()));
            } catch (Exception ignored) {}
        }
    }
}
