package models.json;

import models.db.User;

public class JUser extends JModel {

	public String name;
	public String email;

	public JUser(User user) {
		super(user);
		this.name = user.name;
		this.email = user.email;
	}

}
