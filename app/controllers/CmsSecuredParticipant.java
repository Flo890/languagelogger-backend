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

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by ResearchIME Project on 07.08.16.
 */
public class CmsSecuredParticipant extends CmsSecured {

    public static final String sessionUserKey = "user_participant";

    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get(sessionUserKey);
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.CmsLoginParticipantController.login());
    }
}
