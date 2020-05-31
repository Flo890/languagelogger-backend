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
import play.libs.Json;
import utils.DateConverter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Event extends Model{

    /** --- general ------------------------ **/

    @Id
    Long id;

    @JsonProperty("type")
    public String type;

    @JsonProperty("timestamp")
    public long timestamp;

    @JsonProperty("userUuid")
    public String userUuid;

    @JsonProperty("keyboardStateUuid")
    public String keyboardStateUuid;

    @JsonProperty("handPosture")
    public String handPosture;

    @JsonProperty("inputMode")
    public String inputMode;

    @JsonProperty("fieldPackageName")
    public String fieldPackageName;

    @JsonProperty("fieldId")
    public Integer fieldId;

    @JsonProperty("fieldHintText")
    public String fieldHintText;

    @JsonProperty("anonymized")
    public boolean anonymized;

    @JsonProperty("sensors")
    public transient Map<Integer, float[]> sensors = new HashMap<>();

    @Column(name = "sensors")
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    public List<Sensor> persistentSensors = new ArrayList<>();


    /** --- auto correct ------------------- **/

    @JsonProperty("autoCorrectBefore")
    public String autoCorrectBefore;

    @JsonProperty("autoCorrectAfter")
    public String autoCorrectAfter;

    @JsonProperty("autoCorrectBeforeLength")
    public Integer autoCorrectBeforeLength;

    @JsonProperty("autoCorrectAfterLength")
    public Integer autoCorrectAfterLength;

    @JsonProperty("autoCorrectOffset")
    public Integer autoCorrectOffset;

    @JsonProperty("autoCorrectLevenshteinDistance")
    public Integer autoCorrectLevenshteinDistance;


    /** --- content change ----------------- **/

    @JsonProperty("content")
    public String content;

    @JsonProperty("contentLength")
    public Integer contentLength;


    /** --- suggestion picked -------------- **/

    @JsonProperty("suggestion")
    public String suggestion;

    @JsonProperty("suggestionLength")
    public String suggestionLength;

    /** --- private mode -------------- **/

    @JsonProperty("privateMode")
    public Boolean privateMode;

    @JsonProperty("cause")
    public String cause;

    /** --- touch -------------------------- **/

    @JsonProperty("code")
    public String code;

    @JsonProperty("x")
    public Integer x;

    @JsonProperty("y")
    public Integer y;

    @JsonProperty("pressure")
    public Double pressure;

    @JsonProperty("size")
    public Double size;

    /** ------------------------------------ **/

    @Override
    public String toString() {
        return Json.toJson(this).toString();
    }

    public Map<Integer, float[]> getSensors() {
        return sensors;
    }

    public void addSensor(Sensor sensor) {
        this.persistentSensors.add(sensor);
    }

    public List<Sensor> getPersistentSensors() {
        return persistentSensors;
    }

    public String getType(){
        return type;
    }

    /** CMS converters **/
    public String getDayStamp(){
        return DateConverter.getDateStamp(timestamp);
    }

    public String getTimeStamp(){
        return DateConverter.getTimeStamp(timestamp);
    }

    public boolean isInPrivateMode(){
        return privateMode != null && privateMode;
    }

    public String getShortUserId(){
        if(userUuid == null){return "";}
        if(userUuid.length() < 8){return userUuid;}
        return userUuid.substring(0, 8);
    }
}
