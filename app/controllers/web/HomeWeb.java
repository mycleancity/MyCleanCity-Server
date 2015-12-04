package controllers.web;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import models.Faulty;
import models.db.Banner;
import models.db.CMS;
import models.db.Complaint;
import models.db.ComplaintCategory;
import models.db.Culprit;
import models.db.CulpritCategory;
import models.db.ThinkBox;
import models.db.ThinkBoxCategory;
import models.db.User;
import models.job.ImportComplaints;
import models.job.ImportCulprits;
import play.Logger;
import play.Play;
import controllers.BaseWeb;
import controllers.Security;

public class HomeWeb extends BaseWeb {

	public static void updateUser() {
		Logger.info("Import Users");
		BufferedReader br = null;
		String row = "";
		try {
			br = new BufferedReader(new FileReader(
					Play.getFile("/resources/users.csv")));
			int i = 0;
			while ((row = br.readLine()) != null) {
				Logger.info("Line: %s", row);
				if (i++ == 0)
					continue;
				String[] cols = row.split(",");
				Long ID = Long.parseLong(cols[0]);
				String email = cols[1];
				String password = cols[3];
				String name = email.split("@")[0];
				email = email.replaceAll("\"", "");
				password = password.replaceAll("\"", "");
				name = name.replaceAll("\"", "");

				User user = User.find("byUserID", ID).first();
				if (user != null) {
					user.password = password;
					user.save();
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void ready() {
		new ImportComplaints().now();
		new ImportCulprits().now();
		ok();
	}

	public static void index() {

		CMS cms = CMS.findOnlyOne();
		List<Complaint> complaints = Complaint.search(null, null, null, null,
				false, 0, 4);
		Complaint featured = cms.pickedComplaint;
		if (featured == null && complaints.size() > 0)
			featured = complaints.get(complaints.size() - 1);
		List<ComplaintCategory> complaintCategories = ComplaintCategory.search(
				null, null, 0, 10);
		ComplaintCategory featuredComplaintCategory = complaintCategories
				.get(0);

		List<CulpritCategory> culpritCategories = CulpritCategory.search(null,
				0, 10);
		CulpritCategory featuredCulpritCategory = culpritCategories.get(0);

		List<Culprit> culprits = Culprit.search(null, null, null, null, 0, 4);
		Culprit featuredCulprit = cms.pickedCulprit;
		if (featuredCulprit == null && culprits.size() > 0)
			featuredCulprit = culprits.get(culprits.size() - 1);

		List<ThinkBoxCategory> thinkBoxCategories = ThinkBoxCategory.search(
				null, 0, 10);
		ThinkBoxCategory featuredThinkBoxCategory = thinkBoxCategories.get(0);

		List<ThinkBox> thinkBoxes = ThinkBox.search(null, null, null, null, 0,
				4);
		ThinkBox featuredThinkBox = cms.pickedThinkBox;
		if (featuredThinkBox == null && thinkBoxes.size() > 0)
			featuredThinkBox = thinkBoxes.get(thinkBoxes.size() - 1);

		List<Banner> banners = Banner.all().fetch();

		render(featured, featuredCulprit, complaints, culprits,
				complaintCategories, featuredComplaintCategory,
				culpritCategories, featuredCulpritCategory, banners,
				featuredThinkBoxCategory, featuredThinkBox, thinkBoxes,
				thinkBoxCategories);
	}

	public static void about() {
		CMS cms = CMS.findOnlyOne();
		render(cms);
	}

	public static void term() {
		CMS cms = CMS.findOnlyOne();
		render(cms);
	}

	public static void policy() {
		CMS cms = CMS.findOnlyOne();
		render(cms);
	}

	public static void register(String email, String cEmail, String password,
			String cPassword, String name) {

		List<Complaint> complaints = Complaint.search(null, null, null, null,
				false, 0, 4);
		Complaint featured = complaints.get(0);
		List<ComplaintCategory> complaintCategories = ComplaintCategory.search(
				null, null, 0, 10);
		ComplaintCategory featuredComplaintCategory = complaintCategories
				.get(0);

		List<CulpritCategory> culpritCategories = CulpritCategory.search(null,
				0, 10);
		CulpritCategory featuredCulpritCategory = culpritCategories.get(0);

		List<Culprit> culprits = Culprit.search(null, null, null, null, 0, 4);

		Boolean hasError = false;
		if (!email.equalsIgnoreCase(cEmail)) {
			flash.put("rerror", "Confirm email not match.");
			hasError = true;
		}

		if (!password.equalsIgnoreCase(cPassword)) {
			flash.put("rerror", "Confirm password not match.");
			hasError = true;
		}

		if (!hasError) {
			try {
				User me = User.create(email, password, name, null);
				Security.authenticated(true, me, null, null);
				index();
			} catch (Faulty e) {
				flash.put("rerror", "Email '" + email + "' is duplicated");
			} catch (Throwable e) {
				flash.put("rerror", "Please contact your administrator");
			}
		}

		renderTemplate("/web/HomeWeb/index.html", featured, complaints,
				culprits, complaintCategories, featuredComplaintCategory,
				culpritCategories, featuredCulpritCategory, email, cEmail, name);
	}
}
