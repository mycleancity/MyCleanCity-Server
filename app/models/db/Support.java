package models.db;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.Constant;
import models.Util;
import play.db.jpa.Model;

@Entity
public class Support extends Model {

	public String contactName;
	public String contactEmail;
	public String contactNo;
	public String status = Constant.ACTIVE;

	@ManyToOne
	public ThinkBox thinkBox;

	@Embedded
	public Log log;

	public static Support create(String contactName, String contactEmail,
			String contactNo, ThinkBox thinkBox, User createdBy) {
		Support support = new Support();
		support.contactName = contactName;
		support.contactEmail = contactEmail;
		support.contactNo = contactNo;
		support.thinkBox = thinkBox;
		support.log = new Log(createdBy);
		support.save();

		thinkBox.supportCount++;
		thinkBox.save();
		return support;
	}

	public static boolean isSupported(ThinkBox thinkBox, User user) {
		if (user == null)
			return false;
		return Support.find("byThinkBoxAndLog.createdBy", thinkBox, user)
				.first() != null;
	}

	public static List<Support> search(ThinkBox thinkBox, int page, int length) {
		length = Util.length(length);
		return query("SELECT c", thinkBox).fetch(page, length);
	}

	public static int count(ThinkBox thinkBox) {
		return Integer.valueOf(query("SELECT count(*)", thinkBox).first()
				.toString());
	}

	private static JPAQuery query(String select, ThinkBox thinkBox) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM Support c WHERE status = :status ");
		if (thinkBox != null)
			sql.append("AND c.thinkBox = :thinkBox ");
		sql.append("ORDER BY c.log.createdAt DESC ");

		JPAQuery query = Complaint.find(select + sql.toString());
		query.setParameter("status", Constant.ACTIVE);
		if (thinkBox != null)
			query.bind("thinkBox", thinkBox);
		return query;
	}
}
