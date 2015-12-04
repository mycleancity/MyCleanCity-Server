package controllers.web.office;

import models.ListPaginator;
import models.Util;
import models.db.User;

import org.apache.commons.lang.StringUtils;

import controllers.BaseOffice;

public class ResidentWeb extends BaseOffice {

	public static void index(String keyword, String role, int page, int length) {

		length = Util.length(length);
		ListPaginator<User> users = null;
		if (StringUtils.isEmpty(role) || role.equalsIgnoreCase("All")) {
			users = new ListPaginator(
					User.search(false, keyword, page, length), User.count(
							false, keyword));
		} else {
			users = new ListPaginator(User.search(false, keyword, page, length,
					role), User.count(false, keyword, role));
		}
		users.setPageSize(length);
		render(users, keyword, role);
	}
}
