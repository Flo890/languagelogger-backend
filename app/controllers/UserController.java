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

package controllers;

import config.ConfigManager;
import database.KeyboardDatabase;
import database.UserDatabase;
import models.Message;
import models.User;
import models.config.ConfigItem;
import play.mvc.Result;
import utils.PushSender;

public class UserController extends JsonPostController<User> {

    @Override
    protected Class<User> getClassType() {
        return User.class;
    }

    public Result postUser() {
        System.out.println("POST /api/user");
        return processRequestData();
    }

    @Override
    protected void saveObject(User user) {
        System.out.println("saveObject - User.  " + user.age + user.deviceManufacturer + user.deviceModel + user.osVersion + user.uuid);
        User existingUser = UserDatabase.getUserById(user.getUserId());
        if(existingUser == null){
            KeyboardDatabase.createKeyboardsForUser(user);
            user.resetRegistrationTimestamp();
            user.save();
            onStudyStart(user.getUserId());
        }else{
            existingUser.update(user);
        }
    }

    private void onStudyStart(String userId){
        ConfigItem item = ConfigManager.getInstance().getConfig();
        if(item.hasWelcomeText()){
            Message message = new Message(item.getWelcomeTitle(), item.getWelcomeMessage(), userId);
            message.save();
            PushSender.pushMessageToDevice(message);
        }
    }
}
