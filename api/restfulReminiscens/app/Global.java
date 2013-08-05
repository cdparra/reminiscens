import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Collections;
import java.util.Locale;

import models.SecurityRole;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import enums.MyRoles;

import play.Application;
import play.GlobalSettings;
import play.data.format.Formatters;
import play.mvc.Action;
import play.mvc.Call;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Result;
import pojos.CityBean;
import pojos.PersonBean;
import providers.MyUsernamePasswordAuthUser;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import controllers.routes;

public class Global extends GlobalSettings {

	/**
	 * Call to create the root Action of a request for a Java application. The
	 * request and actionMethod values are passed for information.
	 * 
	 * @param request
	 *            The HTTP Request
	 * @param actionMethod
	 *            The action method containing the user code for this Action.
	 * @return The default implementation returns a raw Action calling the
	 *         method.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Action onRequest(Request request, Method actionMethod) {
		return new Action.Simple() {
			public Result call(Context ctx) throws Throwable {
				Result r = delegate.call(ctx);
				ctx.session().clear();
				// Instead of passing the session key in a cookie, we pass it in
				// a header
				// String context =
				// Configuration.root().getString("application.context");
				// ctx.response().discardCookie("PLAY_SESSION");
				// ctx.response().discardCookie("PLAY_SESSION", context);
				return r;
			}
		};
	}

	static class InitialData {

		@SuppressWarnings("deprecation")
		public static void insert(Application app) {

			/*
			 * Insert enumerated Security Roles in DB if none exist
			 */
			if (SecurityRole.find.findRowCount() == 0) {
				for (final enums.MyRoles roleEnum : enums.MyRoles.values()) {
					final SecurityRole role = new SecurityRole();
					role.setName(roleEnum);
					role.save();
				}
			}

			if (User.find.findRowCount() == 0) {

				// Adding a member
				PersonBean person = new PersonBean();
				person.setFirstname("First");
				person.setLastname("Member");
				person.setBirthdate(DateTime.parse("1950-01-01 12:00:00"));

				CityBean birthplace = new CityBean();
				birthplace.setName("Trento");

				person.setBirthplace(birthplace);

				MyUsernamePasswordAuthUser auth = null;

				auth = new MyUsernamePasswordAuthUser("First Member", person,
						null, "first@example.com", "password");

				User user = models.User.create(auth);

				user.setEmailValidated(true);
				user.setRoles(Collections.singletonList(SecurityRole
						.findByRoleName(MyRoles.MEMBER.toString())));
				user.setCreationDate(DateTime.now());
				user.setLocale("it_IT");
				user.setUsername("first.member");
				user.setUsernameVerified(true);
				user.setActive(true);
				user.setEmailValidated(true);
				user.update();

				// TODO add users of other ROLES
				// TODO add one complete timeline, with stories and mementos
			}
		}

	}

	public void onStart(final Application app) {

		InitialData.insert(app);

		PlayAuthenticate.setResolver(new Resolver() {
			@Override
			public Call login() {
				// Your login page
				return routes.Application.index();
			}

			@Override
			public Call afterAuth() {
				// The user will be redirected to this page after authentication
				// if no original URL was saved

				// play.mvc.Call call = new Call() {
				// @Override
				// public String url() {
				// final play.mvc.Http.Cookie cookie = play.mvc.Http.Context
				// .current().request().cookie("PLAY_SESSION");
				// String url_decoded = "";
				// try {
				// url_decoded = java.net.URLDecoder.decode(
				// controllers.routes.Application.id(
				// "!#" + cookie.name() + "="
				// + cookie.value()).url(),
				// "UTF-8");
				// } catch (UnsupportedEncodingException e) {
				// e.printStackTrace();
				// }
				// return url_decoded;
				// }
				//
				// @Override
				// public String method() {
				// return "GET";
				// }
				// };
				// return call;
				return routes.Restricted.index();
			}

			@Override
			public Call afterLogout() {
				// TODO what should we do ?
				return routes.Application.index();
			}

			@Override
			public Call auth(final String provider) {
				// You can provide your own authentication implementation,
				// however the default should be sufficient for most cases
				// return
				// com.feth.play.module.pa.controllers.routes.Authenticate
				// .authenticate(provider);
				return controllers.routes.AuthenticateLocal
						.authenticate(provider);
			}

			@Override
			public Call onException(final AuthException e) {
				if (e instanceof AccessDeniedException) {
					return routes.Application
							.oAuthDenied(((AccessDeniedException) e)
									.getProviderKey());
				}

				// more custom problem handling here...

				return super.onException(e);
			}

			@Override
			public Call askLink() {
				// We don't support moderated account linking in this sample.
				// See the play-authenticate-usage project for an example
				return null;
			}

			@Override
			public Call askMerge() {
				// We don't support moderated account merging in this sample.
				// See the play-authenticate-usage project for an example
				return null;
			}
		});

		Formatters
				.register(
						DateTime.class,
						new Formatters.AnnotationFormatter<utils.JodaDateTime, DateTime>() {
							@Override
							public DateTime parse(
									utils.JodaDateTime annotation,
									String input, Locale locale)
									throws ParseException {
								if (input == null || input.trim().isEmpty())
									return null;

								if (annotation.format().isEmpty())
									return new DateTime(Long.parseLong(input));
								else
									return DateTimeFormat
											.forPattern(annotation.format())
											.withLocale(locale)
											.parseDateTime(input);
							}

							@Override
							public String print(utils.JodaDateTime annotation,
									DateTime time, Locale locale) {
								if (time == null)
									return null;

								if (annotation.format().isEmpty())
									return time.getMillis() + "";
								else
									return time.toString(annotation.format(),
											locale);
							}

						});

	}

}

