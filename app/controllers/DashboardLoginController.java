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

import org.apache.commons.lang3.StringUtils;
import play.Configuration;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.dashboard_login;

import static play.data.Form.form;

public class DashboardLoginController extends Controller {

    public Result login() {
        return ok(dashboard_login.render(form(Login.class)).body().trim()).as("text/html; charset=utf-8");
    }

    public Result logout() {
        session().clear();
        flash().clear();
        flash("success", "You got signed out successfully.");
        return redirect(routes.DashboardLoginController.login());
    }

    public Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            session().clear();
            flash().clear();
            flash("error", loginForm.globalError().message());
            return redirect(routes.DashboardLoginController.login());
        } else {
            session().clear();
            flash().clear();
            session("user", loginForm.get().user);
            return redirect(routes.DashboardController.index());
        }
    }

    public static class Login {
        public String user;
        public String password;

        public String validate() {
            Configuration conf = play.Play.application().configuration();
            boolean correctUser = StringUtils.equals(user, conf.getString("dashboard.user"));
            if(! correctUser){
                return "Invalid username";
            }
            boolean correctPwd = StringUtils.equals(password, conf.getString("dashboard.pwd"));
            if(! correctPwd){
                return "Invalid password";
            }
            return null;
        }
    }
}
