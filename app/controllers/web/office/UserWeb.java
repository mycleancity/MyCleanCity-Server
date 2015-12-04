package controllers.web.office;

import models.Constant;
import models.ListPaginator;
import models.Util;
import models.db.User;

import org.apache.commons.lang.StringUtils;

import play.data.Upload;
import controllers.BaseOffice;

public class UserWeb extends BaseOffice {

	public static void index(String keyword, String role, int page, int length) {

		length = Util.length(length);
		ListPaginator<User> users = null;
		if (StringUtils.isEmpty(role) || role.equalsIgnoreCase("All")) {
			users = new ListPaginator(User.search(false, keyword, page, length,
					User.ADMIN, User.OFFICER, User.OFFICER_HEAD),
					User.count(false, keyword, User.ADMIN, User.OFFICER,
							User.OFFICER_HEAD));
		} else {
			users = new ListPaginator(User.search(false, keyword, page, length,
					role), User.count(false, keyword, role));
		}
		users.setPageSize(length);
		render(users, keyword, role);
	}

	public static void get(Long id) {
		User user = User.findById(id);
		render(user);
	}

	public static void create(String email, String password, String name,
			String role, Upload avatar) {
		if (isGET())
			render();
		User me = connected();
		User user = User.create(email, password, name, me, role);
		user.attachAvatar(avatar);
		index(null, null, 1, 10);
	}

	public static void edit(Long id) {
		User user = User.findById(id);
		render(user);
	}

	public static void update(Long id, String email, String password,
			String name, String role, Upload avatar) {
		User me = connected();
		User user = User.findById(id);
		user.update(email, password, me);
		user.name = name;
		user.roles.clear();
		user.roles.add(role);
		user.log.updateBy(connected());
		user.save();
		user.attachAvatar(avatar);
		get(id);
	}

	public static void delete(Long id) {
		User user = User.findById(id);
		user.status = Constant.INACTIVE;
		user.log.updateBy(connected());
		user.save();
		index(null, null, 1, 10);
	}
}
