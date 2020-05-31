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
import com.fasterxml.jackson.annotation.JsonProperty;
import database.EventDatabase;
import database.KeyboardDatabase;
import database.MessagingDatabase;
import database.UserDatabase;
import org.apache.commons.lang3.StringUtils;
import utils.DateConverter;

import javax.persistence.*;
import java.util.*;

@Entity
public class User extends Model {


    /** The user model consists of attributes that should not change over the course of a study */

    @JsonProperty("uuid")
    @Id
    @Column(length = 190) // with utf8mb4 encoding one character needs up to 4 bytes, so with INNODB max. key length of 767 bytes we have to limit this property to 190 characters
    public String uuid;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("age")
    public int age;

    @JsonProperty("registrationTimestamp")
    private long registrationTimestamp;

    @JsonProperty("deviceManufacturer")
    public String deviceManufacturer;

    @JsonProperty("deviceModel")
    public String deviceModel;

    @JsonProperty("osVersion")
    public String osVersion;

    @JsonProperty("deviceScreenWidth")
    private int deviceScreenWidth;

    @JsonProperty("deviceScreenHeight")
    private int deviceScreenHeight;

    @JsonProperty("pushToken")
    @Column(columnDefinition = "TEXT")
    private String pushToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<KeyboardUserConfig> keyboards;

    public void update(User user) {
        gender = user.gender;
        age = user.age;
        deviceManufacturer = user.deviceManufacturer;
        deviceModel = user.deviceModel;
        deviceScreenWidth = user.deviceScreenWidth;
        deviceScreenHeight = user.deviceScreenHeight;
        pushToken = user.pushToken;
        update();
    }

    public void setKeyboards(List<KeyboardUserConfig> keyboards){
        this.keyboards = keyboards;
    }

    public String getPushToken(){
        return pushToken;
    }

    public List<KeyboardUserConfig> getKeyboards(){
        Collections.sort(keyboards);
        return keyboards;
    }

    public void resetRegistrationTimestamp(){
        registrationTimestamp = System.currentTimeMillis();
    }

    public String getUserId() {
        return uuid;
    }

    public long getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public boolean hasNewMessages(){
        return MessagingDatabase.hasNewMessagesForUserId(uuid);
    }

    public int getMessagesCount(){
        return MessagingDatabase.getMessagesCountForUserId(uuid);
    }

    public int getNewMessagesCount(){
        return MessagingDatabase.getNewMessagesCountForUserId(uuid);
    }

    public boolean isMale(){
        return StringUtils.equals(gender, "male");
    }

    public boolean isFemale(){
        return StringUtils.equals(gender, "female");
    }

    public int getEventsCount(){
        return EventDatabase.getTotalEventsCountForUser(uuid);
    }

    public long getLastActivityTimestamp(){
        return EventDatabase.getLastEventTimeForUser(uuid);
    }

    public String getLastActivityDateTime(){
        String timeStamp = DateConverter.getDateTimeStamp(getLastActivityTimestamp());
        return StringUtils.isEmpty(timeStamp) ? "-" : timeStamp;
    }

    public String getStudyStartDateTime() {
        String date = DateConverter.getDateTimeStamp(registrationTimestamp);
        if(StringUtils.isEmpty(date)){return "-";}
        return date;
    }

    public String getStudyEndDateTime(){
        String date = DateConverter.getDateTimeStamp(UserDatabase.getStudyEndDate(uuid));
        if(StringUtils.isEmpty(date)){return "-";}
        return date;
    }

    public String getStudyInterval(){
        String start = DateConverter.getDateStamp(registrationTimestamp);
        if(StringUtils.isEmpty(start)){return "-";}
        String end = DateConverter.getDateStamp(UserDatabase.getStudyEndDate(uuid));
        if(StringUtils.isEmpty(end)){return "-";}
        return start + " bis " + end;
    }

    public String getCurrentKeyboardName(){
        KeyboardUserConfig config = getCurrentKeyboardConfig();
        if(config == null){return "-";}
        String name = config.getLayoutName();
        return StringUtils.isEmpty(name) ? "-" : name;
    }

    public KeyboardUserConfig getCurrentKeyboardConfig(){
        return KeyboardDatabase.getActiveKeyboardUserConfig(getKeyboards());
    }

    public String getDensity(){
        if(deviceScreenHeight == 0 || deviceScreenWidth == 0){
            return "";
        }
        return deviceScreenWidth + "x" + deviceScreenHeight;
    }

    public void addKeyboardConfig(KeyboardUserConfig config) {
        keyboards.add(config);
        update();
    }

    public void removeKeyboardConfig(long configId) {
        if(keyboards == null){
            return;
        }
        Iterator<KeyboardUserConfig> configIt = keyboards.iterator();
        while(configIt.hasNext()){
            KeyboardUserConfig config = configIt.next();
            if(Objects.equals(config.getId(), configId)){
                configIt.remove();
            }
        }
        update();
    }

    public boolean containsLayoutId(String layoutId){
        if(keyboards == null){
            return false;
        }
        for(KeyboardUserConfig config : keyboards){
            if(config.hasLayoutId(layoutId)){
                return true;
            }
        }
        return false;
    }

}