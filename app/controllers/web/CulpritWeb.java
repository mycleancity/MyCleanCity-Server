package controllers.web;

import java.util.List;

import models.ListPaginator;
import models.Secured;
import models.Util;
import models.db.Comment;
import models.db.ComplaintCategory;
import models.db.Culprit;
import models.db.CulpritCategory;
import models.db.Media;
import models.db.User;
import models.db.Zone;
import play.data.Upload;
import controllers.BaseWeb;

public class CulpritWeb extends BaseWeb {

	public static void index(Long cat, Long zone, int page, int length) {

		length = Util.length(length);
		CulpritCategory _category = null;
		if (cat != null)
			_category = CulpritCategory.findById(cat);
		Zone _zone = null;
		if (zone != null)
			_zone = Zone.findById(zone);
		ListPaginator<Culprit> culprits = new ListPaginator<Culprit>(
				Culprit.search(_zone, _category, null, null, page, length),
				Culprit.count(_zone, _category, null, null));
		culprits.setPageSize(length);
		List<CulpritCategory> categories = CulpritCategory.search(null, 1,
				99999);
		render(culprits, _category, categories, _zone);
	}

	public static void get(Long id) {
		Culprit culprit = Culprit.findById(id);
		List<CulpritCategory> categories = CulpritCategory.search(null, 1,
				99999);
		List<Comment> comments = Comment.findAll(culprit, null, 99999);
		String url = "http://" + request.host + request.url;
		render(culprit, categories, comments, url);
	}

	@Secured
	public static void create(String email, String name, String mobile,
			String description, Long category, String address, Double latitude,
			Double longitude, Upload photo, String youtubeLink,
			boolean allowPublishName, boolean isRepeatOffender) {
		if (isGET()) {
			List<CulpritCategory> categories = CulpritCategory.search(null, 1,
					99999);
			render(categories);
		} else {
			User me = connected();
			Culprit culprit = new Culprit(name, email, mobile, description,
					address, Media.create(photo, me),
					(CulpritCategory) CulpritCategory.findById(category),
					latitude, longitude, youtubeLink, allowPublishName,
					isRepeatOffender, me).save();
			get(culprit.id);
		}
	}

	@Secured
	public static void comment(Long id, String message) {
		User me = connected();
		Culprit culprit = Culprit.findById(id);
		Comment.create(culprit, message, me);
		get(id);
	}
}
