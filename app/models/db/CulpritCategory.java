package models.db;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import models.Constant;
import models.Util;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.Model;

@Entity
public class CulpritCategory extends Model {

	public String name;

	public String status = Constant.ACTIVE;

	@Embedded
	public Log log;

	public static CulpritCategory create(String name, User createdBy) {
		CulpritCategory category = new CulpritCategory();
		category.name = name;
		category.log = new Log(createdBy);
		return category.save();
	}

	public CulpritCategory update(String name, User updatedBy) {
		if (StringUtils.isNotEmpty(name))
			this.name = name;
		log.updateBy(updatedBy);
		return save();
	}

	public static List<CulpritCategory> search(String keyword, int page,
			int length) {
		length = Util.length(length);
		return query("SELECT c", keyword).fetch(page, length);
	}

	public static int count(String keyword) {
		return Integer.valueOf(query("SELECT count(*)", keyword).first()
				.toString());
	}

	private static JPAQuery query(String select, String keyword) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM CulpritCategory c WHERE status = :status ");
		if (StringUtils.isNotEmpty(keyword))
			sql.append("AND c.name like :keyword ");
		sql.append("ORDER BY c.name ");

		JPAQuery query = Complaint.find(select + sql.toString());
		query.setParameter("status", Constant.ACTIVE);
		if (StringUtils.isNotEmpty(keyword))
			query.bind("keyword", "%" + keyword + "%");
		return query;
	}
}
