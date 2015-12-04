package controllers.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Constant;
import models.db.Complaint;
import models.db.Culprit;
import models.db.ThinkBox;
import models.db.User;
import play.Logger;
import controllers.BaseWeb;

public class CouncillorWeb extends BaseWeb {

	public static void index() {
		List<User> users = User.search(false, null, 1, 999999, User.COUNCILLOR);
		render(users);
	}

	public static void get(Long id, int count) {
		User councillor = User.findById(id);
		Map<String, Object> o = new HashMap<String, Object>();
		if (councillor.roles.contains(User.COUNCILLOR)
				&& councillor.zone != null) {
			List<Complaint> complaints = Complaint.search(councillor.zone,
					null, null, null, true, 1, 10);
			List<ThinkBox> thinkBoxes = ThinkBox.search(null, councillor.zone,
					null, null, 1, 10);
			List<Culprit> culprits = Culprit.search(councillor.zone, null,
					null, null, 1, 10);
			if (count <= 0)
				count = 9999999;
			List<Complaint> _maps = Complaint.search(councillor.zone, null,
					null, null, true, 1, count);
			StringBuffer maps = new StringBuffer();
			for (Complaint complaint : _maps) {
				int status = complaint.status;
				String color = "blue";
				if (status == Constant.SLA_IN_PROGRESS
						&& complaint.getSlaProceedLeftoverDays() < 0) {
					status = Constant.SLA_DELAYED;
					color = "red";
				} else if (status == Constant.SLA_RESOLVED)
					color = "green";
				maps.append("&markers=color:" + color + "%7Clabel:S%7C"
						+ complaint.location.getX() + "+"
						+ complaint.location.getY());
			}
			if (maps.length() == 0)
				maps.append("&markers=color:blue%7Clabel:S%7C3.097360 + 101.638296");

			List<User> councillors = User.search(false, null, 1, 999999,
					User.COUNCILLOR);

			o.put("maps", maps.toString());
			o.put("councillor", councillor);
			o.put("councillors", councillors);
			o.put("complaints", complaints);
			o.put("thinkBoxes", thinkBoxes);
			o.put("culprits", culprits);
		}
		render(o);
	}
}
