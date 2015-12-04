package models.db;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

/**
 * Created by fliptoo on 4/30/15.
 */

@Entity
public class CMS extends Model {

	@ManyToOne
	public Media aboutUsPhoto;

	@Lob
	public String term;

	@Lob
	public String privacy;

	@ManyToOne
	public Complaint pickedComplaint;

	@ManyToOne
	public Culprit pickedCulprit;
	
	@ManyToOne
	public ThinkBox pickedThinkBox;

	public int thinkboxKickOffCount = 40;

	public static CMS findOnlyOne() {
		CMS cms = CMS.find("").first();
		if (cms == null) {
			cms = new CMS();
			cms.save();
		}
		return cms;
	}

}
