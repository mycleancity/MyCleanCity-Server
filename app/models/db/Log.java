package models.db;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.ocpsoft.prettytime.PrettyTime;

import models.Faulty;
import play.libs.Images;

@Embeddable
public class Log {

	@ManyToOne
	public User createdBy;

	@ManyToOne
	public User updatedBy;

	public Date createdAt;

	public Date updatedAt;

	public Log() {
		this(null);
	}

	public Log(User createdBy) {
		this(createdBy, new Date());
	}

	public Log(User createdBy, Date createdAt) {
		if (createdAt == null)
			createdAt = new Date();
		this.createdBy = createdBy;
		this.createdAt = createdAt;
		this.updatedBy = createdBy;
		this.updatedAt = createdAt;
	}

	public void updateBy(User updatedBy) {
		this.updatedBy = updatedBy;
		this.updatedAt = new Date();
	}

	public String getCreatedAtTimeAgo() {
		PrettyTime p = new PrettyTime();
		return p.format(createdAt);
	}

}
