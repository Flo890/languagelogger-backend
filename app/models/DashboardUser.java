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

import org.apache.commons.lang3.StringUtils;
import utils.DateConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;

/**
 * @Deprecated - not in use anymore - there is the new CMS :)
 *
 */
@Deprecated
public class DashboardUser {

    public final String id;
    public final boolean isMale;
    public final int age;
    public final String registrationDate;
    public final String deviceManufacturer;
    public final String deviceModel;
    public final String deviceResolution;

    private long lastEventTimeStamp;
    public String lastEvent;
    public String lastPrivateMode;
    public long eventCount;

    public DashboardUser(ResultSet res) throws SQLException {
        id = res.getString("uuid");
        isMale = StringUtils.equals("male", res.getString("gender"));
        age = res.getInt("age");
        registrationDate = DateConverter.getDateStringFromMillis(res.getLong("registration_timestamp"));
        deviceManufacturer = res.getString("device_manufacturer");
        deviceModel = res.getString("device_model");
        deviceResolution = res.getString("device_screen_width") + "x" + res.getString("device_screen_height");
    }

    public void fillGroupByEventData(ResultSet res) throws SQLException{
        eventCount = res.getLong("eventCount");
        lastEventTimeStamp = res.getLong("lastActivity");
        if(lastEventTimeStamp > 0){
            lastEvent = DateConverter.getDateStringFromMillis(lastEventTimeStamp);
        }
        long lastPrivateModeTimeStamp = res.getLong("lastPrivateMode");
        if(lastPrivateModeTimeStamp > 0){
            lastPrivateMode = DateConverter.getDateStringFromMillis(lastPrivateModeTimeStamp);
        }
    }

    public static Comparator<DashboardUser> getLastEventComparator(){
        return (o1, o2) -> o1.lastEventTimeStamp >= o2.lastEventTimeStamp ? -1 : 1;
    }
}
