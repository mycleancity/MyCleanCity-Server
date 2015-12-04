package controllers.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Secured;
import models.db.Zone;
import models.json.JComplaintCategory;
import models.json.JModel;
import models.json.JZone;
import controllers.BaseApi;

@Secured
public class ZoneApi extends BaseApi {

	public static void zones() {
		List<Zone> zones = Zone.search(null, 1, 99999);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("zones", JModel.fromList(zones, JZone.class));
		renderJSON(o);
	}

}
