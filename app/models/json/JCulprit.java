package models.json;

import java.util.Date;

import models.Constant;
import models.db.Culprit;

public class JCulprit extends JModel {

	public String fullname;
	public String contactEmail;
	public String contactNo;
	public String description;
	public String address;
	public double lat;
	public double longi;
	public Long photo;
	public JCulpritCategory category;
	public String youtubelink;
	public boolean pubName;
	public boolean repeat_offender;
	public int commentCount;
	public Date create_date;
	public int status;
	public int slaTotalDays;
	public int slaLeftoverDays;
	public JUser user;
	public String statusDisplay;

	public JCulprit(Culprit culprit) {
		super(culprit);
		this.fullname = culprit.contactName;
		this.contactEmail = culprit.contactEmail;
		this.contactNo = culprit.contactNo;
		this.description = culprit.story;
		this.address = culprit.address;
		this.lat = culprit.location.getX();
		this.longi = culprit.location.getY();
		this.photo = culprit.photo.id;
		this.category = JModel.from(culprit.category, JCulpritCategory.class);
		this.youtubelink = culprit.youtubeLink;
		this.pubName = culprit.allowPublishName;
		this.repeat_offender = culprit.isRepeatOffender;
		this.commentCount = culprit.commentCount;
		this.create_date = culprit.log.createdAt;
		this.status = culprit.status;
		this.slaTotalDays = 20;
		this.slaLeftoverDays = 15;
		this.user = JModel.from(culprit.log.createdBy, JUser.class);
		this.statusDisplay = culprit.getStatusDisplay();
	}
}
