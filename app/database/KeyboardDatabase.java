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

package database;

import com.avaje.ebean.Ebean;
import config.ConfigManager;
import models.KeyboardLayout;
import models.KeyboardUserConfig;
import models.User;
import models.config.ConfigItem;
import models.config.KeyboardConfigItem;
import models.config.layout.ConfigLayout;
import models.config.layout.ConfigLayoutContainer;
import play.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KeyboardDatabase {

    public static List<KeyboardLayout> getKeyboards() {
        List<KeyboardLayout> layouts = Ebean.find(KeyboardLayout.class).findList();
        if (layouts == null || layouts.isEmpty()) {
            layouts = createFromConfig();
        }
        return layouts;
    }

    private static List<KeyboardLayout> createFromConfig() {
        List<KeyboardLayout> items = new LinkedList<>();
        ConfigLayoutContainer container = ConfigManager.getInstance().getLayouts();
        if (container == null) {
            return items;
        }
        List<ConfigLayout> layouts = container.getLayouts();
        if (layouts == null) {
            return items;
        }
        for (ConfigLayout layout : layouts) {
            KeyboardLayout layoutModel = new KeyboardLayout(layout);
            items.add(layoutModel);
            layoutModel.save();
        }
        return items;
    }

    public static void createKeyboardsForUser(User user) {
        Map<String, KeyboardLayout> layoutMap = new HashMap<>();
        for (KeyboardLayout layout : KeyboardDatabase.getKeyboards()) {
            layoutMap.put(layout.getLayoutId(), layout);
        }

        List<KeyboardUserConfig> userConfig = new LinkedList<>();

        ConfigItem config = ConfigManager.getInstance().getConfig();
        long timestamp = user.getRegistrationTimestamp();
        for (KeyboardConfigItem item : config.getKeyboards()) {
            KeyboardLayout layout = layoutMap.get(item.getId());
            if (layout != null) {
                userConfig.add(new KeyboardUserConfig(timestamp, layout));
                timestamp += item.getDuration();
            }
        }
        user.setKeyboards(userConfig);
    }

    public static KeyboardLayout getKeyboardLayout(String layoutId) {
        if(layoutId == null){return null;}
        return Ebean.find(KeyboardLayout.class)
                .where().eq("layout_id", layoutId)
                .findUnique();
    }

    public static boolean isKeyboardLayoutDBEmpty(){
        return Ebean.find(KeyboardLayout.class).findRowCount() == 0;
    }

    public static boolean deleteKeyboard(String id) {
        KeyboardLayout layout = getKeyboardLayout(id);
        if(layout != null){
            try{
                layout.delete();
                return true;
            }catch (Exception e){
                Logger.warn("could not delete keyboard", e);
            }
        }
        return false;
    }

    public static String getActiveKeyboardId(KeyboardUserConfig config){
        return config == null ? null : config.getLayoutId();
    }

    public static KeyboardUserConfig getActiveKeyboardUserConfig(List<KeyboardUserConfig> items) {
        KeyboardUserConfig configItem = null;
        long shortestInterval = Long.MAX_VALUE;
        for (KeyboardUserConfig item : items) {
            long itemActiveInterval = item.getActiveInterval(System.currentTimeMillis());
            if (itemActiveInterval > 0 && itemActiveInterval < shortestInterval) {
                shortestInterval = itemActiveInterval;
                configItem = item;
            }
        }
        return configItem;
    }
}
