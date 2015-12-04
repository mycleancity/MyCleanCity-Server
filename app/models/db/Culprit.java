package models.db;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import models.Constant;
import models.Util;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;

import play.db.jpa.Model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Entity
public class Culprit extends Model {

	private static GeometryFactory GF = new GeometryFactory();

	public String contactName;
	public String contactEmail;
	public String contactNo;
	public String address;
	public String youtubeLink;
	public boolean allowPublishName;
	public boolean isRepeatOffender;
	public int commentCount;
	public int status = Constant.CULPRIT_PENDING_MODERATE;
	public int slaTotalDays;
	public int slaLeftoverDays;

	@Type(type = "org.hibernatespatial.GeometryUserType")
	@Column(columnDefinition = "Point", nullable = false)
	public Point location;

	@Lob
	public String story;

	@ManyToOne
	public Media photo;

	@ManyToOne
	public Zone zone;

	@ManyToOne
	public CulpritCategory category;

	@Embedded
	public Log log;

	public Culprit(String contactName, String contactEmail, String contactNo,
			String story, String address, Media photo,
			CulpritCategory category, Double latitude, Double longitude,
			String youtubeLink, boolean allowPublishName,
			boolean isRepeatOffender, User createdBy) {
		this.contactName = contactName;
		this.contactEmail = contactEmail;
		this.contactNo = contactNo;
		this.story = story;
		this.address = address;
		this.photo = photo;
		this.category = category;
		this.youtubeLink = youtubeLink;
		this.allowPublishName = allowPublishName;
		this.isRepeatOffender = isRepeatOffender;
		this.location = GF.createPoint(new Coordinate(latitude, longitude));
		this.log = new Log(createdBy);
	}

	public Culprit update(String youtubeLink, String story,
			CulpritCategory category, String contactName, String contactEmail,
			String contactNo, String address, User updatedBy) {
		this.youtubeLink = youtubeLink;
		this.story = story;
		this.category = category;
		this.contactName = contactName;
		this.contactEmail = contactEmail;
		this.contactNo = contactNo;
		this.address = address;
		this.log.updateBy(updatedBy);
		return save();
	}

	public static List<Culprit> search(Zone zone, CulpritCategory category,
			Integer status, String keyword, int page, int length) {
		length = Util.length(length);
		return query("SELECT c", zone, category, status, keyword).fetch(page,
				length);
	}

	public static int count(Zone zone, CulpritCategory category,
			Integer status, String keyword) {
		return Integer.valueOf(query("SELECT count(*)", zone, category, status,
				keyword).first().toString());
	}

	private static JPAQuery query(String select, Zone zone,
			CulpritCategory category, Integer status, String keyword) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM Culprit c WHERE 1 = 1 ");
		if (zone != null)
			sql.append("AND c.zone = :zone ");
		if (category != null)
			sql.append("AND c.category = :category ");
		if (status != null)
			sql.append("AND c.status = :status ");
		else
			sql.append("AND c.status != :status ");
		if (StringUtils.isNotEmpty(keyword))
			sql.append("AND c.story like :keyword ");
		sql.append("ORDER BY c.log.createdAt DESC ");

		JPAQuery query = Complaint.find(select + sql.toString());
		if (status != null)
			query.setParameter("status", status);
		else
			query.setParameter("status", Constant.SLA_DELETED);
		if (zone != null)
			query.bind("zone", zone);
		if (category != null)
			query.bind("category", category);
		if (StringUtils.isNotEmpty(keyword))
			query.bind("keyword", "%" + keyword + "%");
		return query;
	}

	public String getShortStory() {
		return StringUtils.abbreviate(story, 50);
	}

	public String getSuperShortStory() {
		return StringUtils.abbreviate(story, 20);
	}

	public String getStatusDisplay() {
		if (status == Constant.CULPRIT_PENDING_MODERATE)
			return "Pending Moderation";
		else if (status == Constant.CULPRIT_APPROVED)
			return "Approved";
		else if (status == Constant.CULPRIT_BLACKLISTED)
			return "Blacklisted";
		else if (status == Constant.CULPRIT_SETTLED)
			return "Settled";
		else
			return "Unknown";
	}

	public void approve(User updatedBy, Zone zone) {
		status = Constant.CULPRIT_APPROVED;
		log.updateBy(updatedBy);
		this.zone = zone;
		save();
	}

	public void blacklist(User updatedBy) {
		status = Constant.CULPRIT_BLACKLISTED;
		log.updateBy(updatedBy);
		save();
	}

	public void delete(User updatedBy) {
		status = Constant.CULPRIT_DELETED;
		log.updateBy(updatedBy);
		save();
	}

	public void settle(User updatedBy) {
		status = Constant.CULPRIT_SETTLED;
		log.updateBy(updatedBy);
		save();
	}

	public Culprit delete() {
		if (photo != null) {
			Media media = photo;
			photo = null;
			save();
			media.delete();
		}
		return super.delete();
	}
}
