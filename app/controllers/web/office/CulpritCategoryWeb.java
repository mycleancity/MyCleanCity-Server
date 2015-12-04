package controllers.web.office;

import java.util.ArrayList;
import java.util.List;

import models.Constant;
import models.ListPaginator;
import models.Util;
import models.db.ComplaintCategory;
import models.db.CulpritCategory;
import models.db.Department;
import models.db.User;
import controllers.BaseOffice;

public class CulpritCategoryWeb extends BaseOffice {

	public static void index(String keyword, int page, int length) {

		length = Util.length(length);
		ListPaginator<CulpritCategory> categories = new ListPaginator<CulpritCategory>(
				CulpritCategory.search(keyword, page, length),
				CulpritCategory.count(keyword));
		categories.setPageSize(length);
		render(categories, keyword);
	}

	public static void get(Long id) {
		CulpritCategory category = CulpritCategory.findById(id);
		render(category);
	}

	public static void create(String name) {
		if (isGET())
			render();
		User me = connected();
		CulpritCategory.create(name, me);
		index(null, 1, 10);
	}

	public static void edit(Long id) {
		CulpritCategory category = CulpritCategory.findById(id);
		render(category);
	}

	public static void update(Long id, String name) {
		User me = connected();
		CulpritCategory category = CulpritCategory.findById(id);
		category.update(name, me);
		get(id);
	}

	public static void delete(Long id) {
		CulpritCategory category = CulpritCategory.findById(id);
		category.status = Constant.INACTIVE;
		category.log.updateBy(connected());
		category.save();
		index(null, 1, 10);
	}

}
