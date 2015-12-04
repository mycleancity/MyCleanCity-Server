package controllers.web.office;

import java.util.List;

import models.db.*;
import play.data.Upload;
import models.ListPaginator;
import models.Util;
import controllers.BaseOffice;

public class CulpritWeb extends BaseOffice {

	public static void index(String keyword, Integer status, Long category,
			int page, int length) {
		length = Util.length(length);
		CulpritCategory _category = null;
		if (category != null)
			_category = CulpritCategory.findById(category);
		ListPaginator<Culprit> culprits = new ListPaginator(Culprit.search(
				null, _category, status, keyword, page, length), Culprit.count(
				null, _category, status, keyword));
		culprits.setPageSize(length);
		List<CulpritCategory> categories = CulpritCategory.search(null, 1,
				99999);
		CulpritCategory allCategory = new CulpritCategory();
		allCategory.id = null;
		allCategory.name = "All";
		categories.add(0, allCategory);
		render(culprits, status, category, categories, keyword);
	}

	public static void get(Long id) {
		Culprit culprit = Culprit.findById(id);
		render(culprit);
	}

	public static void pick(Long id) {
		Culprit culprit = Culprit.findById(id);
		CMS cms = CMS.findOnlyOne();
		cms.pickedCulprit = culprit;
		cms.save();
		get(id);
	}

	public static void approve(Long id, Long zone) {
		Culprit culprit = Culprit.findById(id);
		if (isGET()) {
			List<Zone> zones = Zone.search(null, 1, 999999);
			zones.add(0, new Zone(null));
			render(culprit, zones);
		}
		culprit.approve(connected(), (Zone) Zone.findById(zone));
		get(id);
	}

	public static void blacklist(Long id) {
		Culprit culprit = Culprit.findById(id);
		culprit.blacklist(connected());
		get(id);
	}

	public static void settle(Long id) {
		Culprit culprit = Culprit.findById(id);
		culprit.settle(connected());
		get(id);
	}

	public static void delete(Long id) {
		Culprit culprit = Culprit.findById(id);
		culprit.delete(connected());
		index(null, null, null, 1, 10);
	}

	public static void edit(Long id) {
		Culprit culprit = Culprit.findById(id);
		List<CulpritCategory> categories = CulpritCategory.search(null, 1,
				99999);
		render(culprit, categories);
	}

	public static void update(Long id, String youtubeLink, String description,
			Long category, String contactName, String contactEmail,
			String contactNo, String address) {
		User me = connected();
		Culprit culprit = Culprit.findById(id);
		CulpritCategory _category = null;
		if (category != null)
			_category = CulpritCategory.findById(id);
		else
			_category = culprit.category;
		culprit.update(youtubeLink, description, _category, contactName,
				contactEmail, contactNo, address, me);
		get(id);
	}
}
