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
import models.DashboardUser;
import models.KeyboardUserConfig;
import models.User;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ResearchIME Project on 11.12.16.
 */
public class UserDatabase {

    public static void fillWithUsers(Connection connection, List<DashboardUser> dataList) {
        String sql = "select * from user";
        DataSelector.fillListData(connection, dataList, sql, DashboardUser::new);
    }

    public static int getTotalUserCount(){
        return Ebean.find(User.class).findRowCount();
    }

    public static List<User> getUsers(){
        return Ebean.find(User.class).findList();
    }

    public static User getUserById(String userId) {
        if (userId == null) {
            return null;
        }
        return Ebean.find(User.class)
                .where().eq("uuid", userId)
                .findUnique();
    }

    public static String getPushToken(String userId) {
        User user = getUserById(userId);
        return user == null ? null : user.getPushToken();
    }

    public static long getStudyEndDate(String userId){
        User user = getUserById(userId);
        if(user == null){
            return 0;
        }
        long studyDuration = ConfigManager.getInstance().getConfig().getStudyDuration();
        return user.getRegistrationTimestamp() > 0 ? user.getRegistrationTimestamp() + studyDuration : 0;
    }

    public static Collection<String> getUserPushTokenWithKeyboardLayoutId(String layoutId){
        Set<String> tokens = new HashSet<>();
        if(layoutId == null){return tokens;}
        List<User> users = Ebean.find(User.class)
                .select("pushToken")
                .findList();
        users.stream().filter(user -> user.containsLayoutId(layoutId)).forEach(user -> {
            String token = user.getPushToken();
            if (token != null) {
                tokens.add(token);
            }
        });
        return tokens;
    }

    public static void setKeyboardConfigAsSent(String userId, Long configId){
        if(configId == null){return;}

        User user = getUserById(userId);
        if(user == null){return;}
        List<KeyboardUserConfig> configs = user.getKeyboards();
        if(configs != null){
            configs.stream().filter(config -> configId.equals(config.getId())).forEach(KeyboardUserConfig::setLayoutMessageSent);
        }
        user.update();
    }

    public static String getCurrentLayoutName(String userId){
        User user = getUserById(userId);
        if(user == null){
            return null;
        }
        KeyboardUserConfig config = user.getCurrentKeyboardConfig();
        return config == null ? null : config.getLayoutName();
    }
}