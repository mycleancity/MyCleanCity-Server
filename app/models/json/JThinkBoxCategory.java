package models.json;

import models.db.ComplaintCategory;
import models.db.ThinkBoxCategory;

public class JThinkBoxCategory extends JModel {

	public String name;

	public JThinkBoxCategory(ThinkBoxCategory category) {
		super(category);
		this.name = category.name;
	}
}
