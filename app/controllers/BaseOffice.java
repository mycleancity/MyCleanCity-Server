package controllers;

import models.Check;
import models.Secured;
import models.db.User;

@Secured
@Check({ User.ADMIN })
public class BaseOffice extends BaseWeb {

}
