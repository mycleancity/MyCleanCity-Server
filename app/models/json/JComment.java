package models.json;

import java.util.Date;

import models.db.Comment;

public class JComment extends JModel {

	public String story;
	public Date date;
	public JUser user;

	public JComment(Comment comment) {
		super(comment);
		this.story = comment.story;
		this.date = comment.log.createdAt;
		this.user = JModel.from(comment.log.createdBy, JUser.class);
	}

}
