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

import models.User;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Comparator;

public enum UserSortType {

    LAST_ACTIVITY("Aktivität", "activity"),
    ID("ID", "id"),
    NEWS("Neue Nachrichten", "news"),
    EVENTS_COUNT("Events", "events"),
    REGISTER("Registrierung", "creation"),
    STUDY_END("Studie-Ende", "study-end"),
    AGE("Alter", "age");

    public static final UserSortType DEFAULT = UserSortType.LAST_ACTIVITY;

    public final String displayName;
    public final String abbreviation;

    UserSortType(String displayName, String abbreviation){
        this.displayName = displayName;
        this.abbreviation = abbreviation;
    }

    public static @Nullable UserSortType createFromAbbreviation(@Nullable  String abbreviation){
        if(abbreviation == null){
            return null;
        }
        for(UserSortType type : UserSortType.values()){
            if(StringUtils.equalsIgnoreCase(type.abbreviation, abbreviation)){
                return type;
            }
        }
        return null;
    }

    public Comparator<User> getComparator(){
        switch(this){
            case LAST_ACTIVITY: return (o1, o2) -> o1.getLastActivityTimestamp() > o2.getLastActivityTimestamp() ? -1 : 1;
            case ID: return (o1, o2) -> o1.uuid.compareTo(o2.uuid);
            case NEWS: return (o1, o2) -> o1.getNewMessagesCount() > o2.getNewMessagesCount() ? -1 : 1;
            case EVENTS_COUNT: return (o1, o2) -> o1.getEventsCount() > o2.getEventsCount() ? -1 : 1;
            case REGISTER: return (o1, o2) -> o1.getRegistrationTimestamp() > o2.getRegistrationTimestamp() ? -1 : 1;
            case STUDY_END: return (o1, o2) -> o1.getRegistrationTimestamp() > o2.getRegistrationTimestamp() ? 1 : -1;
            case AGE: return (o1, o2) -> o1.age > o2.age ? 1 : -1;
        }
        return (o1, o2) -> 0;
    }
}
