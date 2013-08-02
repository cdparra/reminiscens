package controllers;

import static play.libs.Json.toJson;

import java.util.List;

import models.User;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Security;
import play.mvc.Http.Session;
import play.mvc.Result;
import pojos.CityBean;
import pojos.FileBean;
import pojos.MentionPersonBean;
import pojos.ResponseStatusBean;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import views.html.index;


import com.feth.play.module.pa.PlayAuthenticate;

import delegates.ApplicationDelegate;
import enums.ResponseStatus;

public class Application extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static Form<FileBean> fileForm = Form.form(FileBean.class);;

	/**
	 * Index method that renders the index view
	 * 
	 * @return
	 */
	public static Result index() {
		return ok(index.render());
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

	/*
	 * Some utility endpoints
	 */
	public static Result getCities() {
		List<CityBean> bean = ApplicationDelegate.getInstance().getCities();
		return bean != null ? ok(toJson(bean)) : notFound();
	}

	public static Result getCitiesByCountryId(Long countryId) {
		List<CityBean> bean = ApplicationDelegate.getInstance()
				.getCitiesByCountryId(countryId);
		return bean != null ? ok(toJson(bean)) : notFound();
	}

	public static Result getCitiesByCountryName(String countryName) {
		List<CityBean> bean = ApplicationDelegate.getInstance()
				.getCitiesByCountryName(countryName);
		return bean != null ? ok(toJson(bean)) : notFound();
	}

	public static Result getCitiesById(Long cityId) {
		CityBean bean = ApplicationDelegate.getInstance().getCitiesById(cityId);
		return bean != null ? ok(toJson(bean)) : notFound();
	}

	public static Result getNewCities(Long lastCityId) {
		List<CityBean> bean = ApplicationDelegate.getInstance().getNewCities(
				lastCityId);
		return bean != null ? ok(toJson(bean)) : notFound();
	}

	public static Result getCityByName(String cityName) {
		List<CityBean> bean = ApplicationDelegate.getInstance().getCityByName(
				cityName);
		return bean != null ? ok(toJson(bean)) : notFound();
	}

	public static Result testUpload() {
		return ok(views.html.upload.render());
	}

//	@Security.Authenticated(Secured.class)
	public static Result upload() {
		Form<FileBean> filledForm = fileForm.bindFromRequest();

		if (filledForm.hasErrors()) {
			ResponseStatusBean response = new ResponseStatusBean();
			response.setResponseStatus(ResponseStatus.BADREQUEST);
			response.setStatusMessage("play.authenticate.filledFromHasErrors:"
					+ filledForm.errorsAsJson());
			return badRequest(toJson(response));
		} else {
			play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
			play.mvc.Http.MultipartFormData.FilePart file = body.getFile("file") == null ? body.getFile("files[]") : body.getFile("file") ;
			FileBean fileBean = filledForm.get();
						
			if (file != null) {
				fileBean = ApplicationDelegate.getInstance().saveFile(file, fileBean);
				return ok(toJson(fileBean));
			} else {
				flash("error", "Missing file");
				ResponseStatusBean response = new ResponseStatusBean();
				response.setResponseStatus(ResponseStatus.BADREQUEST);
				response.setStatusMessage("File is null");
				return badRequest(toJson(response));
			}
		}
	}

}
