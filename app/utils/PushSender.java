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

package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pushraven.Notification;
import com.pushraven.Pushraven;
import database.UserDatabase;
import models.Message;
import play.Configuration;
import play.Logger;
import play.api.Play;
import play.libs.Json;

public class PushSender {

    private static final String INFO_MESSAGE_TYPE = "message";
    private static final String UPDATE_CONFIG_TYPE = "updateConfig";

    public static boolean sendUpdatePushToDevice(String token){
        if(token != null){
            return PushSender.pushMessageToDevice(token, PushSender.UPDATE_CONFIG_TYPE);
        }
        return false;
    }

    public static boolean pushMessageToDevice(Message message){
        if(! message.hasText()){
            return false;
        }

        String pushToken = UserDatabase.getPushToken(message.getUserId());
        if(pushToken != null){
            //Logger.info("send push message to device");
            message.shortenMessageForPush();
            return PushSender.pushMessageToDevice(pushToken, INFO_MESSAGE_TYPE, Json.toJson(message));
        }else{
            Logger.warn("no push token available for the user");
        }
        return false;
    }

    public static boolean pushMessageToDevice(String token, String type){
        return pushMessageToDevice(token, type, null);
    }

    public static boolean pushMessageToDevice(String token, String type, JsonNode message) {
        ObjectNode payload = Json.newObject();
        payload.put("type", type);
        payload.set("data", message);
        return pushPayloadToDevice(token, payload.toString());
    }

    private static boolean pushPayloadToDevice(String token, String payload){
        Configuration config = Play.current().injector().instanceOf(Configuration .class);
        String serverToken = config.getString("firebase.server.token");

        Pushraven.setKey(serverToken);
        Notification notification = new Notification();
        notification.body(payload).to(token);
        return Pushraven.push(notification).getResponseCode() == 200;
    }
}
