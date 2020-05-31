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

package config;

import models.config.ConfigItem;
import models.config.ConfigItemBuilder;
import models.config.layout.ConfigLayoutContainer;
import play.Configuration;
import play.api.Play;

public class ConfigManager {

    private static ConfigManager instance;
    private ConfigItem configItem;
    private ConfigLayoutContainer layouts;

    public static synchronized ConfigManager getInstance(){
        if(instance == null){
            instance = new ConfigManager();
        }
        return instance;
    }

    private ConfigManager(){}

    public ConfigItem getConfig(){
        if(configItem == null){
            configItem = ConfigItemBuilder.build();
        }
        return configItem;
    }

    public ConfigLayoutContainer getLayouts(){
        if(layouts == null){
            layouts = ConfigLayoutContainer.get();
        }
        return layouts;
    }

    public String getAppAgent(){
        Configuration config = Play.current().injector().instanceOf(Configuration .class);
        return config.getString("config.app.agent");
    }
}
