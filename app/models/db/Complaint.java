package models.db;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import models.Constant;
import models.Util;
import models.job.ZoneFinderJob;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.Type;

import play.db.jpa.Model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import controllers.Mails;

@Entity
public class Complaint extends Model {

	private static GeometryFactory GF = new GeometryFactory();

	public String contactName;
	public String contactEmail;
	public String contactNo;
	public String caption;
	public String address;
	public int commentCount;
	public int status = Constant.SLA_PENDING_MODERATE;
	public int slaAcceptDays;
	public int slaProceedDays;
	public Date slaAcceptDate;
	public Date slaProceedDate;
	public Date slaInvalidDate;
	public Date slaResolveDate;
	public String invalidReason;

	@Type(type = "org.hibernatespatial.GeometryUserType")
	@Column(columnDefinition = "Point", nullable = false)
	public Point location;

	@Lob
	public String story;

	@ManyToOne
	public Media photo;

	@ManyToOne
	public ComplaintCategory category;

	@ManyToOne
	public Department department;

	@ManyToOne
	public Zone zone;

	@ManyToOne
	public Area area;

	@ManyToOne
	public Road road;

	@Embedded
	public Log log;

	public boolean mppjValidated;
	public boolean mppjFinished;
	public String complaintID;
	public String mppjStatus;
	public int mppjCounter;

	public Complaint() {

	}

	private Complaint(String contactName, String contactEmail,
			String contactNo, String caption, String story, String address,
			Media photo, ComplaintCategory category, Double latitude,
			Double longitude, User createdBy) {
		this.contactName = contactName;
		this.contactEmail = contactEmail;
		this.contactNo = contactNo;
		this.caption = caption;
		this.story = story;
		this.address = address;
		this.photo = photo;
		this.category = category;
		if (this.category != null)
			this.department = this.category.department;
		this.location = GF.createPoint(new Coordinate(latitude, longitude));
		this.log = new Log(createdBy);
	}

	public static Complaint create(String contactName, String contactEmail,
			String contactNo, String caption, String story, String address,
			Media photo, ComplaintCategory category, Double latitude,
			Double longitude, User createdBy) {
		Complaint complaint = new Complaint(contactName, contactEmail,
				contactNo, caption, story, address, photo, category, latitude,
				longitude, createdBy);
		complaint.save();
		new ZoneFinderJob(complaint.id, null).in(5);
		return complaint;
	}

	public Complaint update(String caption, String story,
			ComplaintCategory category, String contactName,
			String contactEmail, String contactNo, String address,
			User updatedBy) {
		this.caption = caption;
		this.story = story;
		this.category = category;
		if (this.category != null)
			this.department = this.category.department;
		this.contactName = contactName;
		this.contactEmail = contactEmail;
		this.contactNo = contactNo;
		this.address = address;
		this.log.updateBy(updatedBy);
		return save();
	}

	public static List<Complaint> search(Zone zone, ComplaintCategory category,
			Integer status, String keyword, boolean onGoingOnly, int page,
			int length) {
		length = Util.length(length);
		return query("SELECT c", zone, category, status, keyword, onGoingOnly)
				.fetch(page, length);
	}

	public static int count(Zone zone, ComplaintCategory category,
			Integer status, String keyword, boolean onGoingOnly) {
		return Integer.valueOf(query("SELECT count(*)", zone, category, status,
				keyword, onGoingOnly).first().toString());
	}

	private static JPAQuery query(String select, Zone zone,
			ComplaintCategory category, Integer status, String keyword,
			boolean onGoingOnly) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM Complaint c WHERE 1 = 1 ");
		if (zone != null)
			sql.append("AND c.zone = :zone ");
		if (category != null)
			sql.append("AND c.category = :category ");

		if (status != null && status == -1) {
			sql.append("AND c.mppjStatus LIKE :mppjStatus ");
		} else {
			if (onGoingOnly) {
				sql.append("AND c.status >= " + Constant.SLA_APPROVED
						+ " AND c.status <= " + Constant.SLA_IN_PROGRESS + " ");
			} else {
				if (status != null)
					sql.append("AND c.status = :status ");
				else
					sql.append("AND NOT (c.status = :deleted OR c.status = :rejected) ");
			}
		}

		if (StringUtils.isNotEmpty(keyword)) {
			sql.append("AND (c.caption like :caption OR c.complaintID like :complaintID) ");
		}
		sql.append("ORDER BY c.log.createdAt DESC ");

