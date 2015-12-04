package models.job;

import models.db.Complaint;
import models.db.Culprit;
import models.db.Zone;
import play.jobs.Job;

public class ZoneFinderJob extends Job {

	public Long complaintID;

	public Long culpritID;

	public ZoneFinderJob(Long complaintID, Long culpritID) {
		this.complaintID = complaintID;
		this.culpritID = culpritID;
	}

	public void doJob() {
		if (complaintID != null) {
			Complaint complaint = Complaint.findById(complaintID);
			if (complaint != null) {
				Zone zone = Zone.findByTags(complaint.address);
				if (zone != null) {
					complaint.zone = zone;
					complaint.save();
				}
			}
		} else if (culpritID != null) {
			Culprit culprit = Culprit.findById(culpritID);
			if (culprit != null) {
				Zone zone = Zone.findByTags(culprit.address);
				if (zone != null) {
					culprit.zone = zone;
					culprit.save();
				}
			}
		}
	}
}
