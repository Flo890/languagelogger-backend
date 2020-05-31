package controllers;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import play.Configuration;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.cms_login;

public abstract class AbstractCmsLoginController extends Controller {

    @Inject
    private FormFactory formFactory;
    @Inject
    protected Configuration playConfig;

    public abstract Result login();

    public Result logout() {
        session().clear();
        flash().clear();
        flash("success", "You have been successfully signed out.");
        return redirect(routes.CmsLoginController.login());
    }

    public Result authenticate() {
        Form<CmsLoginParticipantController.Login> loginForm = formFactory.form(CmsLoginParticipantController.Login.class).bindFromRequest();
        String user = loginForm.data().get("userName");
        String pwd = loginForm.data().get("pwd");
        session().clear();
        if(! isValidLogin(user, pwd)){
            flash().clear();
            flash("error", "Invalid Username or Password.");
            return redirect(routes.CmsLoginController.login());
        }


        session(getSessionUserKey(), user);
        return performSuccessRedirect(user);
    }

    protected abstract boolean isValidLogin(String user, String pwd);

    public static class Login {}

    public abstract String getSessionUserKey();

    public abstract Result performSuccessRedirect(String userId);
}
