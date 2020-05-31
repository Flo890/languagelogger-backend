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
import models.*;

import java.sql.Connection;
import java.util.*;

public class EventDatabase {

    public static void fillWithEvents(Connection connection, List<DashboardEvent> dataList, int limit){
        String sql = "select * from event order by timestamp desc limit " + limit;
        DataSelector.fillListData(connection, dataList, sql, DashboardEvent::new);
    }

    public static void fillUserWithEventData(Connection connection, List<DashboardUser> dataList){
        String sql = "select t1.user_uuid, t1.eventCount, t1.lastActivity, t2.lastPrivateMode from " +
                "(select user_uuid, count(user_uuid) as eventCount, max(timestamp) as lastActivity from event group by user_uuid) as t1 " +
                "left join (select user_uuid, max(timestamp) as lastPrivateMode from event group by user_uuid, private_mode having private_mode = 1) as t2 " +
                "on t1.user_uuid = t2.user_uuid;";

        HashMap<String, DashboardUser> dataMap = new HashMap<>();
        for(DashboardUser user : dataList){
            dataMap.put(user.id, user);
        }

        DataSelector.fillData(connection, sql, rs -> {
            String id = rs.getString("user_uuid");
            DashboardUser user = dataMap.get(id);
            if(user != null){
                user.fillGroupByEventData(rs);
            }
            return user;
        });
    }

    public static int getTotalEventsCount(){
        return Ebean.find(Event.class).findRowCount();
    }

    public static int getTotalEventsCountForUser(String userId){
        return Ebean.find(Event.class).where().eq("user_uuid", userId).findRowCount();
    }

    public static long getLastEventTimeForUser(String userId){
        Event event = Ebean.find(Event.class).where().eq("user_uuid", userId).order().desc("timestamp").setMaxRows(1).findUnique();
        return event == null ? 0 : event.timestamp;
    }

    public static List<Event> getNewestEvents(int limit){
        return Ebean.find(Event.class).order().desc("timestamp").setMaxRows(limit).findList();
    }

    public static List<MessageStatistics> getLatestMessageStatistics(int limit){
        return Ebean.find(MessageStatistics.class).order().desc("timestamp_type_start").setMaxRows(limit).findList();
    }

    public static List<MessageStatistics> getLatestMessageStatisticsOfUser(User user, int limit){
        return Ebean.find(MessageStatistics.class).where().eq("user_uuid",user.getUserId()).order().desc("timestamp_type_start").setMaxRows(limit).findList();
    }


    public static List<AbstractedActionEvent> getAbstractedActionEventsForMessageStatistic(MessageStatistics messageStatistics){
        List<AbstractedActionEvent> list = Ebean.find(AbstractedActionEvent.class).where().eq("message_statistics_id",messageStatistics.getClientEventId()).eq("user_uuid",messageStatistics.getUserUuid()).order().asc("date").findList();
        Set<Long> ids = new HashSet<>();
        // remove duplicates
        List<AbstractedActionEvent> newlist = new ArrayList<>();
        for(AbstractedActionEvent item : list){
            if (!ids.contains(item.getClientEventId())){
                newlist.add(item);
                ids.add(item.getClientEventId());
            }
        }
        return newlist;
    }

    public static int getTotalAbstractedActionEventsCount(){
        return Ebean.find(AbstractedActionEvent.class).findRowCount();
    }

    public static int getTotalAbstractedActionEventsCountForUser(User user){
        return Ebean.find(AbstractedActionEvent.class).where().eq("user_uuid", user.getUserId()).findRowCount();
    }
}
