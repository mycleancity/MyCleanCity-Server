package controllers.api;

import static models.Checker.NotFound;
import static models.Checker.Required;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Faulty;
import models.Faulty.Code;
import models.Secured;
import models.db.Comment;
import models.db.Complaint;
import models.db.Culprit;
import models.db.ThinkBox;
import models.json.JComment;
import models.json.JModel;
import controllers.BaseApi;

@Secured
public class CommentApi extends BaseApi {

	public static void createForComplaint(Long id, String story) throws Faulty {
		Required(Code.E1031, id);
		Required(Code.E1032, story);

		Complaint complaint = Complaint.findById(id);
		NotFound(Code.E1033, complaint);

		Comment comment = Comment.create(complaint, story, connected());
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("comment", JModel.from(comment, JComment.class));
		renderJSON(o);
	}

	public static void listForComplaint(Long id, Date date, Integer length)
			throws Faulty {
		Required(Code.E1034, id);
		Complaint complaint = Complaint.findById(id);
		NotFound(Code.E1035, complaint);

		List<Comment> comments = Comment.findAll(complaint, date, length);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("comments", JModel.fromList(comments, JComment.class));
		renderJSON(o);
	}

	public static void createForCulprit(Long id, String story) throws Faulty {
		Required(Code.E1036, id);
		Required(Code.E1037, story);

		Culprit culprit = Culprit.findById(id);
		NotFound(Code.E1038, culprit);

		Comment comment = Comment.create(culprit, story, connected());
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("comment", JModel.from(comment, JComment.class));
		renderJSON(o);
	}

	public static void listForCulprit(Long id, Date date, Integer length)
			throws Faulty {
		Required(Code.E1039, id);
		Culprit culprit = Culprit.findById(id);
		NotFound(Code.E1040, culprit);

		List<Comment> comments = Comment.findAll(culprit, date, length);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("comments", JModel.fromList(comments, JComment.class));
		renderJSON(o);
	}

	public static void createForThinkBox(Long id, String story) throws Faulty {
		Required(Code.E1052, id);
		Required(Code.E1053, story);

		ThinkBox thinkBox = ThinkBox.findById(id);
		NotFound(Code.E1054, thinkBox);

		Comment comment = Comment.create(thinkBox, story, connected());
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("comment", JModel.from(comment, JComment.class));
		renderJSON(o);
	}

	public static void listForThinkBox(Long id, Date date, Integer length)
			throws Faulty {
		Required(Code.E1055, id);
		ThinkBox thinkBox = ThinkBox.findById(id);
		NotFound(Code.E1056, thinkBox);

		List<Comment> comments = Comment.findAll(thinkBox, date, length);
		Map<String, Object> o = new HashMap<String, Object>();
		o.put("comments", JModel.fromList(comments, JComment.class));
		renderJSON(o);
	}
}
