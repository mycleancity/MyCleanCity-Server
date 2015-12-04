package controllers.web.office;

import java.util.List;

import models.Check;
import models.ListPaginator;
import models.Secured;
import models.Util;
import models.db.Complaint;
import models.db.ComplaintCategory;
import models.db.User;
import controllers.BaseOffice;

@Secured
@Check({ User.OFFICER, User.OFFICER_HEAD })
public class DepartmentComplaintWeb extends BaseOffice {

	public static void index(String keyword, Integer status, int page,
			int length) {
		User user = connected();
		ListPaginator<Complaint> complaints = null;
		if (user.department != null) {
			if (ComplaintCategory.search(null, user.department, 1, 1).size() > 0) {
				length = Util.length(length);
				complaints = new ListPaginator(Complaint.searchByDepartment(
						user.department, status, keyword, page, length),
						Complaint.countByDepartment(user.department, status,
								keyword));
				complaints.setPageSize(length);
			}
		}
		render(complaints, keyword, status);
	}

	public static void get(Long id) {
		Complaint complaint = Complaint.findById(id);
		render(complaint);
	}

	public static void proceed(Long id, int days) {
		Complaint complaint = Complaint.findById(id);
		if (isGET())
			render(complaint);
		complaint.proceed(days, connected());
		get(id);
	}

	public static void invalid(Long id, String reason) {
		Complaint complaint = Complaint.findById(id);
		if (isGET())
			render(complaint);
		complaint.invalid(reason, connected());
		get(id);
	}

	public static void resolve(Long id) {
		Complaint complaint = Complaint.findById(id);
		complaint.resolve(connected());
		get(id);
	}
}
