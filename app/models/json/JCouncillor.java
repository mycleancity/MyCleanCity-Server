package models.json;

import models.db.User;

public class JCouncillor extends JModel {

	public JZone zone;
	public String name;
	public String email;
	public String mobile;
	public Long photo;

	public JCouncillor(User user) {
		super(user);
		this.zone = JModel.from(user.zone, JZone.class);
		this.name = user.name;
		this.email = user.email;
		this.mobile = user.mobile;
		if (user.avatar != null)
			this.photo = user.avatar.id;
	}
}
