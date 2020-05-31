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
import database.MessagingDatabase;
import database.UserDatabase;
import models.KeyboardLayout;
import models.KeyboardUserConfig;
import models.Message;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.PushSender;
import utils.UserSortType;
import views.html.cms_error;
import views.html.cms_user;
import views.html.cms_users;

import java.util.Collections;
import java.util.List;

public class CmsUserController extends Controller {

    @Inject
    private FormFactory formFactory;

    @Security.Authenticated(CmsSecured.class)
    public Result getUsers() {
        List<User> users = UserDatabase.getUsers();
        UserSortType sortType = UserSortType.createFromAbbreviation(request().getQueryString("sort"));
        if(sortType == null){
            sortType = UserSortType.DEFAULT;
        }
        Collections.sort(users, sortType.getComparator());
        return ok(cms_users.render(users, UserSortType.values(), sortType).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result index(String userId) {
        User user = UserDatabase.getUserById(userId);
        if(user == null){
            return internalServerError(cms_error.render("invalid user id"));
        }
        List<Message> messages = MessagingDatabase.getMessagesForUserId(userId);
        List<KeyboardUserConfig> keyboardConfigs = user.getKeyboards();
        KeyboardUserConfig activeKeyboard = user.getCurrentKeyboardConfig();
        List<KeyboardLayout> keyboardLayouts = KeyboardDatabase.getKeyboards();
        return ok(cms_user.render(user, activeKeyboard, keyboardConfigs, keyboardLayouts, messages).body().trim()).as("text/html; charset=utf-8");
    }

    @Security.Authenticated(CmsSecured.class)
    public Result deleteUser(){
        Form<DeleteUser> form = formFactory.form(DeleteUser.class).bindFromRequest();
        String userId = form.data().get("userId");
        User user = UserDatabase.getUserById(userId);
        if(user == null){
            return errorRedirect(userId, "The user could not be deleted.");
        }
        try{
            user.delete();
            flash().clear();
            flash("success", "User deleted successfully.");
            return redirect(routes.CmsUserController.getUsers());
        }catch (Exception e){
            return errorRedirect(userId, "The user could not be deleted from database.");
        }
    }

    @Security.Authenticated(CmsSecured.class)
    public Result addKeyboardForUser(){
        Form<KeyboardAddConfig> form = formFactory.form(KeyboardAddConfig.class).bindFromRequest();
        String userId = form.data().get("userId");
        String layoutId = form.data().get("layoutId");
        if(StringUtils.isEmpty(layoutId)){
            return errorRedirect(userId, "Invalid keyboard layout.");
        }
        String dateText = form.data().get("date");
        if(StringUtils.isEmpty(dateText)){
            return errorRedirect(userId, "Invalid date.");
        }
        long dateMillis;
        try{
            dateMillis = Long.parseLong(dateText);
        }catch(Exception e){
            return errorRedirect(userId, "Invalid date.");
        }
        KeyboardLayout layout = KeyboardDatabase.getKeyboardLayout(layoutId);
        if(layout == null){
            return errorRedirect(userId, "Invalid keyboard layout.");
        }
        User user = UserDatabase.getUserById(userId);
        if(user == null){
            return errorRedirect(userId, "Invalid user.");
        }

        try{
            user.addKeyboardConfig(new KeyboardUserConfig(dateMillis, layout));
            PushSender.sendUpdatePushToDevice(user.getPushToken());
            flash().clear();
            flash("success", "Keyboard layout associated.");
            return redirect(routes.CmsUserController.index(userId));
        }catch(Exception e){
            return errorRedirect(userId, "Keyboard layout could not be saved to database.");
        }
    }

    @Security.Authenticated(CmsSecured.class)
    public Result deleteKeyboardForUser(){
        Form<KeyboardDeleteConfig> form = formFactory.form(KeyboardDeleteConfig.class).bindFromRequest();
        String userId = form.data().get("userId");
        String configId = form.data().get("configId");
        if(StringUtils.isEmpty(configId)){
            return errorRedirect(userId, "Invalid keyboard layout.");
        }
        User user = UserDatabase.getUserById(userId);
        if(user == null){
            return errorRedirect(userId, "Invalid user.");
        }

        long configDBId;
        try{
            configDBId = Long.parseLong(configId);
        }catch(Exception e){
            return errorRedirect(userId, "Invalid keyboard layout.");
        }
        user.removeKeyboardConfig(configDBId);
        PushSender.sendUpdatePushToDevice(user.getPushToken());
        flash().clear();
        flash("success", "Removed association of keyboard layout.");
        return redirect(routes.CmsUserController.index(userId));
    }

    @Security.Authenticated(CmsSecured.class)
    public Result sendMessage(){
        Form<MessageCreate> form = formFactory.form(MessageCreate.class).bindFromRequest();
        String userId = form.data().get("userId");
        String title = form.data().get("title");
        if(StringUtils.isEmpty(title)){
            return errorRedirect(userId, "No title - message could not be sent.");
        }
        String text = form.data().get("text");
        if(StringUtils.isEmpty(text)){
            return errorRedirect(userId, "No message text - message could not be sent.");
        }

        Message message = new Message(title, text, userId);
        boolean hasPushedMessage = MessagingDatabase.onMessageReceived(message);
        if(hasPushedMessage){
            flash().clear();
            flash("success", "Message sent successfully.");
        }else{
            flash().clear();
            flash("warning", "User could not be notified via push. The message was saved and will be shown once the user opens the app.");
        }
        return redirect(routes.CmsUserController.index(userId));
    }

    @Security.Authenticated(CmsSecured.class)
    public Result deleteMessage(){
        Form<MessageDelete> form = formFactory.form(MessageDelete.class).bindFromRequest();
        String userId = form.data().get("userId");
        String messageIdText = form.data().get("messageId");
        if(StringUtils.isEmpty(messageIdText)){
            flash().clear();
            flash("error", "Message could not be found.");
        }else{
            try{
                MessagingDatabase.removeMessage(Long.valueOf(messageIdText));
                flash().clear();
                flash("success", "Message deleted successfully.");
            }catch(Exception e){
                flash().clear();
                flash("error", "Message could not be found.");
            }
        }
        return redirect(routes.CmsUserController.index(userId));
    }

    @Security.Authenticated(CmsSecured.class)
    private Result errorRedirect(String userId, String message){
        flash().clear();
        flash("error", message);
        return redirect(routes.CmsUserController.index(userId));
    }

    public static class KeyboardAddConfig {}
    public static class KeyboardDeleteConfig{}
    public static class DeleteUser{}
    public static class MessageDelete{}
    public static class MessageCreate{}
}
