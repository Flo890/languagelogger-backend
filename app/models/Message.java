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

package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import utils.DateConverter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message extends Model {

    private static final int PUSH_MAX_TITLE_BYTE_LENGTH = 100;
    private static final int PUSH_MAX_MESSAGE_BYTE_LENGTH = 1700;

    @Id
    @GeneratedValue
    public Long id;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("title")
    public String title;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("message")
    public String message;

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("isUserMessage")
    private boolean isUserMessage;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String userId;

    private boolean isNew;

    public Message(String title, String message, String userId){
        this.title = title;
        this.message = message;
        this.isUserMessage = false;
        this.timestamp = System.currentTimeMillis();
        this.userId = userId;
    }

    @Override
    public void save() {
        if(hasText()){
            super.save();
        }
    }

    public boolean hasText(){
        return StringUtils.isNotBlank(title) || StringUtils.isNotBlank(message);
    }

    public void setTimestampToNow(){
        this.timestamp = System.currentTimeMillis();
    }

    @JsonIgnore
    public boolean isUserMessage(){
        return isUserMessage;
    }

    @JsonIgnore
    public String getUserId() {
        return userId;
    }

    public void setNew(boolean isNew){
        this.isNew = isNew;
    }

    @JsonIgnore
    public boolean isNewMessage(){
        return isNew;
    }

    @JsonIgnore
    public String getTimestampText(){
        return DateConverter.getDateTimeStamp(timestamp);
    }

    public boolean hasTitle(){
        return StringUtils.isNotBlank(title);
    }

    public void shortenMessageForPush(){
        if(getTextLength(title) > PUSH_MAX_TITLE_BYTE_LENGTH){
            title = shortenText(title, PUSH_MAX_TITLE_BYTE_LENGTH) + "...";
        }
        if(getTextLength(message) > PUSH_MAX_MESSAGE_BYTE_LENGTH){
            message = shortenText(message, PUSH_MAX_MESSAGE_BYTE_LENGTH) + "...\n\nNachricht noch nicht vollständig geladen.";
        }
    }

    private int getTextLength(String text){
        try {
            return text == null ? 0 : text.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            return 0;
        }
    }

    private String shortenText(String text, int length){
        try {
            byte[] bytes = Arrays.copyOf(text.getBytes("UTF-8"), length);
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return StringUtils.abbreviate(text, length);
    }
}