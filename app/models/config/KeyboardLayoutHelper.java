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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.KeyboardDefinition;
import play.Logger;
import play.libs.Json;
import utils.JsonUtils;
import utils.KeyboardDefinitionType;

import java.util.LinkedList;
import java.util.List;

public class KeyboardLayoutHelper {

    public static String getDefaultJson() {
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        ObjectNode layoutSampleNode = JsonNodeFactory.instance.objectNode();
        layoutSampleNode.set("keyboard_rows", JsonNodeFactory.instance.arrayNode());
        root.set("defaultAlphabetLayout", layoutSampleNode);

        ArrayNode altLayoutsNode = JsonNodeFactory.instance.arrayNode();
        ObjectNode localeLayoutSampleNode = JsonNodeFactory.instance.objectNode();
        localeLayoutSampleNode.put("locale", "de");
        localeLayoutSampleNode.set("keyboard_rows", JsonNodeFactory.instance.arrayNode());
        altLayoutsNode.add(localeLayoutSampleNode);
        root.set("altAlphabetLayouts", altLayoutsNode);

        root.set("symbolsLayout", layoutSampleNode);
        root.set("symbolsShiftedLayout", layoutSampleNode);
        return JsonUtils.getPrettyJson(root.toString());
    }

    public static String getFormJson(List<KeyboardDefinition> layouts) {
        try {
            return JsonUtils.getPrettyJson(getFormJsonNode(layouts).toString());
        } catch (Exception e) {
            Logger.warn("json error", e);
        }
        return "";
    }

    private static JsonNode getFormJsonNode(List<KeyboardDefinition> layouts) throws Exception {
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        ArrayNode altAlphabetNode = JsonNodeFactory.instance.arrayNode();
        rootNode.set("altAlphabetLayouts", altAlphabetNode);

        if(layouts != null){
            for (KeyboardDefinition definition : layouts) {
                ObjectNode item = JsonNodeFactory.instance.objectNode();
                if(definition.hasLocale()){
                    item.put("locale", definition.getLocale());
                }
                item.set("keyboard_rows", Json.parse(definition.getKeyboardRows()));

                if(definition.isType(KeyboardDefinitionType.ALPHABET)){
                    rootNode.set("defaultAlphabetLayout", item);
                }else if(definition.isType(KeyboardDefinitionType.ALT_ALPHABET)){
                    altAlphabetNode.add(item);
                }else if(definition.isType(KeyboardDefinitionType.SYMBOL)){
                    rootNode.set("symbolsLayout", item);
                }else if(definition.isType(KeyboardDefinitionType.SYMBOL_SHIFTED)){
                    rootNode.set("symbolsShiftedLayout", item);
                }
            }
        }
        return rootNode;
    }

    public static List<KeyboardDefinition> createLayout(String inputJson) throws Exception {
        JsonNode rootNode = Json.parse(inputJson);
        if(! rootNode.isObject()){
            throw new Exception("invalid input json");
        }

        List<KeyboardDefinition> list = new LinkedList<>();

        JsonNode layoutNode = rootNode.get("defaultAlphabetLayout");
        list.add(new KeyboardDefinition(layoutNode, KeyboardDefinitionType.ALPHABET));

        if(rootNode.has("altAlphabetLayouts")) {
            layoutNode = rootNode.get("altAlphabetLayouts");
            if(layoutNode.isArray()){
                for (JsonNode itemnode : layoutNode) {
                    list.add(new KeyboardDefinition(itemnode, KeyboardDefinitionType.ALT_ALPHABET));
                }
            }
        }

        if(rootNode.has("symbolsLayout")){
            list.add(new KeyboardDefinition(rootNode.get("symbolsLayout"), KeyboardDefinitionType.SYMBOL));
        }

        if(rootNode.has("symbolsShiftedLayout")){
            list.add(new KeyboardDefinition(rootNode.get("symbolsShiftedLayout"), KeyboardDefinitionType.SYMBOL_SHIFTED));
        }
        return list;
    }
}
