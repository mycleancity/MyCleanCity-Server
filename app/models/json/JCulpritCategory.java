package models.json;

import models.db.CulpritCategory;

public class JCulpritCategory extends JModel {

	public String name;

	public JCulpritCategory(CulpritCategory category) {
		super(category);
		this.name = category.name;
	}
}
