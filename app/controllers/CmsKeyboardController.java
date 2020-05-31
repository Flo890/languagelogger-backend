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
import database.KeyboardDatabase;
import database.UserDatabase;
import models.KeyboardLayout;
import models.config.KeyboardLayoutHelper;
import org.apache.commons.lang3.StringUtils;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.PushSender;
import views.html.cms_error;
import views.html.cms_keyboard_settings;
import views.html.cms_keyboards;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CmsKeyboardController extends Controller {

    @Inject
    private FormFactory formFactory;

    @Security.Authenticated(CmsSecured.class)
    public Result index() {
        List<KeyboardLayout> layouts = KeyboardDatabase.getKeyboards();
        return ok(cms_keyboards.render(layouts).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result getKeyboardDetails(String id) {
        Form<KeyboardAddConfig> form = formFactory.form(KeyboardAddConfig.class);
        KeyboardLayout layout = KeyboardDatabase.getKeyboardLayout(id);
        if(layout == null){
            return internalServerError(cms_error.render("invalid layout id"));
        }
        KeyboardAddConfig.fillForm(layout, form.data());
        return ok(cms_keyboard_settings.render(form, false).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result createKeyboard() {
        Form<KeyboardAddConfig> config = formFactory.form(KeyboardAddConfig.class);
        //default values
        config.data().put("showHandPrompt", "off");
        config.data().put("trackingEnabled", "on");
        config.data().put("anonymizeInputEvents", "on");
        config.data().put("layoutJson", KeyboardLayoutHelper.getDefaultJson());
        return ok(cms_keyboard_settings.render(config, true).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result updateKeyboard() {
        Form<KeyboardAddConfig> form = formFactory.form(KeyboardAddConfig.class).bindFromRequest();
        String id = form.data().get("keyboardId");
        boolean isNewLayout = StringUtils.isBlank(id);
        String name = form.data().get("keyboardName");
        if(StringUtils.isEmpty(name)){
            return onKeyboardUpdateError("Keyboard name is not valid.", form, isNewLayout);
        }
        String layoutJson = form.data().get("layoutJson");
        if(StringUtils.isEmpty(layoutJson)){
            return onKeyboardUpdateError("Layout-JSON is not valid.", form, isNewLayout);
        }

        String infoTitle = form.data().get("infoTitle");
        String infoMessage = form.data().get("infoMessage");

        boolean showHandPrompt  = StringUtils.equals("on", form.data().get("showHandPrompt"));
        boolean trackingEnabled  = StringUtils.equals("on", form.data().get("trackingEnabled"));
        boolean anonymizeInputEvents  = StringUtils.equals("on", form.data().get("anonymizeInputEvents"));

        try {
            KeyboardLayout existingLayout = KeyboardDatabase.getKeyboardLayout(id);
            KeyboardLayout layout = KeyboardLayout.createLayout(name, layoutJson, trackingEnabled, showHandPrompt, anonymizeInputEvents, infoTitle, infoMessage);
            if(existingLayout == null){
                layout.save();
            }else{
                existingLayout.update(layout);
                sendKeyboardUpdatePush(id);
            }

            flash().clear();
            flash("success", "Keyboard layout saved successfully.");
            return redirect(routes.CmsKeyboardController.index());
        } catch (Exception e) {
            //Logger.warn("error creating keyboard", e);
            return onKeyboardUpdateError("Layout-JSON is not valid.", form, isNewLayout);
        }
    }

    private void sendKeyboardUpdatePush(String layoutId){
        Collection<String> tokens = UserDatabase.getUserPushTokenWithKeyboardLayoutId(layoutId);
        tokens.forEach(PushSender::sendUpdatePushToDevice);
    }

    @Security.Authenticated(CmsSecured.class)
    public Result onKeyboardUpdateError(String error, Form<KeyboardAddConfig> form, boolean isNewKeyboard){
        flash().clear();
        flash("error", error);
        return internalServerError(cms_keyboard_settings.render(form, isNewKeyboard).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result deleteKeyboard() {
        Form<KeyboardDeleteConfig> form = formFactory.form(KeyboardDeleteConfig.class).bindFromRequest();
        String id = form.data().get("keyboardId");
        if(StringUtils.isEmpty(id)){
            flash().clear();
            flash("error", "Keyboard layout could not be deleted.");
            return redirect(routes.CmsKeyboardController.getKeyboardDetails(id));
        }
        if(KeyboardDatabase.deleteKeyboard(id)){
            if(KeyboardDatabase.isKeyboardLayoutDBEmpty()){
                flash().clear();
                flash("success", "All keyboard layouts successfully deleted. The standard layouts got restored.");
            }else{
                flash().clear();
                flash("success", "Keyboard layout successfully deleted.");
            }
            return redirect(routes.CmsKeyboardController.index());
        }else{
            flash().clear();
            flash("error", "The keyboard could not be removed from database.");
            return redirect(routes.CmsKeyboardController.getKeyboardDetails(id));
        }
    }

    public static class KeyboardDeleteConfig {}

    public static class KeyboardAddConfig {

        private static void fillForm(KeyboardLayout layout, Map<String, String> data){
            if(layout == null){return;}
            data.put("keyboardId", layout.getLayoutId());
            data.put("keyboardName", layout.getName());
            data.put("infoTitle", layout.getInfoTitle());
            data.put("infoMessage", layout.getInfoMessage());
            data.put("layoutJson", layout.getFormJson());
            if(layout.showHandPrompt()){
                data.put("showHandPrompt", String.valueOf(layout.showHandPrompt()));
            }
            if(layout.isTrackingEnabled()){
                data.put("trackingEnabled", String.valueOf(layout.isTrackingEnabled()));
            }
            if(layout.isAnonymizeInputEvents()){
                data.put("anonymizeInputEvents", String.valueOf(layout.isAnonymizeInputEvents()));
            }
        }
    }
}
