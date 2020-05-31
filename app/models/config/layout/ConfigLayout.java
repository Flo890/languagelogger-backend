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

package models.config.layout;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ConfigLayout {

    @JsonProperty("name")
    private String name;

    @JsonProperty("defaultAlphabetLayout")
    private ConfigLayoutDefinition defaultAlphabetLayout;

    @JsonProperty("altAlphabetLayouts")
    private List<ConfigLayoutDefinition> altAlphabetLayouts;

    @JsonProperty("symbolsLayout")
    private ConfigLayoutDefinition symbolsLayout;

    @JsonProperty("symbolsShiftedLayout")
    private ConfigLayoutDefinition symbolsShiftedLayout;

    @JsonProperty("isTrackingEnabled")
    private boolean isTrackingEnabled;

    @JsonProperty("showHandPrompt")
    private boolean showHandPrompt;

    @JsonProperty("anonymizeInputEvents")
    private boolean anonymizeInputEvents;

    @JsonProperty("infoTitle")
    private String infoTitle;

    @JsonProperty("infoMessage")
    private String infoMessage;

    public String getName(){
        return name;
    }

    public ConfigLayoutDefinition getDefaultAlphabetLayout(){
        return defaultAlphabetLayout;
    }

    public List<ConfigLayoutDefinition> getAltAlphabetLayouts(){
        return altAlphabetLayouts;
    }

    public ConfigLayoutDefinition getSymbolsLayout() {
        return symbolsLayout;
    }

    public ConfigLayoutDefinition getSymbolsShiftedLayout() {
        return symbolsShiftedLayout;
    }

    public boolean isTrackingEnabled(){
        return isTrackingEnabled;
    }

    public boolean showHandPrompt(){
        return showHandPrompt;
    }

    public String getInfoTitle(){
        return infoTitle;
    }

    public String getInfoMessage(){
        return infoMessage;
    }

    public boolean anonymizeInputEvents(){
        return anonymizeInputEvents;
    }
}