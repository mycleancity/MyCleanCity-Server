package models.db;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Banner extends Model {

	@ManyToOne
	public Media photo;

	@Embedded
	public Log log;

	public static Banner create(Media photo, User createdBy) {
		Banner banner = new Banner();
		banner.photo = photo;
		return banner.save();
	}

	public Banner update(Media photo, User updatedBy) {
		if (photo != null) {
			if (this.photo != null) {
				Media media = this.photo;
				this.photo = null;
				save();
				media.delete();
			}
			this.photo = photo;
		}
		return save();
	}

	public Banner delete() {
		if (photo != null) {
			Media media = photo;
			photo = null;
			save();
			media.delete();
		}
		return super.delete();
	}
}
