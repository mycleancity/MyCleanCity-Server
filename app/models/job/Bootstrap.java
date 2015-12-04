package models.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import models.db.Banner;
import models.db.ComplaintCategory;
import models.db.CulpritCategory;
import models.db.Department;
import models.db.Log;
import models.db.Media;
import models.db.ThinkBoxCategory;
import models.db.User;
import models.db.Zone;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() throws Exception {

		if (User.count() == 0) {
			User.create("admin@gmail.com", "password", "Admin", null,
					User.ADMIN);

			User.create("head@gmail.com", "password", "Head", null,
					User.OFFICER_HEAD);

			User.create("officer@gmail.com", "password", "Head", null,
					User.OFFICER);

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

					User user = new User();
					user.userID = ID;
					user.email = email;
					user.name = name;
					user.encryptPassword(password);
					user.log = new Log();
					user.save();
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

		User admin = User.findByEmail("admin@gmail.com");
		User head = User.findByEmail("head@gmail.com");
		User officer = User.findByEmail("officer@gmail.com");

		if (Banner.count() == 0) {
			File file = Play.getFile("/resources/banner01.jpg");
			Media photo = Media.create(new FileInputStream(file),
					file.getName(), "image/jpg", admin);
			Banner.create(photo, admin);

			file = Play.getFile("/resources/banner02.jpg");
			photo = Media.create(new FileInputStream(file), file.getName(),
					"image/jpg", admin);
			Banner.create(photo, admin);

			file = Play.getFile("/resources/banner03.jpg");
			photo = Media.create(new FileInputStream(file), file.getName(),
					"image/jpg", admin);
			Banner.create(photo, admin);
		}

		if (Department.count() == 0) {
			Department.create("My Department", "fonghuangyee@gmail.com", head,
					officer, admin);
		}

		Department department = (Department) Department.findAll().get(0);

		if (ComplaintCategory.count() == 0) {
			ComplaintCategory
					.create("Bulk Garbage", "0", department, 14, admin);
			ComplaintCategory.create("Pot Hole", "0", department, 14, admin);
			ComplaintCategory.create("Contractor Garbage", "0", department, 14,
					admin);
			ComplaintCategory.create("Garden Garbage", "0", department, 14,
					admin);
			ComplaintCategory.create("Road Bump", "0", department, 14, admin);
			ComplaintCategory.create("Construction Garbage", "0", department,
					14, admin);
			ComplaintCategory.create("illegal dumping", "0", department, 14,
					admin);
			ComplaintCategory.create("Dirty Road", "0", department, 14, admin);
			ComplaintCategory.create("Sinkhole [Not Obstructing]", "0",
					department, 14, admin);
			ComplaintCategory.create("Sinkhole [Obstructing Others]", "0",
					department, 14, admin);
			ComplaintCategory.create("Fallen Tree [Obstructing Others]", "0",
					department, 14, admin);
			ComplaintCategory.create("Domestic Garbage", "0", department, 14,
					admin);
			ComplaintCategory.create("Fallen Tree [Not Obstructing]", "0",
					department, 14, admin);
		}

		if (CulpritCategory.count() == 0) {
			CulpritCategory.create("Dirty Back Lanes", admin);
			CulpritCategory.create("Illegal Dumping", admin);
			CulpritCategory.create("Loan Shark Advertisement", admin);
		}

		if (Zone.count() == 0) {
			Zone.create("Z01", "Zone01", "Testing ZOne", null, admin);
		}

		if (ThinkBoxCategory.count() == 0) {
			ThinkBoxCategory.create("CategoryA", admin);
		}
	}
}
