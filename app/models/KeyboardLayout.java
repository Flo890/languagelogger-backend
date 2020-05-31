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
import models.config.KeyboardLayoutHelper;
import models.config.layout.ConfigLayout;
import org.apache.commons.lang3.StringUtils;
import utils.KeyboardDefinitionType;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class KeyboardLayout extends Model {

    @Id
    @GeneratedValue
    private Long layoutId;

    private String name;

    private boolean isTrackingEnabled;
    private boolean showHandPrompt;
    private boolean anonymizeInputEvents;

    private String infoTitle;
    private String infoMessage;

    @OneToMany(mappedBy = "layout", cascade = CascadeType.ALL)
    private List<KeyboardDefinition> layouts;

    public KeyboardLayout(){}

    public KeyboardLayout(ConfigLayout layout){
        name = layout.getName();
        isTrackingEnabled = layout.isTrackingEnabled();
        showHandPrompt = layout.showHandPrompt();
        anonymizeInputEvents = layout.anonymizeInputEvents();
        infoTitle = layout.getInfoTitle();
        infoMessage = layout.getInfoMessage();

        layouts = new LinkedList<>();
        layouts.add(new KeyboardDefinition(layout.getDefaultAlphabetLayout(), KeyboardDefinitionType.ALPHABET));
        layouts.addAll(layout.getAltAlphabetLayouts().stream().map(definition -> new KeyboardDefinition(definition, KeyboardDefinitionType.ALT_ALPHABET)).collect(Collectors.toList()));
        if(layout.getSymbolsLayout() != null){
            layouts.add(new KeyboardDefinition(layout.getSymbolsLayout(), KeyboardDefinitionType.SYMBOL));
        }
        if(layout.getSymbolsShiftedLayout() != null){
            layouts.add(new KeyboardDefinition(layout.getSymbolsShiftedLayout(), KeyboardDefinitionType.SYMBOL_SHIFTED));
        }
    }

    public void update(KeyboardLayout layout) {
        name = layout.name;
        infoMessage = layout.infoMessage;
        infoTitle = layout.infoTitle;
        isTrackingEnabled = layout.isTrackingEnabled;
        anonymizeInputEvents = layout.anonymizeInputEvents;
        showHandPrompt = layout.showHandPrompt;
        layouts = layout.layouts;
        update();
    }

    public static KeyboardLayout createLayout(String name, String layoutJson, boolean isTrackingEnabled, boolean showHandPrompty, boolean anonymizeInputEvents, String infoTitle, String infoMessage) throws Exception {
        KeyboardLayout layout = new KeyboardLayout();
        layout.name = name;
        layout.infoMessage = infoMessage;
        layout.infoTitle = infoTitle;
        layout.isTrackingEnabled = isTrackingEnabled;
        layout.anonymizeInputEvents = anonymizeInputEvents;
        layout.showHandPrompt = showHandPrompty;
        layout.layouts = KeyboardLayoutHelper.createLayout(layoutJson);
        return layout;
    }

    public KeyboardDefinition getDefaultAlphabetLayout(){
        return getLayoutForType(KeyboardDefinitionType.ALPHABET);
    }

    public List<KeyboardDefinition> getAltAlphabetLayouts(){
        List<KeyboardDefinition> altLayouts = new LinkedList<>();
        if(layouts != null){
            altLayouts.addAll(layouts.stream().filter(layout
                    -> StringUtils.equals(KeyboardDefinitionType.ALT_ALPHABET.getName(), layout.getDefinitionType()))
                    .collect(Collectors.toList()));
        }
        return altLayouts;
    }

    public KeyboardDefinition getSymbolsLayout(){
        return getLayoutForType(KeyboardDefinitionType.SYMBOL);
    }

    public KeyboardDefinition getSymbolsShiftedLayout() {
        return getLayoutForType(KeyboardDefinitionType.SYMBOL_SHIFTED);
    }

    private KeyboardDefinition getLayoutForType(KeyboardDefinitionType type){
        if(layouts == null){
            return null;
        }
        for(KeyboardDefinition layout : layouts){
            if(StringUtils.equals(type.getName(), layout.getDefinitionType())){
                return layout;
            }
        }
        return null;
    }

    public String getName(){
        return name;
    }

    public String getLayoutId(){
        return String.valueOf(layoutId);
    }

    public boolean isTrackingEnabled(){
        return isTrackingEnabled;
    }

    public boolean showHandPrompt(){
        return showHandPrompt;
    }

    public boolean isAnonymizeInputEvents(){
        return anonymizeInputEvents;
    }

    public boolean hasInfoMessage(){
        return StringUtils.isNotBlank(infoTitle) || StringUtils.isNotBlank(infoMessage);
    }

    public String getInfoTitle(){
        return infoTitle;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public String getFormJson(){
        return KeyboardLayoutHelper.getFormJson(layouts);
    }
}
