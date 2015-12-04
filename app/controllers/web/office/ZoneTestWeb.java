package controllers.web.office;

import java.util.List;

import models.db.Zone;
import controllers.BaseOffice;

public class ZoneTestWeb extends BaseOffice {

	public static void index(String address) {
		Zone zone = Zone.findByTags(address);
		List<Zone> zones = Zone.search(null, 1, 99999);
		render(address, zone, zones);
	}
}
