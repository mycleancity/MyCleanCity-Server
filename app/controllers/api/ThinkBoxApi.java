package controllers.api;

import static models.Checker.Required;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Faulty.Code;
import models.Secured;
import models.db.Media;
import models.db.Support;
import models.db.ThinkBox;
import models.db.ThinkBoxCategory;
import models.db.User;
import models.db.Zone;
import models.json.JModel;
import models.json.JSupport;
import models.json.JThinkBox;
import models.json.JThinkBoxCategory;
import play.data.Upload;
import controllers.BaseApi;

@Secured
public class ThinkBoxApi extends BaseApi {

	public static void create(String contactName, String contactEmail,
			String contactNo, String caption, String story,
			Integer feasibility, Upload photo, Long category, Long zone) {

		Required(Code.E1041, contactName);
		Required(Code.E1042, contactEmail);
		Required(Code.E1043, contactNo);
		Required(Code.E1044, caption);
		Required(Code.E1045, story);
		Required(Code.E1046, feasibility);
		Required(Code.E1047, category);
		Required(Code.E1048, zone);

		User me = connected();
		ThinkBox thinkBox = ThinkBox.create(contactName, contactEmail,
				contactNo, caption, story, feasibility,
				(Zone) Zone.findById(zone),
				(ThinkBoxCategory) ThinkBoxCategory.findById(category),
				photo == null ? null : Media.create(photo, me), me);
		JThinkBox jThinkBox = JModel.from(thinkBox, JThinkBox.class);
		jThinkBox.supported = Support.isSupported(thinkBox, me);

		Map<String, Object> o = new HashMap<String, Object>();
		o.put("thinkBox", jThinkBox);
		renderJSON(o);
	}

	public static void search(Long zone, int page, int length) {
		Zone _zone = null;
		if (zone != null)
			_zone = Zone.findById(zone);
		User me = connected();
		List<ThinkBox> thinkBoxes = ThinkBox.search(null, _zone, null, null,
				page, length);
		List<JThinkBox> jThinkBoxes = new ArrayList<JThinkBox>();
		for (ThinkBox thinkBox : thinkBoxes) {
			JThinkBox jThinkBox = JModel.from(thinkBox, JThinkBox.class);
			jThinkBox.supported = Support.isSupported(thinkBox, me);
			jThinkBoxes.add(jThinkBox);
		}
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("thinkBoxes", jThinkBoxes);
		renderJSON(o);
	}

	public static void categories() {
		List<ThinkBoxCategory> categories = ThinkBoxCategory.search(null, 1,
				99999);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("categories",
				JModel.fromList(categories, JThinkBoxCategory.class));
		renderJSON(o);
	}

	public static void support(Long id, String contactName,
			String contactEmail, String contactNo) {

		Required(Code.E1049, contactName);
		Required(Code.E1050, contactEmail);
		Required(Code.E1051, contactNo);

		User me = connected();
		ThinkBox thinkBox = ThinkBox.findById(id);
		Support support = Support.create(contactName, contactEmail, contactNo,
				thinkBox, me);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("support", JModel.from(support, JSupport.class));
		renderJSON(o);
	}

	public static void supports(Long id, int page, int length) {
		ThinkBox thinkBox = ThinkBox.findById(id);
		List<Support> supports = Support.search(thinkBox, page, length);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("supports", JModel.fromList(supports, JSupport.class));
		renderJSON(o);
	}

}
