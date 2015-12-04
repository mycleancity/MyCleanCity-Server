package controllers;

import models.db.Complaint;
import models.db.Department;
import models.db.User;
import play.Play;

public class Mails extends play.mvc.Mailer {

	private final static String from = Play.configuration
			.getProperty("mail.smtp.user");

	public static void forgotPassword(User user, String resetURL) {
		setFrom("MyCleanCity <" + from + ">");
		setSubject("Password reset on MyCleanCity");
		addRecipient(user.email);
		send(user, resetURL);
	}

	public static void approveComplaint(Department department,
			Complaint complaint) {
		setFrom("MyCleanCity <" + from + ">");
		setSubject("Incoming Complaint");
		addRecipient(department.email);
		send(department, complaint);
	}

}
