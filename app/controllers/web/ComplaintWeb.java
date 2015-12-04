package controllers.web;

import java.util.List;

import models.ListPaginator;
import models.Secured;
import models.Util;
import models.db.Comment;
import models.db.Complaint;
import models.db.ComplaintCategory;
import models.db.Media;
import models.db.User;
import models.db.Zone;
import play.data.Upload;
import controllers.BaseWeb;

public class ComplaintWeb extends BaseWeb {

	public static void index(Long cat, Long zone, int page, int length) {

		length = Util.length(length);
		ComplaintCategory _category = null;
		if (cat != null)
			_category = ComplaintCategory.findById(cat);
		Zone _zone = null;
		if (zone != null)
			_zone = Zone.findById(zone);
		ListPaginator<Complaint> complaints = new ListPaginator<Complaint>(
				Complaint.search(_zone, _category, null, null, false, page,
						length), Complaint.count(_zone, _category, null, null,
						false));
		complaints.setPageSize(length);
		List<ComplaintCategory> categories = ComplaintCategory.search(null,
				null, 1, 99999);
		render(complaints, _category, categories, _zone);
	}

	public static void get(Long id) {
		Complaint complaint = Complaint.findById(id);
		List<ComplaintCategory> categories = ComplaintCategory.search(null,
				null, 1, 99999);
		List<Comment> comments = Comment.findAll(complaint, null, 99999);
		String url = "http://" + request.host + request.url;
		render(complaint, categories, comments, url);
	}

	@Secured
	public static void create(String email, String name, String mobile,
			String title, String description, Long category, String address,
			Double latitude, Double longitude, Upload photo) {
		if (isGET()) {
			List<ComplaintCategory> categories = ComplaintCategory.search(null,
					null, 1, 99999);
			render(categories);
		} else {
			User me = connected();
			Complaint complaint = Complaint.create(name, email, mobile, title,
					description, address, Media.create(photo, me),
					(ComplaintCategory) ComplaintCategory.findById(category),
					latitude, longitude, me);
			get(complaint.id);
		}
	}

	@Secured
	public static void comment(Long id, String message) {
		User me = connected();
		Complaint complaint = Complaint.findById(id);
		Comment.create(complaint, message, me);
		get(id);
	}
}
