package controllers.web.office;

import java.util.List;

import models.db.Banner;
import models.db.Media;
import models.db.User;
import play.data.Upload;
import controllers.BaseOffice;

public class BannerWeb extends BaseOffice {

	public static void index(Long id) {
		List<Banner> banners = Banner.all().fetch();
		Banner banner = null;
		if (id != null)
			banner = Banner.findById(id);
		render(banners, banner);
	}

	public static void create(Long id, Upload photo) {
		if (isGET())
			render();
		User me = connected();
		if (id != null) {
			Banner banner = Banner.findById(id);
			banner.update(photo == null ? null : Media.create(photo, me), me);
		} else
			Banner.create(Media.create(photo, me), me);
		index(null);
	}

	public static void delete(Long id) {
		Banner banner = Banner.findById(id);
		banner.delete();
		index(null);
	}
}
