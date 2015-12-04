package controllers.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Constant;
import models.db.Department;
import play.db.jpa.Model;
import controllers.BaseWeb;

public class ReportCardWeb extends BaseWeb {

	public static void index() {
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		StringBuffer query = new StringBuffer();
		query.append("SELECT d.id, ");
		query.append("SUM(c.status = 1) AS Received, ");
		query.append("SUM(c.status = 2) AS InProgress, ");
		query.append("SUM(c.status = 4) AS Resolved, ");
		query.append("SUM(c.status = 2 && c.slaProceedDays - DATEDIFF(CURDATE(),c.slaProceedDate) < 0) / SUM(c.status = 2) * 100 AS DelayedPrecentage ");
		query.append("FROM Complaint c RIGHT JOIN Department d ON c.department_id = d.id GROUP BY c.department_id");

		List<Object[]> data = Model.em().createNativeQuery(query.toString())
				.getResultList();

		for (Object[] o : data) {
			Department department = Department.findById(Long.parseLong(o[0]
					.toString()));
			if (department.status.equalsIgnoreCase(Constant.ACTIVE)) {
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
					DelayedPercentage = 0;

				Map<String, Object> result = new HashMap<String, Object>();
				result.put("DepartmentID", department.id);
				result.put("DepartmentName", department.name);
				if (department.head != null)
					result.put("Head", department.head.name);
				if (department.head.avatar != null)
					result.put("Photo", department.head.avatar.url("400"));
				result.put("Received", Received);
				result.put("InProgress", InProgress);
				result.put("Resolved", Resolved);
				result.put("Delayed", DelayedPercentage);
				results.add(result);
			}
		}
		render(results);
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

		Map<String, Object> o = new HashMap<String, Object>();
		o.put("DepartmentID", department.id);
		o.put("DepartmentName", department.name);
		if (department.head != null)
			o.put("Head", department.head.name);
		if (department.head.avatar != null)
			o.put("Photo", department.head.avatar);
		o.put("results", results);
		render(o);
	}
}
