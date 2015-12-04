package controllers.web.office;

import models.Constant;
import models.ListPaginator;
import models.Util;
import models.db.Road;
import models.db.User;
import controllers.BaseOffice;

public class RoadWeb extends BaseOffice {

	public static void index(String keyword, int page, int length) {

		length = Util.length(length);
		ListPaginator<Road> roads = new ListPaginator(Road.search(keyword,
				page, length), Road.count(keyword));
		roads.setPageSize(length);
		render(roads, keyword);
	}

	public static void get(Long id) {
		Road road = Road.findById(id);
		render(road);
	}

	public static void create(String zid, String name, String tags) {
		if (isGET()) {
			render();
		}
		User me = connected();
		Road.create(zid, name, tags, me);
		index(null, 1, 10);
	}

	public static void edit(Long id) {
		Road road = Road.findById(id);
		render(road);
	}

	public static void update(Long id, String zid, String name, String tags) {
		User me = connected();
		Road road = Road.findById(id);
		road.zid = zid;
		road.name = name;
		road.tags = tags;
		road.log.updateBy(me);
		road.save();
		get(id);
	}

	public static void delete(Long id) {
		Road road = Road.findById(id);
		road.status = Constant.INACTIVE;
		road.log.updateBy(connected());
		road.save();
		index(null, 1, 10);
	}
}
