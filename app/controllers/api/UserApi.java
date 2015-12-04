package controllers.api;

import static models.Checker.Required;

import java.util.HashMap;
import java.util.Map;

import models.Faulty;
import models.Faulty.Code;
import models.Secured;
import models.db.User;
import models.json.JModel;
import models.json.JUser;
import controllers.BaseApi;

@Secured
public class UserApi extends BaseApi {

	@Secured(false)
	public static void register(String email, String password, String name)
			throws Faulty {
		Required(Code.E1008, email);
		Required(Code.E1009, password);
		Required(Code.E1010, name);

		User user = User.create(email, password, name, null);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("user", JModel.from(user, JUser.class));
		renderJSON(o);
	}

}
