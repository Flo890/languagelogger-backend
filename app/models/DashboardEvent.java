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

import utils.DateConverter;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Deprecated - not in use anymore - there is the new CMS :)
 */
public class DashboardEvent {

    public final long timestamp;
    public final String time;
    public final String userId;
    public final boolean isPrivateMode;
    public final boolean isAnonymous;
    public final String inputEvent;
    public final String inputMode;
    public final String handPosture;
    public final int contentLength;
    public final String packageName;
    public final String textHint;
    public final String code;
    public final int x;
    public final int y;

    public DashboardEvent(ResultSet res) throws SQLException {
        this.timestamp = res.getLong("timestamp");
        this.time = DateConverter.getDateStringFromMillis(timestamp);
        this.userId = res.getString("user_uuid");
        this.isPrivateMode = res.getInt("private_mode") == 1;
        this.isAnonymous = res.getInt("anonymized") == 1;
        this.inputEvent = res.getString("type");
        this.inputMode = res.getString("input_mode");
        this.handPosture = res.getString("hand_posture");
        this.contentLength = res.getInt("content_length");
        this.packageName = res.getString("field_package_name");
        this.textHint = res.getString("field_hint_text");
        this.code = res.getString("code");
        this.x = res.getInt("x");
        this.y = res.getInt("y");
    }
}
