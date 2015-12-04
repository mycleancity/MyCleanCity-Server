package controllers;

import models.db.User;
import play.mvc.Before;

public class BaseWeb extends Controller {

	@Before
	protected static void before() throws Throwable {
		request.args.put(REQ_TYPE, WEB);

		User me = connected();
		renderArgs.put("me", me);
	}

	protected static boolean isGET() {
		return request.method.equalsIgnoreCase("GET");
	}

	protected static boolean isPOST() {
		return request.method.equalsIgnoreCase("POST");
	}

}
