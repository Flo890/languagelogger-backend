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

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.libs.Json;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by ResearchIME Project
 */
@Entity
public class ConfigModel extends Model {

    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("data")
    private String data;

    public static void save(ConfigItem item){
        ConfigModel model = Ebean.find(ConfigModel.class).findUnique();
        if(model != null){
            model.delete();
        }
        model = new ConfigModel();
        model.data = Json.toJson(item).toString();
        model.save();
    }

    public String getData() {
        return data;
    }
}
