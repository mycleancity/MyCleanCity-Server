package controllers.web.office;

import java.util.List;

import models.Constant;
import models.ListPaginator;
import models.Util;
import models.db.User;
import models.db.Zone;
import controllers.BaseOffice;

public class ZoneWeb extends BaseOffice {

	public static void index(String keyword, int page, int length) {

		length = Util.length(length);
		ListPaginator<Zone> zones = new ListPaginator(Zone.search(keyword,
				page, length), Zone.count(keyword));
		zones.setPageSize(length);
		render(zones, keyword);
	}

	public static void get(Long id) {
		Zone zone = Zone.findById(id);
		User councillor = User.findByZone(zone);
		render(zone, councillor);
	}

	public static void create(String zid, String name, String story, String tags) {
		if (isGET()) {
			render();
		}
		User me = connected();
		Zone.create(zid, name, story, tags, me);
		index(null, 1, 10);
	}

	public static void edit(Long id) {
		Zone zone = Zone.findById(id);
		render(zone);
	}

	public static void update(Long id, String zid, String name, String story,
			String tags) {
		User me = connected();
		Zone zone = Zone.findById(id);
		zone.zid = zid;
		zone.name = name;
		zone.story = story;
		zone.tags = tags.toUpperCase();
		zone.log.updateBy(me);
		zone.save();
		get(id);
	}

	public static void delete(Long id) {
		Zone zone = Zone.findById(id);
		zone.status = Constant.INACTIVE;
		zone.log.updateBy(connected());
		zone.save();
		List<User> councillors = User.find("byZoneAndStatus", zone,
				Constant.ACTIVE).fetch();
		for (User councillor : councillors) {
			councillor.zone = null;
			councillor.save();
		}
		index(null, 1, 10);
	}
}
