package models.db;

import static models.Checker.Duplicated;
import static models.Checker.Invalid;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import models.Constant;
import models.Faulty;
import models.Faulty.Code;
import models.Util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.Upload;
import play.data.validation.Validation;
import play.db.jpa.Model;
import play.libs.Crypto;

@Entity
public class User extends Model {

	public final static String ADMIN = "Admin";
	public final static String OFFICER = "Officer";
	public final static String OFFICER_HEAD = "Officer_Head";
	public final static String COUNCILLOR = "Councillor";

	public Long userID;
	public String name;
	public String email;
	public String mobile;
	public String password;
	public Date lastLoginDate;
	public String status = Constant.ACTIVE;
	public String version;
	public String platform;

	@ManyToOne
	public Department department;

	@ManyToOne
	public Media avatar;

	@ElementCollection(fetch = FetchType.EAGER)
	@JoinTable(name = "User_Role", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "role")
	public Set<String> roles = new HashSet<String>();

	@ManyToOne
	public Zone zone;

	@Embedded
	public Log log;

	public User() {
		this.log = new Log();
	}

	private static void validateEmail(String email, boolean checkDuplicate)
			throws Faulty {
		if (StringUtils.isNotEmpty(email)) {
			if (!Validation.email("email", email).ok)
				Invalid(Code.E1007, email);

			if (checkDuplicate
					&& User.find("byEmailAndStatus", email, Constant.ACTIVE)
							.first() != null)
				Duplicated(Code.E1011, email);
		}
	}

	public static User authenticate(String email, String password)
			throws Faulty {
		password = DigestUtils.md5Hex(password);
		return find("byEmailAndPasswordAndStatus", email, password,
				Constant.ACTIVE).first();
	}

	public static User create(String email, String password, String name,
			User createdBy, String... roles) throws Faulty {
		validateEmail(email, true);
		User user = new User();
		user.email = email;
		user.name = name;
		user.encryptPassword(password);
		user.log = new Log(createdBy);
		if (roles != null)
			for (String role : roles)
				user.roles.add(role);
		return user.save();
	}

	public User update(String email, String password, User updatedBy) {
		if (StringUtils.isNotEmpty(email)
				&& !email.equalsIgnoreCase(this.email)) {
			validateEmail(email, true);
			this.email = email;
		}
		if (StringUtils.isNotEmpty(password)) {
			encryptPassword(password);
		}
		log.updateBy(updatedBy);
		return save();
	}

	public static User findByEmail(String email) throws Faulty {
		return find("email", email).first();
	}

	public static List<User> search(boolean departmentFree, String keyword,
			int page, int length, String... roles) {
		length = Util.length(length);
		return query("SELECT u", departmentFree, keyword, roles).fetch(page,
				length);
	}

	public static int count(boolean departmentFree, String keyword,
			String... roles) {
		return Integer.valueOf(query("SELECT count(*)", departmentFree,
				keyword, roles).first().toString());
	}

	private static JPAQuery query(String select, boolean departmentFree,
			String keyword, String... roles) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM User u WHERE u.status = :status ");
		if (roles != null && roles.length > 0) {
			sql.append("AND (");
			for (int i = 0; i < roles.length; i++) {
				if (i == 0)
					sql.append(":role" + i + " IN ELEMENTS(u.roles) ");
				else
					sql.append("OR :role" + i + " IN ELEMENTS(u.roles) ");
			}
			sql.append(") ");
		} else {
			sql.append("AND SIZE(u.roles) = 0 ");
		}
		if (StringUtils.isNotEmpty(keyword))
			sql.append("AND u.email LIKE :email ");
		if (departmentFree)
			sql.append("AND u.department IS NULL ");

		JPAQuery query = User.find(select + sql.toString());
		query.bind("status", Constant.ACTIVE);
		if (roles != null)
			for (int i = 0; i < roles.length; i++) {
				String role = roles[i];
				query.bind("role" + i, role);
			}
		if (StringUtils.isNotEmpty(keyword)) {
			keyword = keyword.toLowerCase() + "%";
			query.bind("email", keyword);
		}
		return query;
	}

	public void encryptPassword(String password) {
		this.password = DigestUtils.md5Hex(password);
	}

	public boolean passwordMatch(String _password) {
		return password.equals(DigestUtils.md5Hex(password));
	}

	public String getRoleDisplay() {
		if (roles.size() > 0) {
			String role = (String) roles.toArray()[0];
			if (role.equalsIgnoreCase(User.ADMIN))
				return "Admin";
			else if (role.equalsIgnoreCase(User.OFFICER))
				return "Department Officer";
			else if (role.equalsIgnoreCase(User.OFFICER_HEAD))
				return "Head of Department";
			else if (role.equalsIgnoreCase(User.COUNCILLOR))
				return "Councillor";
		}
		return "User";
	}

	public static User findByZone(Zone zone) {
		return User.find("byZoneAndStatus", zone, Constant.ACTIVE).first();
	}

	public User attachAvatar(Media upload) {
		if (upload != null) {
			deleteAvatar();
			avatar = upload;
		}
		return save();
	}

	public User attachAvatar(Upload upload) {
		if (upload != null) {
			deleteAvatar();
			avatar = Media.create(upload, this);
		}
		return save();
	}

	private void deleteAvatar() {
		if (avatar != null) {
			Media o = avatar;
			avatar = null;
			save();
			o.delete();
		}
	}

}