package controllers.web.office;

import models.Constant;
import models.ListPaginator;
import models.Util;
import models.db.ThinkBoxCategory;
import models.db.User;
import controllers.BaseOffice;

public class ThinkBoxCategoryWeb extends BaseOffice {

	public static void index(String keyword, int page, int length) {

		length = Util.length(length);
		ListPaginator<ThinkBoxCategory> categories = new ListPaginator<ThinkBoxCategory>(
				ThinkBoxCategory.search(keyword, page, length),
				ThinkBoxCategory.count(keyword));
		categories.setPageSize(length);
		render(categories, keyword);
	}

	public static void get(Long id) {
		ThinkBoxCategory category = ThinkBoxCategory.findById(id);
		render(category);
	}

	public static void create(String name) {
		if (isGET())
			render();
		User me = connected();
		ThinkBoxCategory.create(name, me);
		index(null, 1, 10);
	}

	public static void edit(Long id) {
		ThinkBoxCategory category = ThinkBoxCategory.findById(id);
		render(category);
	}

	public static void update(Long id, String name) {
		User me = connected();
		ThinkBoxCategory category = ThinkBoxCategory.findById(id);
		category.update(name, me);
		get(id);
	}

	public static void delete(Long id) {
		ThinkBoxCategory category = ThinkBoxCategory.findById(id);
		category.status = Constant.INACTIVE;
		category.log.updateBy(connected());
		category.save();
		index(null, 1, 10);
	}

}
