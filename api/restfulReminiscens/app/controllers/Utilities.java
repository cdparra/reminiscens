package controllers;

import static play.libs.Json.toJson;

import java.util.List;

import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Security;
import play.mvc.Result;
import pojos.CityBean;
import pojos.FileBean;
import pojos.ResponseStatusBean;

import delegates.UtilitiesDelegate;
import enums.ResponseStatus;

public class Utilities extends Controller {
	
	public static Form<FileBean> fileForm = Form.form(FileBean.class);;
	
	public static Result getCities() {
		List<CityBean> bean = UtilitiesDelegate.getInstance().getCities();
		return bean != null ? ok(toJson(bean)) : notFound();
	}

//	Deprecated	
//	public static Result getCitiesByCountryId(Long countryId) {
//		List<CityBean> bean = UtilitiesDelegate.getInstance()
//				.getCitiesByCountryId(countryId);
//		return bean != null ? ok(toJson(bean)) : notFound();
//	}

	public static Result getCitiesByCountryName(String country) {
		List<CityBean> bean = UtilitiesDelegate.getInstance()
				.getCitiesByCountryName(country);
		return bean != null ? ok(toJson(bean)) : notFound();
	}

	public static Result getCitiesByCountryNameAndRegion(String country, String region) {
		List<CityBean> bean = UtilitiesDelegate.getInstance()
				.getCitiesByCountryNameAndRegion(country,region);
		return bean != null ? ok(toJson(bean)) : notFound();
	}
	
	public static Result getCityById(Long cityId) {
		CityBean bean = UtilitiesDelegate.getInstance().getCitiesById(cityId);
		return bean != null ? ok(toJson(bean)) : notFound();
	}

	public static Result getNewCities(Long lastCityId) {
		List<CityBean> bean = UtilitiesDelegate.getInstance().getNewCities(
				lastCityId);
		return bean != null ? ok(toJson(bean)) : notFound();
	}

	public static Result getCitiesByName(String name) {
		List<CityBean> bean = UtilitiesDelegate.getInstance().getCityByName(
				name);
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
				fileBean = UtilitiesDelegate.getInstance().saveFile(file, fileBean);
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

 	@Security.Authenticated(Secured.class)
	public static Result secureUpload() {
		Form<FileBean> filledForm = fileForm.bindFromRequest();

		String userEmail = session().get("pa.u.id");
		User user = User.getByEmail(userEmail);
		
		
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
			
			if (fileBean.getOwner() == null && user != null) {
				Logger.debug("User "+user.getEmail()+" posted a file");
				fileBean.setOwner(user.getUserId());
			}
						
			if (file != null) {
				fileBean = UtilitiesDelegate.getInstance().saveFile(file, fileBean);
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
