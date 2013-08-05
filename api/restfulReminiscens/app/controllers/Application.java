package controllers;

import static play.libs.Json.toJson;

import models.User;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.Session;
import play.mvc.Result;
import pojos.ResponseStatusBean;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;

import com.feth.play.module.pa.PlayAuthenticate;

import enums.ResponseStatus;

public class Application extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";

	/**
	 * Index method that renders the index view
	 * 
	 * @return
	 */
	public static Result index() {
		return ok(views.html.index.render());
	}

	public static Result oAuthDenied(final String providerKey) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		flash(FLASH_ERROR_KEY,
				"You need to accept the OAuth connection in order to use this website!");
		return redirect(routes.Application.index());
	}

	public static User getLocalUser(final Session session) {
		final User localUser = User.findByAuthUserIdentity(PlayAuthenticate
				.getUser(session));
		return localUser;
	}

	public static Result doSignup() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			// return badRequest(signup.render(filledForm));
			ResponseStatusBean response = new ResponseStatusBean();
			response.setResponseStatus(ResponseStatus.BADREQUEST);
			response.setStatusMessage("play.authenticate.filledFromHasErrors:"
					+ filledForm.errorsAsJson());
			return badRequest(toJson(response));
		} else {
			// Everything was filled correctly
			// Do something with your part of the form before handling the user
			// signup
			return MyUsernamePasswordAuthProvider.handleSignup(ctx());
		}
	}

	public static Result doLogin() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			// return badRequest(login.render(filledForm));
			return badRequest();
		} else {
			// Everything was filled
			return MyUsernamePasswordAuthProvider.handleLogin(ctx());
		}
	}

	public static Result onLoginUserNotFound() {
		ResponseStatusBean response = new ResponseStatusBean();
		response.setResponseStatus(ResponseStatus.NODATA);
		response.setStatusMessage(Messages
				.get("playauthenticate.password.login.unknown_user_or_pw"));
		return notFound(toJson(response));
		// return notFound(Messages
		// .get("playauthenticate.password.login.unknown_user_or_pw"));
	}

}
