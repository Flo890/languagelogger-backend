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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.ConfigManager;
import database.KeyboardDatabase;
import database.UserDatabase;
import models.KeyboardClientModel;
import models.KeyboardUserConfig;
import models.Message;
import models.User;
import org.apache.commons.codec.binary.StringUtils;
import play.Configuration;
import play.Logger;
import play.api.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.PushSender;

import java.util.List;

public class KeyboardController extends Controller {

    public Result getConfig() {
        Configuration config = Play.current().injector().instanceOf(Configuration.class);
        JsonNode configNode = Json.toJson(config.getObject("config.client"));
        System.out.println("GET /api/config");
        return ok(configNode);
    }

    public Result getLayout(String userId) {
        User user = UserDatabase.getUserById(userId);
        if (user == null) {
            return badRequest("No user available for that id");
        }

        List<KeyboardUserConfig> keyboards = user.getKeyboards();
        KeyboardUserConfig activeKeyboardConfig = KeyboardDatabase.getActiveKeyboardUserConfig(keyboards);
        String activeKeyboardId = KeyboardDatabase.getActiveKeyboardId(activeKeyboardConfig);
        if (isRequestFromApp()) {
            checkForLayoutUpdate(userId, activeKeyboardId, keyboards);
        }
        ObjectNode node = Json.newObject();
        node.put("enabledKeyboardId", activeKeyboardId);
        node.set("keyboards", getKeyboardsArray(activeKeyboardConfig));
        return ok(node);
    }

    /* The API exposes only the active item at the moment because the client should not show the layout change actively
     * However the array structure ensures a very dynamic description of the keyboard layout timeline  */

    private ArrayNode getKeyboardsArray(KeyboardUserConfig config) {
        ArrayNode keyboardsArray = Json.newArray();
        if(config != null){
            try {
                KeyboardClientModel model = new KeyboardClientModel(config);
                keyboardsArray.add(Json.toJson(model));
            } catch (InstantiationException e) {
                Logger.error("error loading keyboard model", e);
            }
        }
        return keyboardsArray;
    }

    /* Could be used for exposing the keyboard layout timeline */

    /*
    private ArrayNode getKeyboardsArray(List<KeyboardUserConfig> keyboardsConfig){
        ArrayNode keyboardsArray = Json.newArray();
        for(KeyboardUserConfig config : keyboardsConfig){
            try {
                KeyboardClientModel model = new KeyboardClientModel(config);
                keyboardsArray.add(Json.toJson(model));
            } catch (InstantiationException e) {
                Logger.error("error loading keyboard model",e);
            }
        }
        return keyboardsArray;
    }
    */

    private void checkForLayoutUpdate(String userId, String newLayoutId, List<KeyboardUserConfig> keyboards) {
        String oldLayoutId = request().getQueryString("layoutId");
        if (!StringUtils.equals(newLayoutId, oldLayoutId)) {
            onKeyboardChanged(userId, newLayoutId, keyboards);
        }
    }

    private void onKeyboardChanged(String userId, String newLayoutId, List<KeyboardUserConfig> keyboards) {
        keyboards.stream().filter(config ->
                config.hasLayoutId(newLayoutId) && config.hasInfoMessage()).forEach(
                config -> sendKeyboardChangedMessage(userId, config));
    }

    private void sendKeyboardChangedMessage(String userId, KeyboardUserConfig config) {
        if(! config.hasInfoMessage() || config.hasSentLayoutMessage()  /*config already transmitted*/){
            return;
        }
        UserDatabase.setKeyboardConfigAsSent(userId, config.getId());
        Message message = new Message(config.getInfoTitle(), config.getInfoMessage(), userId);
        message.save();
        PushSender.pushMessageToDevice(message);
    }

    private boolean isRequestFromApp() {
        String userAgent = request().getHeader("User-Agent");
        return StringUtils.equals(userAgent, ConfigManager.getInstance().getAppAgent());
    }
}