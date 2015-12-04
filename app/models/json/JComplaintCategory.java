package models.json;

import models.db.ComplaintCategory;

public class JComplaintCategory extends JModel {

	public String name;

	public JComplaintCategory(ComplaintCategory category) {
		super(category);
		this.name = category.name;
	}
}
