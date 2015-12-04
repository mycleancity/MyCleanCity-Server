package controllers.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Constant;
import models.db.Complaint;
import models.db.Culprit;
import models.db.ThinkBox;
import models.db.User;
import models.json.JComplaint;
import models.json.JCouncillor;
import models.json.JCulprit;
import models.json.JMap;
import models.json.JModel;
import models.json.JThinkBox;
import controllers.BaseApi;

public class CouncillorApi extends BaseApi {

	public static void all() {

		List<User> users = User.search(false, null, 1, 999999, User.COUNCILLOR);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("results", JModel.fromList(users, JCouncillor.class));
		renderJSON(o);
	}

	public static void find(Long id, int count) {
		User user = User.findById(id);
		Map<String, Object> o = new HashMap<String, Object>();
		if (user.roles.contains(User.COUNCILLOR) && user.zone != null) {
			List<Complaint> complaints = Complaint.search(user.zone, null,
					null, null, true, 1, 10);
			List<ThinkBox> thinkBoxes = ThinkBox.search(null, user.zone, null,
					null, 1, 10);
			List<Culprit> culprits = Culprit.search(user.zone, null, null,
					null, 1, 10);
			if (count <= 0)
				count = 9999999;
			List<Complaint> _maps = Complaint.search(user.zone, null, null,
					null, true, 1, count);
			List<JMap> maps = new ArrayList<JMap>();
			for (Complaint complaint : _maps) {
				int status = complaint.status;
				if (status == Constant.SLA_IN_PROGRESS
						&& complaint.getSlaProceedLeftoverDays() < 0)
					status = Constant.SLA_DELAYED;
				maps.add(new JMap(complaint.location.getX(), complaint.location
						.getY(), status));
			}

			o.put("maps", maps);
			o.put("councillor", JModel.from(user, JCouncillor.class));
			o.put("complaints", JModel.fromList(complaints, JComplaint.class));
			o.put("thinkBoxes", JModel.fromList(thinkBoxes, JThinkBox.class));
			o.put("culprits", JModel.fromList(culprits, JCulprit.class));
		}
		renderJSON(o);
	}
}
