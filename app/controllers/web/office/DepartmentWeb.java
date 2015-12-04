package controllers.web.office;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ListPaginator;
import models.Util;
import models.db.Department;
import models.db.User;
import play.db.jpa.Model;
import controllers.BaseOffice;

public class DepartmentWeb extends BaseOffice {

	public static void index(String keyword, int page, int length) {

		length = Util.length(length);
		ListPaginator<Department> departments = new ListPaginator<Department>(
				Department.search(keyword, page, length),
				Department.count(keyword));
		departments.setPageSize(length);
		render(departments, keyword);
	}

	public static void get(Long id) {
		Department department = Department.findById(id);

		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		StringBuffer query = new StringBuffer();
		query.append("SELECT DATE_FORMAT(c.createdAt, '%Y-%m') as Month, ");
		query.append("SUM(c.status = 1) AS Received, ");
		query.append("SUM(c.status = 2) AS InProgress, ");
		query.append("SUM(c.status = 4) AS Resolved, ");
		query.append("SUM(c.status = 2 && c.slaProceedDays - DATEDIFF(CURDATE(),c.slaProceedDate) < 0) / SUM(c.status = 2) * 100 AS DelayedPrecentage ");
		query.append("FROM Complaint c RIGHT JOIN Department d ON c.department_id = d.id ");
		query.append("WHERE d.id = " + id + " ");
		query.append("GROUP BY c.department_id, Month ");
		query.append("ORDER BY Month DESC LIMIT 6 ");

		List<Object[]> data = Model.em().createNativeQuery(query.toString())
				.getResultList();

		for (Object[] o : data) {
			String month = o[0].toString();
			Object Received = o[1];
			if (Received == null)
				Received = 0;
			Object InProgress = o[2];
			if (InProgress == null)
				InProgress = 0;
			Object Resolved = o[3];
			if (Resolved == null)
				Resolved = 0;
			Object DelayedPercentage = o[4];
			if (DelayedPercentage == null)
				DelayedPercentage = 0.0;

			Map<String, Object> result = new HashMap<String, Object>();
			result.put("Month", month);
			result.put("Received", Received);
			result.put("InProgress", InProgress);
			result.put("Resolved", Resolved);
			result.put("Delayed", DelayedPercentage);
			results.add(result);
		}
		render(department, results);
	}

	public static void create(String name, String email, Long head, Long officer) {
		if (isGET()) {
			List<User> heads = User.search(true, null, 1, 9999,
					User.OFFICER_HEAD);
			List<User> officers = User
					.search(true, null, 1, 9999, User.OFFICER);
			User empty = new User();
			empty.email = "";
			heads.add(0, empty);
			officers.add(0, empty);
			render(heads, officers);
		}
		User _head = null;
		if (head != null)
			_head = User.findById(head);
		User _officer = null;
		if (officer != null)
			_officer = User.findById(officer);
		Department.create(name, email, _head, _officer, connected());
		index(null, 1, 10);
	}

	public static void edit(Long id) {
		Department department = Department.findById(id);
		List<User> heads = User.search(true, null, 1, 9999, User.OFFICER_HEAD);
		List<User> officers = User.search(true, null, 1, 9999, User.OFFICER);

		if (department.head != null)
			heads.add(department.head);

		if (department.officers.size() > 0)
			officers.addAll(department.officers);

		User empty = new User();
		empty.email = "";
		heads.add(0, empty);
		officers.add(0, empty);
		render(department, heads, officers);
	}

	public static void update(Long id, String name, String email, Long head,
			Long officer) {
		User me = connected();
		User _head = null;
		if (head != null)
			_head = User.findById(head);
		User _officer = null;
		if (officer != null)
			_officer = User.findById(officer);
		Department department = Department.findById(id);
		department.update(name, email, _head, _officer, me);
		get(id);
	}

	public static void delete(Long id) {
		Department department = Department.findById(id);
		department.delete(connected());
		index(null, 1, 10);
	}
}
