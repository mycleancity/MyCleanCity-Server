package controllers.web.office;

import java.util.List;

import models.ListPaginator;
import models.Util;
import models.db.Area;
import models.db.CMS;
import models.db.Complaint;
import models.db.ComplaintCategory;
import models.db.Road;
import models.db.SOAP;
import models.db.ThinkBox;
import models.db.ThinkBoxCategory;
import models.db.User;
import models.db.Zone;
import models.job.MPPSRekodAduan;
import controllers.BaseOffice;

public class ThinkBoxWeb extends BaseOffice {

	public static void index(String keyword, Integer status, Long category,
			int page, int length) {
		length = Util.length(length);
		ThinkBoxCategory _category = null;
		if (category != null)
			_category = ThinkBoxCategory.findById(category);
		ListPaginator<ThinkBox> thinkBoxes = new ListPaginator(ThinkBox.search(
				_category, null, keyword, status, page, length),
				ThinkBox.count(_category, null, keyword, status));
		thinkBoxes.setPageSize(length);
		List<ThinkBoxCategory> categories = ThinkBoxCategory.search(null, 1,
				99999);
		ThinkBoxCategory allCategory = new ThinkBoxCategory();
		allCategory.id = null;
		allCategory.name = "All";
		categories.add(0, allCategory);
		render(thinkBoxes, status, category, categories, keyword);
	}

	public static void get(Long id) {
		ThinkBox thinkbox = ThinkBox.findById(id);
		render(thinkbox);
	}

	public static void approve(Long id, Long zone, Long area, Long road) {
		User me = connected();
		ThinkBox thinkBox = ThinkBox.findById(id);
		thinkBox.approve(me);
		get(id);
	}

	public static void select(Long id) {
		User me = connected();
		ThinkBox thinkBox = ThinkBox.findById(id);
		thinkBox.select(me);
		get(id);
	}

	public static void delete(Long id) {
		User me = connected();
		ThinkBox thinkBox = ThinkBox.findById(id);
		thinkBox.delete(me);
		index(null, null, null, 1, 10);
	}

	public static void present(Long id) {
		User me = connected();
		ThinkBox thinkBox = ThinkBox.findById(id);
		thinkBox.presented(me);
		get(id);
	}

	public static void edit(Long id) {
		ThinkBox thinkbox = ThinkBox.findById(id);
		List<ThinkBoxCategory> categories = ThinkBoxCategory.search(null, 1,
				99999);
		render(thinkbox, categories);
	}

	public static void pick(Long id) {
		ThinkBox thinkbox = ThinkBox.findById(id);
		CMS cms = CMS.findOnlyOne();
		cms.pickedThinkBox = thinkbox;
		cms.save();
		get(id);
	}

	public static void update(Long id, String title, String description,
			Long category, String contactName, String contactEmail,
			String contactNo) {
		User me = connected();
		ThinkBox thinkBox = ThinkBox.findById(id);
		thinkBox.update(contactName, contactEmail, contactNo, title,
				description, thinkBox.feasibility, thinkBox.zone,
				thinkBox.category, null, me);
		get(id);
	}

}
