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
import models.Message;
import utils.PushSender;

import java.util.List;

public class MessagingDatabase {

    public static boolean onMessageReceived(Message message){
        message.setTimestampToNow();
        if(message.isUserMessage()){
            message.setNew(true);
            message.save();
        }else{
            message.save();
            return PushSender.pushMessageToDevice(message);
        }
        return false;
    }

    public static List<Message> getMessagesForUserId(final String userId){
        return Ebean
                .find(Message.class)
                .where().eq("user_id", userId)
                .order().desc("timestamp")
                .findList();
    }

    public static int getMessagesCountForUserId(final String userId){
        return Ebean
                .find(Message.class)
                .where().eq("user_id", userId)
                .findRowCount();
    }

    public static boolean removeMessage(final Long messageId) {
        Message message = Ebean.find(Message.class).where().eq("id", messageId).findUnique();
        if (message == null) {
            return false;
        }
        message.delete();
        return true;
    }

    public static int getNewMessagesCountForUserId(String userId) {
        return userId != null ?
                Ebean.find(Message.class)
                        .where().eq("user_id", userId)
                        .eq("is_new", true).findRowCount() : 0;
    }

    public static boolean hasNewMessagesForUserId(final String userId){
        return getNewMessagesCountForUserId(userId) > 0;
    }

    public static int getNewMessagesCount() {
        return Ebean.find(Message.class).where().eq("is_new", true).findRowCount();
    }

    public static void setIsMessageNew(Long messageId, boolean isNew) {
        Message message = Ebean.find(Message.class).where().eq("id", messageId).findUnique();
        if(message != null){
            message.setNew(isNew);
            message.update();
        }
    }
}
