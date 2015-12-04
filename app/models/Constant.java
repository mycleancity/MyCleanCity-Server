package models;

public class Constant {

	public final static String MPPJ_SOAP_URL = "http://eps.mbpj.gov.my/";
	public final static String MPPJ_SOAP_API = "http://aplikasi.mbpj.gov.my/IntegrasiAduan/AduanWebService.asmx";

	public final static String ACTIVE = "ACTIVE";
	public final static String INACTIVE = "INACTIVE";
	public final static String DELETED = "DELETED";

	public final static String MALE = "M";
	public final static String FEMALE = "F";

	public final static String IOS = "IOS";
	public final static String ANDROID = "ANDROID";

	public final static String FACEBOOK = "FACEBOOK";
	public final static String INSTAGRAM = "INSTAGRAM";

	public final static int DAY_SLA_DEPARTMENT_ACCEPT = 7;

	public final static int SLA_DELETED = -1;
	public final static int SLA_PENDING_MODERATE = 0;
	public final static int SLA_APPROVED = 1;
	public final static int SLA_IN_PROGRESS = 2;
	public final static int SLA_INVALID = 3;
	public final static int SLA_RESOLVED = 4;
	public final static int SLA_REJECTED = 5;
	public final static int SLA_DELAYED = 6;

	public final static int CULPRIT_DELETED = -1;
	public final static int CULPRIT_PENDING_MODERATE = 0;
	public final static int CULPRIT_APPROVED = 1;
	public final static int CULPRIT_BLACKLISTED = 2;
	public final static int CULPRIT_SETTLED = 3;

	public final static int THINKBOX_DELETED = -1;
	public final static int THINKBOX_PENDING_MODERATE = 0;
	public final static int THINKBOX_APPROVED = 1;
	public final static int THINKBOX_SELECTED = 2;
	public final static int THINKBOX_PRESENTED = 3;

	public final static String MPPJ_BELUM_SAH = "Belum Disahkan";
	public final static String MPPJ_SAH = "Y";
	public final static String MPPJ_TIDAK_SAH = "N";
	public final static String MPPJ_TIADA_REKOD = "Tiada Rekod";
	public final static String MPPJ_BELUM_TINDAKAN = "Belum Diambil Tindakan";
	public final static String MPPJ_SEDANG_TINDAKAN = "Sedang Diambil Tindakan";
	public final static String MPPJ_SELESAI = "Selesai";
	public final static String MPPJ_BERULANG = "Aduan Berulang";
	public final static String MPPJ_PALSU = "Aduan Palsu";

}
