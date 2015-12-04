package models.db;

import java.util.Date;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import models.Faulty;
import play.db.jpa.Model;

@Entity
public class Comment extends Model {

	@ManyToOne
	public Complaint complaint;

	@ManyToOne
	public Culprit culprit;

	@ManyToOne
	public ThinkBox thinkBox;

	@Lob
	public String story;

	@Embedded
	public Log log;

	private Comment(Complaint complaint, String story, User user) {
		this.complaint = complaint;
		this.story = story;
		this.log = new Log(user);
	}

	private Comment(Culprit culprit, String story, User user) {
		this.culprit = culprit;
		this.story = story;
		this.log = new Log(user);
	}

	private Comment(ThinkBox thinkBox, String story, User user) {
		this.thinkBox = thinkBox;
		this.story = story;
		this.log = new Log(user);
	}

	public static Comment create(Complaint complaint, String story, User user) {
		Comment comment = new Comment(complaint, story, user).save();
		complaint.commentCount++;
		complaint.save();
		return comment;
	}

	public static Comment create(Culprit culprit, String story, User user)
			throws Faulty {
		Comment comment = new Comment(culprit, story, user).save();
		culprit.commentCount++;
		culprit.save();
		return comment;
	}

	public static Comment create(ThinkBox thinkBox, String story, User user)
			throws Faulty {
		Comment comment = new Comment(thinkBox, story, user).save();
		thinkBox.commentCount++;
		thinkBox.save();
		return comment;
	}

	public static List<Comment> findAll(Complaint complaint, Date date,
			Integer length) {
		if (length == null)
			length = 10;

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c FROM Comment c ");
		sql.append("WHERE c.complaint = :complaint ");

		if (date != null)
			sql.append("AND c.log.createdAt < :date ");
		sql.append("ORDER BY c.log.createdAt DESC");

		JPAQuery query = Comment.find(sql.toString());
		query.bind("complaint", complaint);
		if (date != null)
			query.bind("date", date);
		return query.fetch(length);
	}

	public static List<Comment> findAll(Culprit culprit, Date date,
			Integer length) {
		if (length == null)
			length = 10;

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c FROM Comment c ");
		sql.append("WHERE c.culprit = :culprit ");

		if (date != null)
			sql.append("AND c.log.createdAt < :date ");
		sql.append("ORDER BY c.log.createdAt DESC");

		JPAQuery query = Comment.find(sql.toString());
		query.bind("culprit", culprit);
		if (date != null)
			query.bind("date", date);
		return query.fetch(length);
	}

	public static List<Comment> findAll(ThinkBox thinkBox, Date date,
			Integer length) {
		if (length == null)
			length = 10;

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c FROM Comment c ");
		sql.append("WHERE c.thinkBox = :thinkBox ");

		if (date != null)
			sql.append("AND c.log.createdAt < :date ");
		sql.append("ORDER BY c.log.createdAt DESC");

		JPAQuery query = Comment.find(sql.toString());
		query.bind("thinkBox", thinkBox);
		if (date != null)
			query.bind("date", date);
		return query.fetch(length);
	}

}
