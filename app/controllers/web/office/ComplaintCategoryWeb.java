package controllers.web.office;

import java.util.ArrayList;
import java.util.List;

import models.Constant;
import models.ListPaginator;
import models.Util;
import models.db.ComplaintCategory;
import models.db.Department;
import models.db.User;
import controllers.BaseOffice;

public class ComplaintCategoryWeb extends BaseOffice {

	public static void index(String keyword, Long department, int page,
			int length) {

		length = Util.length(length);
		Department _department = null;
		if (department != null)
			_department = Department.findById(department);
		ListPaginator<ComplaintCategory> categories = new ListPaginator<ComplaintCategory>(
				ComplaintCategory.search(keyword, _department, page, length),
				ComplaintCategory.count(keyword, _department));
		categories.setPageSize(length);

		List<Department> departments = Department.search(null, 1, 99999);
		List<Department> sdepartments = new ArrayList<Department>(departments);
		Department allDepartment = new Department();
		allDepartment.id = null;
		allDepartment.name = "All";
		sdepartments.add(0, allDepartment);
		render(categories, keyword, department, departments, sdepartments);
	}

	public static void get(Long id) {
		ComplaintCategory category = ComplaintCategory.findById(id);
		render(category);
	}

	public static void create(String name, String mbpjName, String mbpjID,
			Long department, int slaDays) {
		if (isGET())
			render();
		User me = connected();
		ComplaintCategory category = ComplaintCategory.create(name, mbpjID,
				(Department) Department.findById(department), slaDays, me);
		category.mbpjName = mbpjName;
		category.save();
		index(null, null, 1, 10);
	}

	public static void edit(Long id) {
		ComplaintCategory category = ComplaintCategory.findById(id);
		List<Department> departments = Department.search(null, 1, 99999);
		render(category, departments);
	}

	public static void update(Long id, String name, String mbpjName,
			String mbpjID, Long department, int slaDays) {
		User me = connected();
		Department _deparment = Department.findById(department);
		ComplaintCategory category = ComplaintCategory.findById(id);
		category.update(name, mbpjID, _deparment, slaDays, me);
		category.mbpjName = mbpjName;
		category.save();
		get(id);
	}

	public static void delete(Long id) {
		ComplaintCategory category = ComplaintCategory.findById(id);
		category.status = Constant.INACTIVE;
		category.log.updateBy(connected());
		category.save();
		index(null, null, 1, 10);
	}

}
