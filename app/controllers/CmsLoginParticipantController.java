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

import database.UserDatabase;
import models.User;
import play.mvc.Result;
import views.html.cms_login_participant;

public class CmsLoginParticipantController extends AbstractCmsLoginController {

    public Result login() {
        return ok(cms_login_participant.render().body().trim()).as("text/html; charset=utf-8");
    }

    @Override
    public String getSessionUserKey() {
        return CmsSecuredParticipant.sessionUserKey;
    }

    @Override
    public Result performSuccessRedirect(String userId) {
        return redirect(routes.AbstractionEventsDemoController.index(userId));
    }

    @Override
    protected boolean isValidLogin(String userName, String pwd) {
        User user = UserDatabase.getUserById(userName);
        if (user != null){
            if (user.getPushToken().substring(0,11).equals(pwd)){
                return true;
            }
        }
        return false;
    }
}