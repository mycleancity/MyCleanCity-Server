package controllers;

import static models.Checker.Required;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.Check;
import models.Faulty;
import models.Faulty.Code;
import models.Faulty.Type;
import models.Secured;
import models.db.User;
import models.json.JLogin;
import models.json.JModel;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.libs.Crypto;
import play.libs.Time;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Http;
import controllers.web.HomeWeb;

public class Security extends BaseController {

	private final static String USER = "C";

	@Catch(Faulty.class)
	protected static void onFaulty(Faulty faulty) {
		faulty.apply(Controller.isApi());
	}

	@Before(unless = { "login", "authenticate", "logout" })
	protected static void checkAccess() throws Throwable {

		Secured secured = getActionAnnotation(Secured.class);
		if (secured == null)
			secured = getControllerInheritedAnnotation(Secured.class);
		if (secured == null || !secured.value())
			return;

		// Authenticate
		if (!session.contains(USER)) {
			flash.put("url", "GET".equals(request.method) ? request.url
					: Play.ctxPath + "/");
			if (Controller.isApi())
				forbidden();
			login(null);
		}

		// Checks
		Check check = getActionAnnotation(Check.class);
		if (check != null)
			check(check);

		check = getControllerInheritedAnnotation(Check.class);
		if (check != null)
			check(check);
	}

	public static void login(String email) throws Throwable {
		rememberMe();
		flash.keep("url");
		render(email);
	}

	public static void status() {
		Map<String, Boolean> o = new HashMap<String, Boolean>();
		o.put("isConnected", isConnected() ? true : false);
		renderJSON(o);
	}

	public static void authenticate(String email, String password,
			boolean remember, String version, String platform) throws Throwable {

		Required(Code.E1001, email);
		Required(Code.E1002, password);

		// Check tokens
		User me = authenticate(email, password);
		Boolean allowed = me != null;
		if (validation.hasErrors() || !allowed) {
			if (Controller.isApi())
				throw new Faulty(Type.INVALID, Code.E1006);
			flash.keep("url");
			flash.error(Code.E1006.toString());
			params.flash();
			if (Controller.isOffice())
				login(email);
			else
				HomeWeb.index();

		}
		authenticated(remember, me, version, platform);
		redirectToOriginalURL();
	}

	public static void logout(String redirect) throws Throwable {
		session.clear();
		response.removeCookie("rememberme");

		if (Controller.isApi())
			BaseApi.OK();

		flash.success("secure.logout");
		if (StringUtils.isEmpty(redirect))
			redirect = Play.ctxPath + "/";
		redirect(redirect);
	}

	protected static void redirectToOriginalURL() throws Throwable {
		if (Controller.isApi()) {
			Map<String, Object> o = new HashMap<String, Object>();
			o.put("user", JModel.from(connected(), JLogin.class));
			renderJSON(o);
		}

		String url = flash.get("url");
		if (url == null) {
			url = Play.ctxPath + "/";
			User me = connected();
			if (me.roles.contains(User.ADMIN)) {
				url = Play.ctxPath + "/office/complaints";
			} else if (me.roles.contains(User.OFFICER)
					|| me.roles.contains(User.OFFICER_HEAD)) {
				url = Play.ctxPath + "/office/department/complaints";
			} else if (me.roles.contains(User.COUNCILLOR)) {
				url = Play.ctxPath + "/office/councillor/dashboard";
			}
		}
		redirect(url);
	}

	protected static void authenticated(boolean remember, User user,
			String version, String platform) throws Faulty {

		// Mark user as connected
		session.put(USER, user.id);
		user.version = version;
		user.platform = platform;
		user.lastLoginDate = new Date();
		user.save();

		// Remember if needed
		if (remember) {
			Date expiration = new Date();
			String duration = "30d"; // maybe make this override-able
			expiration.setTime(expiration.getTime()
					+ Time.parseDuration(duration));
			response.setCookie("rememberme",
					Crypto.sign(user.id + "-" + expiration.getTime()) + "-"
							+ user.id + "-" + expiration.getTime(), duration);
		}
	}

	protected static void rememberMe() throws Throwable {
		Http.Cookie remember = request.cookies.get("rememberme");
		if (remember != null) {
			int firstIndex = remember.value.indexOf("-");
			int lastIndex = remember.value.lastIndexOf("-");
			if (lastIndex > firstIndex) {
				String sign = remember.value.substring(0, firstIndex);
				String restOfCookie = remember.value.substring(firstIndex + 1);
				String user = remember.value.substring(firstIndex + 1,
						lastIndex);
				String time = remember.value.substring(lastIndex + 1);
				Date expirationDate = new Date(Long.parseLong(time));
				Date now = new Date();
				if (expirationDate == null || expirationDate.before(now)) {
					logout(null);
				}
				if (Crypto.sign(restOfCookie).equals(sign)) {
					session.put(USER, user);
					redirectToOriginalURL();
				}
			}
		}
	}

	protected static User authenticate(String email, String password)
			throws Faulty {
		return User.authenticate(email, password);
	}

	protected static boolean check(String profile) {
		return connected().roles.contains(profile);
	}

	protected static User connected() {
		if (isConnected()) {
			User user = User.findById(Long.parseLong(session.get(USER)));
			return user;
		}
		return null;
	}

	protected static boolean isConnected() {
		return session.contains(USER);
	}

	private static void check(Check check) throws Throwable {
		boolean hasProfile = false;
		for (String profile : check.value()) {
			if (check(profile)) {
				hasProfile = true;
				break;
			}
		}
		if (!hasProfile)
			forbidden();
	}

}
