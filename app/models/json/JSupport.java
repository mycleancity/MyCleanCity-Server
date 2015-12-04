package models.json;

import java.util.Date;

import models.db.Support;

public class JSupport extends JModel {

	public String contactName;
	public String contactEmail;
	public String contactNo;
	public Date create_date;

	public JSupport(Support support) {
		super(support);
		this.contactName = support.contactName;
		this.contactEmail = support.contactEmail;
		this.contactNo = support.contactNo;
		this.create_date = support.log.createdAt;
	}
}
