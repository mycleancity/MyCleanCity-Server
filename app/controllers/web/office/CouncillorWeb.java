package controllers.web.office;

import java.util.List;

import models.Constant;
import models.ListPaginator;
import models.Util;
import models.db.User;
import models.db.Zone;
import play.data.Upload;
import controllers.BaseOffice;

public class CouncillorWeb extends BaseOffice {

	public static void index(String keyword, int page, int length) {

		length = Util.length(length);
		ListPaginator<User> users = new ListPaginator(User.search(false,
				keyword, page, length, User.COUNCILLOR), User.count(false,
				keyword, User.COUNCILLOR));
		users.setPageSize(length);
		render(users, keyword);
	}

	public static void get(Long id) {
		User user = User.findById(id);
		render(user);
	}

	public static void create(String email, String password, String name,
			Long zone, String mobile, Upload avatar) {
		if (isGET()) {
			List<Zone> zones = Zone.search(null, 1, 99999);
			render(zones);
		}
		User me = connected();
		User user = User.create(email, password, name, me, User.COUNCILLOR);
		user.zone = Zone.findById(zone);
		user.mobile = mobile;
		user.attachAvatar(avatar);
		user.save();
		index(null, 1, 10);
	}

	public static void edit(Long id) {
		User user = User.findById(id);
		List<Zone> zones = Zone.search(null, 1, 99999);
		render(user, zones);
	}

	public static void update(Long id, String email, String name,
			String password, Long zone, String mobile, Upload avatar) {
		User me = connected();
		User user = User.findById(id);
		user.update(email, password, me);
		user.name = name;
		user.zone = Zone.findById(zone);
		user.mobile = mobile;
		user.log.updateBy(connected());
		user.attachAvatar(avatar);
		user.save();
		get(id);
	}

	public static void delete(Long id) {
		User user = User.findById(id);
		user.status = Constant.INACTIVE;
		user.log.updateBy(connected());
		user.save();
		index(null, 1, 10);
	}
}
