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

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.AbstractedActionEvent;
import models.Message;
import models.MessageStatistics;
import models.WordFrequency;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.JsonUtils;

import javax.persistence.PersistenceException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Created by ResearchIME Project on 10.12.16.
 */
public abstract class JsonPostController<T extends Model> extends Controller {

    public Result processRequestArrayData(String arrayKeyName){
        JsonNode json = JsonUtils.jsonFromPostRequest(request());
        if (json == null){
            Logger.warn("bad request");
            return badRequest("request is empty or has wrong format");
        }

        JsonNode jsonArray = json.get(arrayKeyName);
        if (jsonArray == null || !jsonArray.isArray()){
            Logger.warn("json doesn't contain the array named" + arrayKeyName);
            return badRequest("request must contain array " + arrayKeyName);
        }

        new Thread(() -> {
            for(JsonNode jsonItem : jsonArray){
                storeMessage(jsonItem);
            }
        }).start();

        return ok();
    }

    public Result processRequestData() {
        JsonNode json = JsonUtils.jsonFromPostRequest(request());
        if (json == null){
            Logger.warn("bad request");
            return badRequest("request is empty or has wrong format");
        }
        storeMessage(json);
        return ok();
    }

    private boolean storeMessage(JsonNode jsonMessage){
        if (getClassType() == MessageStatistics.class || getClassType() == WordFrequency.class) {
            if (jsonMessage.has("id")) {
                ((ObjectNode) jsonMessage).remove("id");
            }
        }
        T object = Json.fromJson(jsonMessage, getClassType());
        if (object == null){
            Logger.warn("could not parse data: " + jsonMessage.toString());
            return false;
        }
        else{
            if (getClassType() == WordFrequency.class){
                // allow updating for WordFreqeuncy
                saveOrUpdateObject(object);
            }
            else {
                if (getClassType() == AbstractedActionEvent.class){
                    AbstractedActionEvent aee = (AbstractedActionEvent) object;
                    if (aee.isRegexMatcherEvent()){
                        JsonNode eventJson = Json.parse(aee.getEventJson());
                        if(eventJson.has("rawContentAfter")){
                            char[] emojiChars = eventJson.get("rawContentAfter").asText().toCharArray();
                            ArrayNode asciiArray = ((ObjectNode)eventJson).putArray("rawContentAfter");
                            for(int i = 0; i<emojiChars.length; i++){
                                asciiArray.add((int) emojiChars[i]);
                            }
                            aee.setEventJson(eventJson.toString());
                        }
                        if(eventJson.has("rawContentBefore")){
                            char[] emojiChars = eventJson.get("rawContentBefore").asText().toCharArray();
                            ArrayNode asciiArray = ((ObjectNode)eventJson).putArray("rawContentBefore");
                            for(int i = 0; i<emojiChars.length; i++){
                                asciiArray.add((int) emojiChars[i]);
                            }
                            aee.setEventJson(eventJson.toString());
                        }
                    }
                }
                // for others, no data may be overwritten
                saveObject(object);
            }
        }
        return true;
    }

    protected void saveObject(T item){
        item.save();
    }

    protected void saveOrUpdateObject(T item){
        // custom implementation of update criteria per item type
        if (WordFrequency.class.isInstance(item)) {
            WordFrequency wordFrequency = (WordFrequency) item;
            WordFrequency wordFrequencyItemExisting = Ebean.find(WordFrequency.class).where()
                    .eq("user_uuid", wordFrequency.getUserUuid())
                    .eq("word", wordFrequency.getWord().toLowerCase())
                    .eq("logical_word_list_id", wordFrequency.getLogicalWordListId())
            .findUnique();

            if (wordFrequencyItemExisting == null){
                wordFrequencyItemExisting = Ebean.find(WordFrequency.class).where()
                        .eq("id", wordFrequency.getId()).findUnique();
                if (wordFrequencyItemExisting != null && !wordFrequencyItemExisting.getWord().toLowerCase().equals(wordFrequency.getWord().toLowerCase())){
                    throw new IllegalArgumentException("the existing wordFreq in db (selected by id "+wordFrequency.getId()+") has a different word "+wordFrequencyItemExisting.getWord().toLowerCase()+" than the new one: "+wordFrequency.getWord().toLowerCase());
                }
            }

            if (wordFrequencyItemExisting != null) {
                wordFrequencyItemExisting.setCount(wordFrequency.getCount());
                wordFrequencyItemExisting.update();
            }
            else {
                wordFrequency.save();
            }
        }
        else {
            throw new IllegalArgumentException("updating database items is not implemented for items of type "+item.getClass().getName());
        }
    }

    protected abstract Class<T> getClassType();
}
