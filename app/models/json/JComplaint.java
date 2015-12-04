package models.json;

import java.util.Date;

import models.Constant;
import models.db.Complaint;

public class JComplaint extends JModel {

	public String fullname;
	public String contactEmail;
	public String contactNo;
	public String title;
	public String description;
	public String address;
	public double lat;
	public double longi;
	public Long photo;
	public JComplaintCategory category;
	public int commentCount;
	public Date create_date;
	public int status;
	public int slaTotalDays;
	public int slaLeftoverDays;
	public JUser user;
	public String ref;

	public JComplaint(Complaint complaint) {
		super(complaint);
		this.fullname = complaint.contactName;
		this.contactEmail = complaint.contactEmail;
		this.contactNo = complaint.contactNo;
		this.title = complaint.caption;
		this.description = complaint.story;
		this.address = complaint.address;
		this.lat = complaint.location.getX();
		this.longi = complaint.location.getY();
		this.photo = complaint.photo.id;
		this.category = JModel.from(complaint.category,
				JComplaintCategory.class);
		this.commentCount = complaint.commentCount;
		this.create_date = complaint.log.createdAt;
		this.status = complaint.status;
		this.ref = complaint.complaintID;
		if (complaint.status == Constant.SLA_APPROVED) {
			this.slaTotalDays = complaint.slaAcceptDays;
			this.slaLeftoverDays = 0;// complaint.getSlaAcceptLeftoverDays();
		}
		if (complaint.status >= Constant.SLA_IN_PROGRESS
				&& complaint.status <= Constant.SLA_RESOLVED) {
			this.slaTotalDays = complaint.slaProceedDays;
			this.slaLeftoverDays = complaint.getSlaProceedLeftoverDays();
		}
		this.user = JModel.from(complaint.log.createdBy, JUser.class);
	}
}
