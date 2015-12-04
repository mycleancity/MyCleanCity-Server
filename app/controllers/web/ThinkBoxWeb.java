package controllers.web;

import java.util.List;

import models.ListPaginator;
import models.Secured;
import models.Util;
import models.db.Comment;
import models.db.Media;
import models.db.Support;
import models.db.ThinkBox;
import models.db.ThinkBoxCategory;
import models.db.User;
import models.db.Zone;
import play.data.Upload;
import controllers.BaseWeb;

public class ThinkBoxWeb extends BaseWeb {

	public static void index(Long cat, Long zone, int page, int length) {

		length = Util.length(length);
		ThinkBoxCategory _category = null;
		if (cat != null)
			_category = ThinkBoxCategory.findById(cat);
		Zone _zone = null;
		if (zone != null)
			_zone = Zone.findById(zone);
		ListPaginator<ThinkBox> thinkBoxes = new ListPaginator<ThinkBox>(
				ThinkBox.search(_category, _zone, null, null, page, length),
				ThinkBox.count(_category, _zone, null, null));
		thinkBoxes.setPageSize(length);
		List<ThinkBoxCategory> categories = ThinkBoxCategory.search(null, 1,
				99999);
		render(thinkBoxes, _category, categories, _zone);
	}

	public static void get(Long id) {
		User me = connected();
		ThinkBox thinkbox = ThinkBox.findById(id);
		thinkbox.supported = Support.isSupported(thinkbox, me);
		List<ThinkBoxCategory> categories = ThinkBoxCategory.search(null, 1,
				99999);
		List<Comment> comments = Comment.findAll(thinkbox, null, 99999);
		List<Support> supporters = Support.search(thinkbox, 1, 99999);
		String url = "http://" + request.host + request.url;
		render(me, thinkbox, categories, comments, supporters, url);
	}

	@Secured
	public static void create(String email, String name, String mobile,
			String title, String description, Long category,
			Integer feasibility, Long zone, Upload photo) {

		if (isGET()) {
			List<ThinkBoxCategory> categories = ThinkBoxCategory.search(null,
					1, 99999);
			List<Zone> zones = Zone.search(null, 1, 99999);
			render(categories, zones);
		} else {
			User me = connected();
			ThinkBox thinkBox = ThinkBox.create(name, email, mobile, title,
					description, feasibility, (Zone) Zone.findById(zone),
					(ThinkBoxCategory) ThinkBoxCategory.findById(category),
					photo == null ? null : Media.create(photo, me), me);

			get(thinkBox.id);
		}
	}

	@Secured
	public static void comment(Long id, String message) {
		User me = connected();
		ThinkBox thinkBox = ThinkBox.findById(id);
		Comment.create(thinkBox, message, me);
		get(id);
	}

	@Secured
	public static void support(Long id, String name, String email, String mobile) {

		User me = connected();
		ThinkBox thinkBox = ThinkBox.findById(id);
		Support.create(name, email, mobile, thinkBox, me);
		get(id);
	}
}
