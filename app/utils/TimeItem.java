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

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

public class TimeItem {

    private static final int DAYS_MILLIS = 86400000;
    private static final int HOURS_MILLIS = 3600000;
    private static final int MINUTES_MILLIS = 60000;

    private final long timestamp;

    private long days;
    private long minutes;
    private long hours;

    public TimeItem(long timestamp) throws Exception{
        this.timestamp = timestamp;
        if(timestamp <= 0){
            throw new Exception("time item has no duration");
        }
        calcUnits();
    }

    public TimeItem(String daysText, String hoursText, String minutesText) throws Exception{
        days = 0;
        try{
            days = Integer.parseInt(daysText);
        }catch (Exception ignore){}
        hours = 0;
        try{
            hours = Integer.parseInt(hoursText);
        }catch (Exception ignore){}
        minutes = 0;
        try{
            minutes = Integer.parseInt(minutesText);
        }catch (Exception ignore){}

        timestamp = days * DAYS_MILLIS + hours * HOURS_MILLIS + minutes * MINUTES_MILLIS;
        if(timestamp <= 0){
            throw new Exception("time item has no duration");
        }
        calcUnits();
    }

    private void calcUnits(){
        long splitTimestamp = timestamp;
        days = TimeUnit.MILLISECONDS.toDays(splitTimestamp);
        if(days > 0){splitTimestamp -= days * DAYS_MILLIS;}
        hours = TimeUnit.MILLISECONDS.toHours(splitTimestamp);
        if(hours > 0){splitTimestamp -= hours * HOURS_MILLIS;}
        minutes = TimeUnit.MILLISECONDS.toMinutes(splitTimestamp);
    }

    public long getDays(){
        return days;
    }

    public long getHours(){
        return hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getTimestamp(){
        return timestamp;
    }

    @Override
    public String toString() {
        String result = "";
        if(days > 0){
            result += days + " Tage";
        }
        if(hours > 0){
            if(StringUtils.isNotBlank(result)){
                result += ", ";
            }
            result += hours + " h";
        }
        if(minutes > 0){
            if(StringUtils.isNotBlank(result)){
                result += ", ";
            }
            result += minutes + " min";
        }
        if(StringUtils.isBlank(result)){
            return "< 1 min";
        }
        return result;
    }
}
