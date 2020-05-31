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

import database.EventDatabase;
import database.MessagingDatabase;
import database.UserDatabase;
import models.Event;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.cms_dashboard;

import java.util.List;

public class CmsDashboardController extends Controller {

    private static final int MAX_EVENTS_COUNT = 1000;

    @Security.Authenticated(CmsSecured.class)
    public Result index() {
        List<Event> events = EventDatabase.getNewestEvents(MAX_EVENTS_COUNT);
        int eventsCount = EventDatabase.getTotalEventsCount();
        int userCount = UserDatabase.getTotalUserCount();
        int newMessagesCount = MessagingDatabase.getNewMessagesCount();
        return ok(cms_dashboard.render(events, eventsCount, userCount, newMessagesCount).body().trim()).as("text/html; charset=utf-8");
    }
}