// @SuppressWarnings("rawtypes")
// @Override
// public Action onRequest(Request request, Method actionMethod) {
// String play_session = request.getHeader("PLAY_SESSION");
// Logger.debug("play session header: " + play_session);
// if(play_session != null && !"".trim().equals(play_session)){
// scala.collection.immutable.Map<String, String> values =
// Session.decode(play_session);
// if(values.isEmpty()){
// Logger.warn("ignoring a not valid session ! : " + play_session);
// }else{
// try {
// //getting the request field from anon class
// Field requestField = request.getClass().getDeclaredField("req$2");
// play.api.mvc.Request requestInstance = (play.api.mvc.Request)
// requestField.get(request);
// // getting the session field
// Field sessionField =
// requestInstance.getClass().getDeclaredField("session");
// sessionField.setAccessible(true);
// Session sessionInstance = (Session) sessionField.get(requestInstance);
// //getting the data from the session
// Field dataField = sessionInstance.getClass().getDeclaredField("data");
// dataField.setAccessible(true);
// dataField.set(sessionInstance, Session.decode(play_session));
// //everything was ok, so we'll add the cookie to the session because maybe
// is going to be used later
// // Field cookiesField =
// requestInstance.getClass().getDeclaredField("cookies");
// // cookiesField.setAccessible(true);
// // cookiesField.set(requestInstance,
// Session.encodeAsCookie(play_session));
// Logger.debug("added to the session: " + sessionInstance);
// } catch (SecurityException e) {
// Logger.error(e.getMessage());
// } catch (NoSuchFieldException e) {
// Logger.error(e.getMessage());
// }catch (IllegalAccessException e) {
// Logger.error(e.getMessage());
// }
// }
//
// }
// return super.onRequest(request, actionMethod);
// }

// /**
// * Call to create the root Action of a request for a Java application.
// * The request and actionMethod values are passed for information.
// *
// * @param request The HTTP Request
// * @param actionMethod The action method containing the user code for this
// Action.
// * @return The default implementation returns a raw Action calling the
// method.
// */
// @SuppressWarnings("rawtypes")
// @Override
// public Action onRequest(Request request, Method actionMethod) {
// return new Action.Simple() {
// public Result call(Context ctx) throws Throwable {
// ctx.session().clear();
// return delegate.call(ctx);
// }
// };
// }

// public void onStart(Application app) {
// InitialData.insert(app);
// }