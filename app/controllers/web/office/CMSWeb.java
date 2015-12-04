package controllers.web.office;

import models.db.CMS;
import models.db.Media;
import models.db.User;
import play.data.Upload;
import controllers.BaseOffice;

/**
 * Created by fliptoo on 4/30/15.
 */
public class CMSWeb extends BaseOffice {

	public static void index() {
		CMS cms = CMS.findOnlyOne();
		render(cms);
	}

	public static void updateAboutUsPhoto(Upload photo) {
		if (photo != null) {
			User me = connected();
			CMS cms = CMS.findOnlyOne();
			cms.aboutUsPhoto = Media.create(photo, me);
			cms.save();
		}
		index();
	}

	public static void updateTerm(String term) {
		CMS cms = CMS.findOnlyOne();
		cms.term = term;
		cms.save();
		index();
	}

	public static void updatePrivacy(String privacy) {
		CMS cms = CMS.findOnlyOne();
		cms.privacy = privacy;
		cms.save();
		index();
	}

	public static void updateThinkBoxKickOffCount(int count) {
		CMS cms = CMS.findOnlyOne();
		cms.thinkboxKickOffCount = count;
		cms.save();
		index();
	}

}
