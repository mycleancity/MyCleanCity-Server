package models.json;

import models.db.User;
import play.Play;

public class JLogin extends JUser {

	public String apiVersion;
	public String apiMinVersion;

	public JLogin(User user) {
		super(user);
		this.apiVersion = Play.configuration.getProperty("api.version");
		this.apiMinVersion = Play.configuration.getProperty("api.min.version");
	}

}
