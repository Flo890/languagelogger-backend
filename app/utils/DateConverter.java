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

package utils;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {

    private static final int MILLIS_ONE_DAY = 86400000;

    public static String getDateStringFromMillis(long millis){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy - HH:mm:ss");
        Date date = new Date(millis);
        return sdf.format(date);
    }

    public static String getDateTimeStamp(long millis){
        if(millis == 0){return "";}
        Date date = new Date(millis);
        return getDateStamp(date) + ", " + getTimeStamp(date) + " Uhr";
    }

    public static String getDateStamp(long millis){
        return getDateStamp(new Date(millis));
    }

    public static String getDateStamp(Date date){
        if(date.getTime() == 0){
            return "";
        }
        if(DateUtils.isSameDay(date, new Date())){
            return "Heute";
        }else if(DateUtils.isSameDay(date, new Date(System.currentTimeMillis() - MILLIS_ONE_DAY))){
            return "Gestern";
        }else if(DateUtils.isSameDay(date, new Date(System.currentTimeMillis() + MILLIS_ONE_DAY))){
            return "Morgen";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EE, dd.MM.");
        return sdf.format(date);
    }

    public static String getTimeStamp(long millis){
        return getTimeStamp(new Date(millis));
    }

    public static String getTimeStamp(Date date){
        if(date.getTime() == 0){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }
}
