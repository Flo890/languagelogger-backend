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
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.ConfigManager;
import database.EventDatabase;
import database.MessagingDatabase;
import database.UserDatabase;
import models.Message;
import models.config.ConfigItem;
import play.libs.Json;
import play.mvc.Result;

public class MessagingController extends JsonPostController<Message> {

    @Override
    protected Class<Message> getClassType() {
        return Message.class;
    }

    public Result getDashboard(String userId) {
        ObjectNode root = Json.newObject();
        root.set("statistics", getStatistics(userId));
        root.set("messages", getMessages(userId));
        return ok(root);
    }

    public Result postMessage() {
        return processRequestData();
    }

    @Override
    protected void saveObject(Message message) {
        MessagingDatabase.onMessageReceived(message);
    }

    private JsonNode getStatistics(String userId) {
        ObjectNode json = Json.newObject();
        ConfigItem config = ConfigManager.getInstance().getConfig();
        if(config.showName()){
            json.put("studyName", config.getStudyName());
        }
        if(config.showEndDate()){
            json.put("studyEndDate", UserDatabase.getStudyEndDate(userId));
        }
        if(config.showStatistics()){
            json.put("totalEventsCount", EventDatabase.getTotalEventsCount());
            json.put("userEventsCount", EventDatabase.getTotalEventsCountForUser(userId));
        }
        if(config.showKeyboardName()){
            json.put("currentLayoutName", UserDatabase.getCurrentLayoutName(userId));
        }
        return json;
    }

    private JsonNode getMessages(String userId){
        return Json.toJson(MessagingDatabase.getMessagesForUserId(userId));
    }

    public Result changeMessageStatus(){
        try{
            Long messageId = Long.valueOf(request().getQueryString("id"));
            boolean isNew = Boolean.valueOf(request().getQueryString("isNew"));
            MessagingDatabase.setIsMessageNew(messageId, isNew);
            return ok();
        }catch (Exception e){
            return internalServerError();
        }
    }
}
