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

import com.google.inject.Inject;
import database.EventDatabase;
import database.UserDatabase;
import models.DashboardEvent;
import models.DashboardUser;
import play.Logger;
import play.api.db.Database;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardController extends Controller {

    private static final int MAX_EVENTS_COUNT = 1000;

    private @Inject Database db;

    @Security.Authenticated(CmsSecured.class)
    public Result index() {
        Connection connection = db.getConnection();

        List<DashboardUser> users = new ArrayList<>();
        Thread userFetcher = new Thread(() -> {
            UserDatabase.fillWithUsers(connection, users);
            EventDatabase.fillUserWithEventData(connection, users);
            Collections.sort(users, DashboardUser.getLastEventComparator());
        });

        List<DashboardEvent> events = new ArrayList<>();
        Thread eventFetcher = new Thread(() -> EventDatabase.fillWithEvents(connection, events, MAX_EVENTS_COUNT));

        userFetcher.start();
        eventFetcher.start();

        try {
            userFetcher.join();
            eventFetcher.join();
        } catch (InterruptedException e) {
            Logger.warn("error join thread", e);
        }

        int totalEventsCount = EventDatabase.getTotalEventsCount();

        try {
            connection.close();
        } catch (SQLException e) {
            Logger.error("error closing connection ", e);
        }

        return ok(dashboard.render(totalEventsCount, events, users).body().trim()).as("text/html; charset=utf-8");
    }
}
