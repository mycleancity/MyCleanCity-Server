package controllers.api;

import static models.Checker.Required;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Constant;
import models.Faulty.Code;
import models.Secured;
import models.db.Culprit;
import models.db.CulpritCategory;
import models.db.Media;
import models.db.User;
import models.db.Zone;
import models.job.ZoneFinderJob;
import models.json.JCulprit;
import models.json.JCulpritCategory;
import models.json.JModel;
import play.data.Upload;
import controllers.BaseApi;

@Secured
public class CulpritApi extends BaseApi {

	public static void create(String contactName, String contactEmail,
			String contactNo, String story, String address, Upload photo,
			Long category, Double latitude, Double longitude,
			String youtubeLink, boolean allowPublishName,
			boolean isRepeatOffender, Long zone) {

		Required(Code.E1021, contactName);
		Required(Code.E1022, contactEmail);
		Required(Code.E1023, contactNo);
		Required(Code.E1025, story);
		Required(Code.E1026, address);
		Required(Code.E1027, photo);
		Required(Code.E1028, category);
		Required(Code.E1029, latitude);
		Required(Code.E1030, longitude);

		User me = connected();
		Culprit culprit = new Culprit(contactName, contactEmail, contactNo,
				story, address, Media.create(photo, me),
				(CulpritCategory) CulpritCategory.findById(category), latitude,
				longitude, youtubeLink, allowPublishName, isRepeatOffender, me)
				.save();
		if (zone != null) {
			culprit.zone = Zone.findById(zone);
			culprit.save();
		}
		new ZoneFinderJob(null, culprit.id).in(5);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("culprit", JModel.from(culprit, JCulprit.class));
		renderJSON(o);
	}

	public static void search(Long zone, int page, int length) {
		Zone _zone = null;
		if (zone != null)
			_zone = Zone.findById(zone);
		List<Culprit> complaints = Culprit.search(_zone, null, null, null,
				page, length);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("culprits", JModel.fromList(complaints, JCulprit.class));
		renderJSON(o);
	}

	public static void categories() {
		List<CulpritCategory> categories = CulpritCategory.search(null, 0,
				999999);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("categories", JModel.fromList(categories, JCulpritCategory.class));
		renderJSON(o);
	}

	public static void delete(Long id) {
		Culprit culprit = Culprit.findById(id);
		if (culprit.status == Constant.SLA_PENDING_MODERATE) {
			culprit.delete(connected());
		}
		OK();
	}

}