		JPAQuery query = Complaint.find(select + sql.toString());
		if (status != null && status == -1) {
			query.bind("mppjStatus", "%soap:ServerServer%");
		} else {
			if (!onGoingOnly) {
				if (status != null)
					query.setParameter("status", status);
				else {
					query.setParameter("deleted", Constant.SLA_DELETED);
					query.setParameter("rejected", Constant.SLA_REJECTED);
				}
			}
		}
		if (zone != null)
			query.bind("zone", zone);
		if (category != null)
			query.bind("category", category);
		if (StringUtils.isNotEmpty(keyword)) {
			query.bind("caption", "%" + keyword + "%");
			query.bind("complaintID", "%" + keyword + "%");
		}
		return query;
	}

	public String getStatusDisplay() {
		if (status == 0)
			return "Pending Moderation";
		else if (status == 1)
			return "Approved";
		else if (status == 2)
			return "In Progress";
		else if (status == 3)
			return "Invalid";
		else if (status == 4)
			return "Resolved";
		else if (status == 5)
			return "Rejected";
		else
			return "Unknown";
	}

	public String getStatusDisplayShort() {
		if (status == 0)
			return "Pending";
		else if (status == 1)
			return "Approved";
		else if (status == 2)
			return "In Progress";
		else if (status == 3)
			return "Invalid";
		else if (status == 4)
			return "Resolved";
		else if (status == 5)
			return "Rejected";
		else
			return "Unknown";
	}

	public String getDepartmentStatusDisplay() {
		if (status == 0)
			return "Pending Moderation";
		else if (status == 1)
			return "Pending";
		else if (status == 2)
			return "In Progress";
		else if (status == 3)
			return "Invalid";
		else if (status == 4)
			return "Resolved";
		else if (status == 5)
			return "Rejected";
		else
			return "Unknown";
	}

	public void approve(User updatedBy, Zone zone, Area area, Road road) {
		status = Constant.SLA_APPROVED;
		slaAcceptDays = Constant.DAY_SLA_DEPARTMENT_ACCEPT;
		slaAcceptDate = new Date();
		log.updateBy(updatedBy);
		this.zone = zone;
		this.area = area;
		this.road = road;
		save();

		// Notify Department Officer
		if (category != null && category.department != null
				&& StringUtils.isNotEmpty(category.department.email)) {
			Mails.approveComplaint(category.department, this);
		}
	}

	public void reject(User updatedBy) {
		status = Constant.SLA_REJECTED;
		log.updateBy(updatedBy);
		save();
	}

	public void proceed(int days, User updatedBy) {
		status = Constant.SLA_IN_PROGRESS;
		slaProceedDays = days;
		slaProceedDate = new Date();
		log.updateBy(updatedBy);
		save();
	}

	public void invalid(String reason, User updatedBy) {
		status = Constant.SLA_INVALID;
		slaInvalidDate = new Date();
		invalidReason = reason;
		log.updateBy(updatedBy);
		save();
	}

	public void resolve(User updatedBy) {
		status = Constant.SLA_RESOLVED;
		slaResolveDate = new Date();
		log.updateBy(updatedBy);
		save();
	}

	public void delete(User updatedBy) {
		status = Constant.SLA_DELETED;
		log.updateBy(updatedBy);
		save();
	}

	public Complaint delete() {
		if (photo != null) {
			Media media = photo;
			photo = null;
			save();
			media.delete();
		}
		return super.delete();
	}

	public int getSlaAcceptLeftoverDays() {
		return 0;
		// if (slaAcceptDate == null)
		// return 0;
		//
		// Date NOW = new Date();
		// Date BEFORE = slaAcceptDate;
		// NOW = DateUtils.truncate(NOW, Calendar.DATE);
		// BEFORE = DateUtils.truncate(BEFORE, Calendar.DATE);
		// long diff = NOW.getTime() - BEFORE.getTime();
		// int days = (int) TimeUnit.MILLISECONDS.toDays(diff);
		// return slaAcceptDays - days;
	}

	public int getSlaProceedLeftoverDays() {
		if (status == Constant.SLA_IN_PROGRESS) {

			if (slaProceedDate == null)
				return 0;

			Date NOW = new Date();
			Date BEFORE = slaProceedDate;
			NOW = DateUtils.truncate(NOW, Calendar.DATE);
			BEFORE = DateUtils.truncate(BEFORE, Calendar.DATE);
			long diff = (NOW.getTime() - BEFORE.getTime());
			if (diff < 0)
				diff = -diff;
			int days = (int) TimeUnit.MILLISECONDS.toDays(diff);
			return slaProceedDays - days;
		}
		return 0;
	}

	public int getSlaProceedLeftoverPercent() {
		if (slaProceedDate == null)
			return 0;

		Date NOW = new Date();
		Date BEFORE = slaProceedDate;
		NOW = DateUtils.truncate(NOW, Calendar.DATE);
		BEFORE = DateUtils.truncate(BEFORE, Calendar.DATE);
		long diff = NOW.getTime() - BEFORE.getTime();
		if (diff < 0)
			diff = -diff;
		int days = (int) TimeUnit.MILLISECONDS.toDays(diff);
		if (days == 0)
			return 0;
		double percent = ((double) days / (double) slaProceedDays) * 100.0;
		return (int) percent;
	}

	public static List<Complaint> searchByDepartment(Department department,
			Integer status, String keyword, int page, int length) {
		length = Util.length(length);
		return query("SELECT c", department, status, keyword).fetch(page,
				length);
	}

	public static int countByDepartment(Department department, Integer status,
			String keyword) {
		return Integer.valueOf(query("SELECT count(*)", department, status,
				keyword).first().toString());
	}

	private static JPAQuery query(String select, Department department,
			Integer status, String keyword) {
		List<ComplaintCategory> categories = ComplaintCategory.search(null,
				department, 1, 99999);
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM Complaint c WHERE c.category IN :categories ");
		if (status != null)
			sql.append("AND c.status = :status ");
		else
			sql.append("AND c.status > 0 ");
		if (StringUtils.isNotEmpty(keyword))
			sql.append("AND c.caption like :keyword ");
		sql.append("ORDER BY c.log.createdAt DESC ");

		JPAQuery query = Complaint.find(select + sql.toString());
		query.bind("categories", categories);
		if (status != null)
			query.setParameter("status", status);
		if (StringUtils.isNotEmpty(keyword))
			query.bind("keyword", "%" + keyword + "%");
		return query;
	}

	public String getRef() {
		if (StringUtils.isNotEmpty(complaintID))
			return complaintID;
		return "PENDING";
	}
}
