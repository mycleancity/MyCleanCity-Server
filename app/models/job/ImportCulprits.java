package models.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import models.db.Culprit;
import models.db.CulpritCategory;
import models.db.Media;
import models.db.User;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.jobs.Job;
import play.libs.WS;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ImportCulprits extends Job {

	public void doJob() throws Exception {
		Logger.info("----------- Import Culprit Start -----------");
		JsonArray array = WS
				.url("http://mycleancity.my/c4app/public/index.php/getCulprit/1000000/0")
				.get().getJson().getAsJsonArray();

		Logger.info("Count: %s", array.size());
		DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (JsonElement e : array) {
			JsonObject o = e.getAsJsonObject();
			String fullname = o.get("fullname").getAsString();
			String email = o.get("email").getAsString();
			String mobile = o.get("mobile").getAsString();
			String category = o.get("category").getAsString();
			String description = o.get("description").getAsString();
			String image = o.get("image").getAsString();
			String lon = o.get("longi").getAsString();
			String lat = o.get("lat").getAsString();
			String userID = o.get("user_id").getAsString();
			String address = o.get("address").getAsString();
			String createdAt = o.get("create_date").getAsString();
			String pubName = o.get("pubName").getAsString();
			String repeatOffender = o.get("repeat_offender").getAsString();
			String youtubelink = o.get("youtubelink").getAsString();

			if (StringUtils.isNotEmpty(fullname)
					&& StringUtils.isNotEmpty(email)
					&& StringUtils.isNotEmpty(mobile)
					&& StringUtils.isNotEmpty(category)
					&& StringUtils.isNotEmpty(description)
					&& StringUtils.isNotEmpty(image)
					&& StringUtils.isNotEmpty(lon)
					&& StringUtils.isNotEmpty(lat)
					&& StringUtils.isNotEmpty(userID)
					&& StringUtils.isNotEmpty(address)
					&& StringUtils.isNotEmpty(createdAt)) {

				User user = User.find("byUserID", Long.parseLong(userID))
						.first();
				CulpritCategory culpritCategory = CulpritCategory.find(
						"byName", category).first();
				if (user != null && culpritCategory != null) {
					Media photo = Media.create(
							WS.url("http://mycleancity.my/img/upload/" + image)
									.get().getStream(), userID + ".jpeg",
							"image/jpeg", user);
					Culprit culprit = new Culprit(fullname, email, mobile,
							description, address, photo, culpritCategory,
							Double.parseDouble(lat), Double.parseDouble(lon),
							youtubelink, pubName.equalsIgnoreCase("0"),
							repeatOffender.equalsIgnoreCase("0"), user).save();
					culprit.log.createdAt = DF.parse(createdAt);
					culprit.save();
				}
			}
		}
		Logger.info("----------- Import Culprit End -----------");
	}
}
