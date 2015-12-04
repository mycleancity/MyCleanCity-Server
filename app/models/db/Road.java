package models.db;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;

import models.Constant;
import models.Util;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.Model;

@Entity
public class Road extends Model {

	public String zid;
	public String name;
	public String status = Constant.ACTIVE;

	@Lob
	public String tags;

	@Embedded
	public Log log;

	public Road() {

	}

	public Road(Long id) {
		this.id = id;
		this.name = "";
	}

	public static Road create(String zid, String name, String tags,
			User createdBy) {
		Road zone = new Road();
		zone.zid = zid;
		zone.name = name;
		zone.tags = tags;
		zone.log = new Log(createdBy);
		return zone.save();
	}

	public static Road findByTags(String tag) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select z FROM Zone z WHERE status = :status ");
		if (StringUtils.isNotEmpty(tag))
			sql.append("AND UPPER(z.tags) like :tag ");

		JPAQuery query = Complaint.find(sql.toString());
		query.setParameter("status", Constant.ACTIVE);
		if (StringUtils.isNotEmpty(tag))
			query.bind("tag", "%" + tag + "%");
		return query.first();
	}

	public static List<Road> search(String keyword, int page, int length) {
		length = Util.length(length);
		return query("SELECT c", keyword).fetch(page, length);
	}

	public static int count(String keyword) {
		return Integer.valueOf(query("SELECT count(*)", keyword).first()
				.toString());
	}

	private static JPAQuery query(String select, String keyword) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM Road c WHERE status = :status ");
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
