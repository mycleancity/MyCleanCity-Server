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
public class Zone extends Model {

	public String zid;
	public String name;
	public String story;
	public String status = Constant.ACTIVE;

	@Lob
	public String tags;

	@Embedded
	public Log log;

	public Zone() {

	}

	public Zone(Long id) {
		this.id = id;
		this.name = "";
	}

	public static Zone create(String zid, String name, String story,
			String tags, User createdBy) {
		Zone zone = new Zone();
		zone.zid = zid;
		zone.name = name;
		zone.story = story;
		if (StringUtils.isNotEmpty(tags))
			zone.tags = tags.toUpperCase();
		zone.log = new Log(createdBy);
		return zone.save();
	}

	public static Zone findByTags(String address) {
		if (StringUtils.isEmpty(address))
			return null;

		address = address.toUpperCase();
		List<Zone> zones = Zone.search(null, 1, 99999);
		for (Zone zone : zones) {
			String[] tags = zone.tags.split(",");
			for (String tag : tags) {
				tag = tag.trim().toUpperCase();
				if (address.matches("(?i).*" + tag + ".*"))
					return zone;
			}
		}
		return null;

		// StringBuilder sql = new StringBuilder();
		// sql.append("Select z FROM Zone z WHERE status = :status ");
		// if (StringUtils.isNotEmpty(tag))
		// sql.append("AND UPPER(z.tags) like :tag ");
		//
		// JPAQuery query = Complaint.find(sql.toString());
		// query.setParameter("status", Constant.ACTIVE);
		// if (StringUtils.isNotEmpty(tag))
		// query.bind("tag", "%_" + tag.toUpperCase() + "_%");
		// return query.first();
	}

	public static List<Zone> search(String keyword, int page, int length) {
		length = Util.length(length);
		return query("SELECT c", keyword).fetch(page, length);
	}

	public static int count(String keyword) {
		return Integer.valueOf(query("SELECT count(*)", keyword).first()
				.toString());
	}

	private static JPAQuery query(String select, String keyword) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM Zone c WHERE status = :status ");
		if (StringUtils.isNotEmpty(keyword))
			sql.append("AND c.name like :keyword ");
		sql.append("ORDER BY c.name ");

		JPAQuery query = Complaint.find(select + sql.toString());
		query.setParameter("status", Constant.ACTIVE);
		if (StringUtils.isNotEmpty(keyword))
			query.bind("keyword", "%" + keyword.toUpperCase() + "%");
		return query;
	}

	public String toString() {
		return name + ":" + tags;
	}

	public String getFullname() {
		return name + ": " + story;
	}
}
