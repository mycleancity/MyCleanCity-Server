package models.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import models.Constant;
import models.Util;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.Model;

@Entity
public class Department extends Model {

	public String name;

	public String email;

	@ManyToOne
	public User head;

	public String status = Constant.ACTIVE;

	@ManyToMany
	@JoinTable(name = "Department_Officers")
	public List<User> officers = new ArrayList<User>();

	@Embedded
	public Log log;

	public static Department create(String name, String email, User head,
			User officer, User createdBy) {
		Department department = new Department();
		department.name = name;
		department.email = email;
		department.head = head;
		department.officers.add(officer);
		department.log = new Log(createdBy);
		department.save();

		if (head != null) {
			head.department = department;
			head.save();
		}

		if (officer != null) {
			officer.department = department;
			officer.save();
		}
		return department;
	}

	public Department update(String name, String email, User head,
			User officer, User updatedBy) {
		if (StringUtils.isNotEmpty(name))
			this.name = name;
		if (StringUtils.isNotEmpty(email))
			this.email = email;
		if (head != null) {
			if (this.head != null) {
				this.head.department = null;
				this.head.save();
			}
			this.head = head;
			this.head.department = this;
			this.head.save();
		}
		if (officer != null) {
			if (this.officers.size() > 0) {
				for (User _officer : this.officers) {
					_officer.department = null;
					_officer.save();
				}
			}
			this.officers.clear();
			this.officers.add(officer);
			officer.department = this;
			officer.save();
		}
		log.updateBy(updatedBy);
		return save();
	}

	public User getOfficer() {
		if (officers.size() > 0)
			return officers.get(0);
		return null;
	}

	public static List<Department> search(String keyword, int page, int length) {
		length = Util.length(length);
		return query("SELECT d", keyword).fetch(page, length);
	}

	public static int count(String keyword) {
		return Integer.valueOf(query("SELECT count(*)", keyword).first()
				.toString());
	}

	private static JPAQuery query(String select, String keyword) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM Department d WHERE status = :status ");
		if (StringUtils.isNotEmpty(keyword))
			sql.append("AND d.name like :keyword ");
		sql.append("ORDER BY d.name ");

		JPAQuery query = Complaint.find(select + sql.toString());
		query.setParameter("status", Constant.ACTIVE);
		if (StringUtils.isNotEmpty(keyword))
			query.bind("keyword", "%" + keyword + "%");
		return query;
	}

	public Department delete(User updatedBy) {
		if (head != null) {
			head.department = null;
			head.save();
		}

		for (User officer : officers) {
			officer.department = null;
			officer.save();
		}
		status = Constant.INACTIVE;
		log.updateBy(updatedBy);
		return save();
	}

}
