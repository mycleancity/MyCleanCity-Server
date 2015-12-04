package controllers.web.office;

import models.Check;
import models.Secured;
import models.db.User;
import controllers.BaseOffice;

@Secured
@Check({ User.COUNCILLOR })
public class CouncillorDashboardWeb extends BaseOffice {

	public static void index() {
		render();
	}
}
