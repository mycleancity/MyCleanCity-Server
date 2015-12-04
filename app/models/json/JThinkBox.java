package models.json;

import java.util.Date;

import models.db.ThinkBox;

public class JThinkBox extends JModel {

	public String contactName;
	public String contactEmail;
	public String contactNo;
	public String title;
	public String description;
	public int feasibility;
	public int supportCount;
	public int commentCount;
	public int kickoffCount;
	public Long photo;
	public JThinkBoxCategory category;
	public Date create_date;
	public JUser user;
	public JZone zone;
	public boolean supported;
	public int status;
	public String statusDisplay;

	public JThinkBox(ThinkBox thinkBox) {
		super(thinkBox);
		this.contactName = thinkBox.contactName;
		this.contactEmail = thinkBox.contactEmail;
		this.contactNo = thinkBox.contactNo;
		this.title = thinkBox.caption;
		this.description = thinkBox.story;
		this.feasibility = thinkBox.feasibility;
		this.supportCount = thinkBox.supportCount;
		this.commentCount = thinkBox.commentCount;
		this.kickoffCount = thinkBox.kickoffCount;
		if (thinkBox.photo != null)
			this.photo = thinkBox.photo.id;
		this.category = JModel.from(thinkBox.category, JThinkBoxCategory.class);
		this.create_date = thinkBox.log.createdAt;
		this.user = JModel.from(thinkBox.log.createdBy, JUser.class);
		this.zone = JModel.from(thinkBox.zone, JZone.class);
		this.status = thinkBox.status;
		this.statusDisplay = thinkBox.getStatusDisplay();
	}
}
