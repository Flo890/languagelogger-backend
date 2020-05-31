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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class KeyboardClientModel {

    @JsonProperty("startDate")
    private long startDate;

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private String id;

    @JsonProperty("defaultAlphabetLayout")
    private KeyboardDefinition defaultLayout;

    @JsonProperty("altAlphabetLayouts")
    private List<KeyboardDefinition> altLayouts;

    @JsonProperty("symbolsLayout")
    private KeyboardDefinition symbolsLayout;

    @JsonProperty("symbolsShiftedLayout")
    private KeyboardDefinition symbolsShiftedLayout;

    @JsonProperty("isTrackingEnabled")
    private boolean isTrackingEnabled;

    @JsonProperty("showHandPosture")
    private boolean showHandPosture;

    public KeyboardClientModel(KeyboardUserConfig config) throws InstantiationException{
        startDate = config.getStartDate();
        KeyboardLayout layout = config.getLayout();
        if(layout == null){
            throw new InstantiationException("keyboard layout relation missing");
        }
        defaultLayout = layout.getDefaultAlphabetLayout();
        if(defaultLayout == null){
            throw new InstantiationException("no default layout available");
        }
        id = layout.getLayoutId();
        if(id == null){
            throw new InstantiationException("keyboard layout id missing");
        }
        altLayouts = layout.getAltAlphabetLayouts();
        symbolsLayout = layout.getSymbolsLayout();
        symbolsShiftedLayout = layout.getSymbolsShiftedLayout();
        name = layout.getName();
        isTrackingEnabled = layout.isTrackingEnabled();
        showHandPosture = layout.showHandPrompt();
    }
}
