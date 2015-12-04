package models.db;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.Constant;
import models.Util;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.Model;

@Entity
public class ComplaintCategory extends Model {

	public String mbpjID = "0";

	public String mbpjName = "";

	public String name;

	public int slaDays;

	@ManyToOne
	public Department department;

	public String status = Constant.ACTIVE;

	@Embedded
	public Log log;

	public static ComplaintCategory create(String name, String mbpjID,
			Department department, int slaDays, User createdBy) {
		ComplaintCategory category = new ComplaintCategory();
		category.name = name;
		category.mbpjID = mbpjID;
		category.slaDays = slaDays;
		category.department = department;
		category.log = new Log(createdBy);
		return category.save();
	}

	public ComplaintCategory update(String name, String mbpjID,
			Department department, int slaDays, User updatedBy) {
		if (StringUtils.isNotEmpty(name))
			this.name = name;
		if (StringUtils.isNotEmpty(mbpjID))
			this.mbpjID = mbpjID;
		if (department != null) {
			if (this.department.id != department.id) {
				Model.em()
						.createNativeQuery(
								"UPDATE Complaint SET department_id = "
										+ department.id
										+ " WHERE category_id = " + id)
						.executeUpdate();
			}
			this.department = department;
		}
		this.slaDays = slaDays;
		log.updateBy(updatedBy);
		return save();
	}

	public static List<ComplaintCategory> search(String keyword,
			Department department, int page, int length) {
		length = Util.length(length);
		return query("SELECT c", keyword, department).fetch(page, length);
	}

	public static int count(String keyword, Department department) {
		return Integer.valueOf(query("SELECT count(*)", keyword, department)
				.first().toString());
	}

	private static JPAQuery query(String select, String keyword,
			Department department) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM ComplaintCategory c WHERE status = :status ");
		if (department != null)
			sql.append("AND c.department = :department ");
		if (StringUtils.isNotEmpty(keyword))
			sql.append("AND c.name like :keyword ");
		sql.append("ORDER BY c.name ");

		JPAQuery query = Complaint.find(select + sql.toString());
		query.setParameter("status", Constant.ACTIVE);
		if (department != null)
			query.bind("department", department);
		if (StringUtils.isNotEmpty(keyword))
			query.bind("keyword", "%" + keyword + "%");
		return query;
	}

}
