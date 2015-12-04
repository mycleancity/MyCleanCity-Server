package models.db;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import models.Util;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.Model;

@Entity
public class SOAP extends Model {

	public final static String REKOD_ADUAN = "REKOD_ADUAN";
	public final static String KESAHIHAN_ADUAN = "KESAHIHAN_ADUAN";
	public final static String MAKLUM_BALAS_ADUAN = "MAKLUM_BALAS_ADUAN";

	@ManyToOne
	public Complaint complaint;

	public String api;

	@Lob
	public String request;

	@Lob
	public String response;

	public Date createdAt;

	public long times;

	public boolean success;

	public static SOAP create(Complaint complaint, String api, String request) {
		SOAP soap = new SOAP();
		soap.complaint = complaint;
		soap.api = api;
		soap.request = request;
		soap.createdAt = new Date();
		soap.success = false;
		return soap.save();
	}

	public SOAP update(boolean success, String response) {
		this.success = success;
		this.response = response;
		this.times = new Date().getTime() - createdAt.getTime();
		return save();
	}

	public static List<SOAP> search(Complaint complaint, String api, int page,
			int length) {
		length = Util.length(length);
		return query("SELECT c", complaint, api).fetch(page, length);
	}

	public static int count(Complaint complaint, String api) {
		return Integer.valueOf(query("SELECT count(*)", complaint, api).first()
				.toString());
	}

	private static JPAQuery query(String select, Complaint complaint, String api) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM SOAP c WHERE 1 = 1 ");
		if (complaint != null)
			sql.append("AND c.complaint = :complaint ");
		if (StringUtils.isNotEmpty(api))
			sql.append("AND c.name = :api ");
		sql.append("ORDER BY c.createdAt DESC ");

		JPAQuery query = Complaint.find(select + sql.toString());
		if (complaint != null)
			query.bind("complaint", complaint);
		if (StringUtils.isNotEmpty(api))
			query.bind("api", api);
		return query;
	}

}
