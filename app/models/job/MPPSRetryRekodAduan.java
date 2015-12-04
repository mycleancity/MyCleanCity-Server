package models.job;

import java.util.List;

import models.Constant;
import models.db.Complaint;
import play.db.jpa.GenericModel.JPAQuery;
import play.jobs.Every;
import play.jobs.Job;

@Every("1h")
public class MPPSRetryRekodAduan extends Job {

	public void doJob() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c FROM Complaint c WHERE c.status = :status ");
		sql.append("AND c.complaintID IS NULL ");

		JPAQuery query = Complaint.find(sql.toString());
		query.setParameter("status", Constant.SLA_APPROVED);
		List<Complaint> complaints = query.fetch();
		for (Complaint complaint : complaints) {
			new MPPSRekodAduan(complaint.id).now();
		}
	}
}
