package controllers;

import play.Logger;

public class Controller extends Security {

	protected static boolean isApi() {
		if (request.url.startsWith("/api"))
			return true;
		if (request.args.containsKey(REQ_TYPE)) {
			String type = (String) request.args.get(REQ_TYPE);
			return type.equalsIgnoreCase(API);
		}
		return "json".equals(request.format);
	}

	protected static boolean isOffice() {
		return request.url.startsWith("/office");
	}

}
