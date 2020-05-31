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

package models.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import database.KeyboardDatabase;
import models.KeyboardLayout;
import utils.TimeItem;

public class KeyboardConfigItem {

    @JsonProperty("id")
    private String id;

    @JsonProperty("duration")
    private long duration;

    public KeyboardConfigItem(){}

    public KeyboardConfigItem(String id, long duration){
        this.id = id;
        this.duration = duration;
    }

    public long getDuration(){
        return duration;
    }

    public String getId(){
        return id;
    }

    @JsonIgnoreProperties
    public String linkLayoutName(){
        KeyboardLayout layout = KeyboardDatabase.getKeyboardLayout(id);
        return layout == null ? "" : layout.getName();
    }

    public String calcDuration(){
        try {
            return new TimeItem(duration).toString();
        } catch (Exception e) {
            return "";
        }
    }
}
