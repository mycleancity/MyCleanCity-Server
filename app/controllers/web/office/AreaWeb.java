package controllers.web.office;

import models.Constant;
import models.ListPaginator;
import models.Util;
import models.db.Area;
import models.db.User;
import controllers.BaseOffice;

public class AreaWeb extends BaseOffice {

	public static void index(String keyword, int page, int length) {

		length = Util.length(length);
		ListPaginator<Area> areas = new ListPaginator(Area.search(keyword,
				page, length), Area.count(keyword));
		areas.setPageSize(length);
		render(areas, keyword);
	}

	public static void get(Long id) {
		Area area = Area.findById(id);
		render(area);
	}

	public static void create(String zid, String name, String tags) {
		if (isGET()) {
			render();
		}
		User me = connected();
		Area.create(zid, name, tags, me);
		index(null, 1, 10);
	}

	public static void edit(Long id) {
		Area area = Area.findById(id);
		render(area);
	}

	public static void update(Long id, String zid, String name, String tags) {
		User me = connected();
		Area area = Area.findById(id);
		area.zid = zid;
		area.name = name;
		area.tags = tags;
		area.log.updateBy(me);
		area.save();
		get(id);
	}

	public static void delete(Long id) {
		Area area = Area.findById(id);
		area.status = Constant.INACTIVE;
		area.log.updateBy(connected());
		area.save();
		index(null, 1, 10);
	}
}
