package controllers.api;

import static models.Checker.Required;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Constant;
import models.Faulty.Code;
import models.Secured;
import models.db.Complaint;
import models.db.ComplaintCategory;
import models.db.Media;
import models.db.User;
import models.db.Zone;
import models.json.JComplaint;
import models.json.JComplaintCategory;
import models.json.JModel;
import play.data.Upload;
import controllers.BaseApi;

@Secured
public class ComplaintApi extends BaseApi {

	public static void create(String contactName, String contactEmail,
			String contactNo, String caption, String story, String address,
			Upload photo, Long category, Double latitude, Double longitude,
			Long zone) {

		Required(Code.E1011, contactName);
		Required(Code.E1012, contactEmail);
		Required(Code.E1013, contactNo);
		Required(Code.E1014, caption);
		Required(Code.E1015, story);
		Required(Code.E1016, address);
		Required(Code.E1017, photo);
		Required(Code.E1018, category);
		Required(Code.E1019, latitude);
		Required(Code.E1020, longitude);

		User me = connected();
		Complaint complaint = Complaint.create(contactName, contactEmail,
				contactNo, caption, story, address, Media.create(photo, me),
				(ComplaintCategory) ComplaintCategory.findById(category),
				latitude, longitude, me);
		if (zone != null) {
			complaint.zone = Zone.findById(zone);
			complaint.save();
		}
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("complaint", JModel.from(complaint, JComplaint.class));
		renderJSON(o);
	}

	public static void search(Long zone, int page, int length) {
		Zone _zone = null;
		if (zone != null)
			_zone = Zone.findById(zone);
		List<Complaint> complaints = Complaint.search(_zone, null, null, null,
				false, page, length);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("complaints", JModel.fromList(complaints, JComplaint.class));
		renderJSON(o);
	}

	public static void categories() {
		List<ComplaintCategory> categories = ComplaintCategory.search(null,
				null, 1, 99999);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("categories",
				JModel.fromList(categories, JComplaintCategory.class));
		renderJSON(o);
	}

	public static void delete(Long id) {
		Complaint complaint = Complaint.findById(id);
		if (complaint.status == Constant.SLA_PENDING_MODERATE) {
			complaint.delete(connected());
		}
		OK();
	}

}
