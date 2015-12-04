package controllers.web.office;

import java.util.Date;
import java.util.List;

import models.Constant;
import models.ListPaginator;
import models.Util;
import models.db.Area;
import models.db.CMS;
import models.db.Complaint;
import models.db.ComplaintCategory;
import models.db.Road;
import models.db.SOAP;
import models.db.User;
import models.db.Zone;
import models.job.MPPSRekodAduan;
import controllers.BaseOffice;

public class ComplaintWeb extends BaseOffice {

	public static void index(String keyword, Integer status, Long category,
			Long zone, int page, int length) {
		length = Util.length(length);
		ComplaintCategory _category = null;
		if (category != null)
			_category = ComplaintCategory.findById(category);
		Zone _zone = null;
		if (zone != null)
			_zone = Zone.findById(zone);

		ListPaginator<Complaint> complaints = new ListPaginator(
				Complaint.search(_zone, _category, status, keyword, false,
						page, length), Complaint.count(_zone, _category,
						status, keyword, false));
		complaints.setPageSize(length);
		List<ComplaintCategory> categories = ComplaintCategory.search(null,
				null, 1, 99999);
		ComplaintCategory allCategory = new ComplaintCategory();
		allCategory.id = null;
		allCategory.name = "All";
		categories.add(0, allCategory);

		List<Zone> zones = Zone.search(null, 1, 999999);
		Zone allZone = new Zone();
		allZone.id = null;
		allZone.name = "All";
		zones.add(0, allZone);
		render(complaints, status, category, categories, keyword, zone, zones);
	}

	public static void get(Long id) {
		Complaint complaint = Complaint.findById(id);
		render(complaint);
	}

	public static void pick(Long id) {
		Complaint complaint = Complaint.findById(id);
		CMS cms = CMS.findOnlyOne();
		cms.pickedComplaint = complaint;
		cms.save();
		get(id);
	}

	public static void approve(Long id, Long zone, Long area, Long road) {
		Complaint complaint = Complaint.findById(id);
		if (isGET()) {
			List<Zone> zones = Zone.search(null, 1, 999999);
			zones.add(0, new Zone(null));

			List<Area> areas = Area.search(null, 1, 999999);
			areas.add(0, new Area(null));

			List<Road> roads = Road.search(null, 1, 999999);
			roads.add(0, new Road(null));

			if (complaint.zone == null)
				complaint.zone = Zone.findByTags(complaint.address);

			render(complaint, zones, areas, roads);
		}
		complaint.approve(connected(), (Zone) Zone.findById(zone), null, null);
		new MPPSRekodAduan(complaint.id).in(5);
		get(id);
	}

	public static void reapprove(Long id, Long zone, Long area, Long road) {
		User me = connected();
		Complaint complaint = Complaint.findById(id);
		complaint.status = Constant.SLA_APPROVED;
		complaint.slaAcceptDays = Constant.DAY_SLA_DEPARTMENT_ACCEPT;
		complaint.slaAcceptDate = new Date();
		complaint.log.updateBy(me);
		complaint.mppjCounter = 0;
		complaint.mppjStatus = Constant.MPPJ_BELUM_SAH;
		complaint.mppjValidated = false;
		complaint.mppjFinished = false;
		complaint.save();
		get(id);
	}

	public static void reject(Long id) {
		Complaint complaint = Complaint.findById(id);
		complaint.reject(connected());
		get(id);
	}

	public static void delete(Long id) {
		Complaint complaint = Complaint.findById(id);
		complaint.delete(connected());
		index(null, null, null, null, 1, 10);
	}

	public static void edit(Long id) {
		Complaint complaint = Complaint.findById(id);
		List<ComplaintCategory> categories = ComplaintCategory.search(null,
				null, 1, 99999);
		render(complaint, categories);
	}

	public static void update(Long id, String title, String description,
			Long category, String contactName, String contactEmail,
			String contactNo, String address) {
		User me = connected();
		Complaint complaint = Complaint.findById(id);
		ComplaintCategory _category = complaint.category;
		if (category != null)
			_category = ComplaintCategory.findById(category);
		if (_category == null)
			_category = complaint.category;
		complaint.update(title, description, _category, contactName,
				contactEmail, contactNo, address, me);
		get(id);
	}

	public static void soaps(Long complaintID, String api, int page, int length) {
		length = Util.length(length);
		Complaint complaint = null;
		if (complaintID != null)
			complaint = Complaint.findById(complaintID);
		ListPaginator<SOAP> soaps = new ListPaginator(SOAP.search(complaint,
				api, page, length), SOAP.count(complaint, api));
		soaps.setPageSize(length);
		render(soaps, complaintID, api);
	}

	public static void soap(Long id) {
		SOAP soap = SOAP.findById(id);
		render(soap);
	}

	public static void retry(Long id) {
		new MPPSRekodAduan(id).now();
		index(null, null, null, null, 1, 10);
	}
}
