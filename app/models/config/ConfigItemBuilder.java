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
import com.fasterxml.jackson.databind.JsonNode;
import play.Configuration;
import play.api.Play;
import play.libs.Json;

public class ConfigItemBuilder {

    public static ConfigItem build(){
        ConfigModel model = Ebean.find(ConfigModel.class).findUnique();
        if(model == null){
            ConfigItem item = createFromLocalConfig();
            ConfigModel.save(item);
            return item;
        }
        return create(Json.parse(model.getData()));
    }

    private static ConfigItem createFromLocalConfig(){
        Configuration config = Play.current().injector().instanceOf(Configuration.class);
        return create(Json.toJson(config.getObject("config.research")));
    }

    private static ConfigItem create(JsonNode json){
        return Json.fromJson(json, ConfigItem.class);
    }
}
