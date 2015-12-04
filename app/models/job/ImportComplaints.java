package models.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import models.Constant;
import models.db.Complaint;
import models.db.ComplaintCategory;
import models.db.Log;
import models.db.Media;
import models.db.User;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.jobs.Job;
import play.libs.WS;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ImportComplaints extends Job {

	private static GeometryFactory GF = new GeometryFactory();

	public void doJob() throws Exception {
		Logger.info("----------- Import Complaints Start -----------");
		JsonArray array = WS
				.url("http://mycleancity.my/c4app/public/index.php/getFeedback/1000000/0")
				.get().getJson().getAsJsonArray();
		Logger.info("Count: %s", array.size());
		DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (JsonElement e : array) {
			JsonObject o = e.getAsJsonObject();

			String fullname = o.get("fullname").getAsString();
			String email = o.get("email").getAsString();
			String mobile = o.get("mobile").getAsString();
			String category = o.get("category").getAsString();
			String title = o.get("title").getAsString();
			String description = o.get("description").getAsString();
			String image = o.get("image").getAsString();
			String lon = o.get("longi").getAsString();
			String lat = o.get("lat").getAsString();
			String userID = o.get("user_id").getAsString();
			String address = o.get("address").getAsString();
			String createdAt = o.get("create_date").getAsString();

			if (StringUtils.isNotEmpty(fullname)
					&& StringUtils.isNotEmpty(email)
					&& StringUtils.isNotEmpty(mobile)
					&& StringUtils.isNotEmpty(category)
					&& StringUtils.isNotEmpty(title)
					&& StringUtils.isNotEmpty(description)
					&& StringUtils.isNotEmpty(image)
					&& StringUtils.isNotEmpty(lon)
					&& StringUtils.isNotEmpty(lat)
					&& StringUtils.isNotEmpty(userID)
					&& StringUtils.isNotEmpty(address)
					&& StringUtils.isNotEmpty(createdAt)) {

				User user = User.find("byUserID", Long.parseLong(userID))
						.first();
				ComplaintCategory complaintCategory = ComplaintCategory.find(
						"byName", category).first();
				if (user != null && complaintCategory != null) {
					Complaint complaint = new Complaint();
					complaint.contactName = fullname;
					complaint.contactEmail = email;
					complaint.contactNo = mobile;
					complaint.caption = title;
					complaint.story = description;
					complaint.address = address;
					complaint.location = GF.createPoint(new Coordinate(Double
							.parseDouble(lat), Double.parseDouble(lon)));
					complaint.log = new Log(user, DF.parse(createdAt));
					complaint.category = complaintCategory;
					if (complaint.category != null)
						complaint.department = complaint.category.department;
					Media photo = Media.create(
							WS.url("http://mycleancity.my/img/upload/" + image)
									.get().getStream(), userID + ".jpeg",
							"image/jpeg", user);
					complaint.photo = photo;
					complaint.status = Constant.SLA_PENDING_MODERATE;
					complaint.save();
				}
			}
		}
		Logger.info("----------- Import Complaints End -----------");
	}
}
