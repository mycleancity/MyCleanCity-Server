package models.db;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import models.Constant;
import models.Util;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.Model;

@Entity
public class ThinkBox extends Model {

	public static int F1Years = 1;
	public static int F2Years = 2;
	public static int F3Years = 3;

	public String contactName;
	public String contactEmail;
	public String contactNo;
	public String caption;
	public int feasibility;
	public int supportCount;
	public int commentCount;
	public int kickoffCount = 40;
	public int status = Constant.THINKBOX_PENDING_MODERATE;
	@Transient
	public boolean supported;

	@Lob
	public String story;

	@ManyToOne
	public Zone zone;

	@ManyToOne
	public Media photo;

	@ManyToOne
	public ThinkBoxCategory category;

	@Embedded
	public Log log;

	public static ThinkBox create(String contactName, String contactEmail,
			String contactNo, String caption, String story, int feasibility,
			Zone zone, ThinkBoxCategory category, Media photo, User createdBy) {
		ThinkBox thinkBox = new ThinkBox();
		thinkBox.contactName = contactName;
		thinkBox.contactEmail = contactEmail;
		thinkBox.contactNo = contactNo;
		thinkBox.caption = caption;
		thinkBox.story = story;
		thinkBox.feasibility = feasibility;
		thinkBox.zone = zone;
		thinkBox.category = category;
		thinkBox.photo = photo;
		thinkBox.kickoffCount = CMS.findOnlyOne().thinkboxKickOffCount;
		thinkBox.log = new Log(createdBy);
		return thinkBox.save();
	}

	public ThinkBox update(String contactName, String contactEmail,
			String contactNo, String caption, String story, int feasibility,
			Zone zone, ThinkBoxCategory category, Media photo, User updatedBy) {
		this.contactName = contactName;
		this.contactEmail = contactEmail;
		this.contactNo = contactNo;
		this.caption = caption;
		this.story = story;
		this.feasibility = feasibility;
		this.zone = zone;
		this.category = category;
		this.log.updateBy(updatedBy);
		if (photo != null)
			this.photo = photo;
		return save();
	}

	public String getStatusDisplay() {
		if (status == Constant.THINKBOX_PENDING_MODERATE)
			return "Pending Moderation";
		else if (status == Constant.THINKBOX_APPROVED)
			return "Approved";
		else if (status == Constant.THINKBOX_SELECTED)
			return "Idea selected to be presented";
		else if (status == Constant.THINKBOX_PRESENTED)
			return "Ideas presented";
		else
			return "Unknown";
	}

	public String getFeasibilityDisplay() {
		if (feasibility == 1)
			return "Can be implemented immediately";
		else if (feasibility == 2)
			return "Can be implemented within 1 to 2 Years";
		else
			return "Requires more than 3 Years";
	}

	public static List<ThinkBox> search(ThinkBoxCategory category, Zone zone,
			String keyword, Integer status, int page, int length) {
		length = Util.length(length);
		return query("SELECT c", category, zone, keyword, status).fetch(page,
				length);
	}

	public static int count(ThinkBoxCategory category, Zone zone,
			String keyword, Integer status) {
		return Integer.valueOf(query("SELECT count(*)", category, zone,
				keyword, status).first().toString());
	}

	private static JPAQuery query(String select, ThinkBoxCategory category,
			Zone zone, String keyword, Integer status) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM ThinkBox c WHERE 1 = 1 ");
		if (status != null)
			sql.append("AND c.status = :status ");
		else
			sql.append("AND c.status != :deleted ");
		if (category != null)
			sql.append("AND c.category = :category ");
		if (zone != null)
			sql.append("AND c.zone = :zone ");
		if (StringUtils.isNotEmpty(keyword))
			sql.append("AND c.caption like :keyword ");
		sql.append("ORDER BY c.log.createdAt DESC ");

		JPAQuery query = Complaint.find(select + sql.toString());
		if (status != null)
			query.setParameter("status", status);
		else {
			query.setParameter("deleted", Constant.THINKBOX_DELETED);
		}
		if (category != null)
			query.bind("category", category);
		if (zone != null)
			query.bind("zone", zone);
		if (StringUtils.isNotEmpty(keyword))
			query.bind("keyword", "%" + keyword + "%");
		return query;
	}

	public void approve(User updatedBy) {
		status = Constant.THINKBOX_APPROVED;
		log.updateBy(updatedBy);
		save();
	}

	public void select(User updatedBy) {
		status = Constant.THINKBOX_SELECTED;
		log.updateBy(updatedBy);
		save();
	}

	public void delete(User updatedBy) {
		status = Constant.THINKBOX_DELETED;
		log.updateBy(updatedBy);
		save();
	}

	public void presented(User updatedBy) {
		status = Constant.THINKBOX_PRESENTED;
		log.updateBy(updatedBy);
		save();
	}

	public double getPercentage() {
		return supportCount / kickoffCount;
	}
}
