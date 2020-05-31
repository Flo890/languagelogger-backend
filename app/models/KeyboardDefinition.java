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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import models.config.layout.ConfigLayoutDefinition;
import org.apache.commons.codec.binary.StringUtils;
import utils.JsonUtils;
import utils.KeyboardDefinitionType;

import javax.annotation.Nullable;
import javax.persistence.*;

@Entity
public class KeyboardDefinition extends Model {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private KeyboardLayout layout;

    @JsonProperty("locale")
    private @Nullable String locale;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("keyboard_rows")
    private String keyboardRows;

    @JsonIgnore
    private String definitionType;

    public KeyboardDefinition(JsonNode node, KeyboardDefinitionType type) throws Exception{
        if(node.has("keyboard_rows")){
            keyboardRows = JsonUtils.getMinifiedJson(node.get("keyboard_rows").toString());
        }else{
            throw new Exception("keyboard_rows data is missing");
        }
        if(node.has("locale")){
            locale = node.get("locale").asText();
        }
        definitionType = type.getName();
    }

    public KeyboardDefinition(ConfigLayoutDefinition layout, KeyboardDefinitionType type) {
        this.locale = layout.getLocale();
        this.keyboardRows = layout.getKeyboardRows();
        this.definitionType = type.getName();
    }

    public boolean hasLocale(){
        return locale != null;
    }

    public String getKeyboardRows(){
        return keyboardRows;
    }

    public String getLocale(){
        return locale;
    }

    public String getDefinitionType() {
        return definitionType;
    }

    public boolean isType(KeyboardDefinitionType type){
        return StringUtils.equals(definitionType, type.getName());
    }
}
