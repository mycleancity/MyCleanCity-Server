package models;

import play.data.validation.Validation;
import play.db.jpa.JPA;
import play.exceptions.PlayException;
import play.i18n.Messages;
import play.mvc.Http.Request;

public class Faulty extends PlayException {

	public static enum Type {
		MIN, MAX, NOT_FOUND, DUPLICATED, REQUIRED, INVALID;
	}

	public static enum Code {
		E1001, E1002, E1003, E1004, E1005, E1006, E1007, E1008, E1009, E1010, 
		E1011, E1012, E1013, E1014, E1015, E1016, E1017, E1018, E1019, E1020, 
		E1021, E1022, E1023, E1024, E1025, E1026, E1027, E1028, E1029, E1030,
		E1031, E1032, E1033, E1034, E1035, E1036, E1037, E1038, E1039, E1040,
		E1041, E1042, E1043, E1044, E1045, E1046, E1047, E1048, E1049, E1050,
		E1051, E1052, E1053, E1054, E1055, E1056;
	}

	public final Type type;

	public final Code code;

	public Faulty(Type type, Code code) {
		super(Messages.get(code));
		this.type = type;
		this.code = code;
		JPA.setRollbackOnly();
	}

	public Faulty(Type type, Code code, String message) {
		super(message);
		this.type = type;
		this.code = code;
		JPA.setRollbackOnly();
	}

	public void apply(boolean isAPI) {
		if (isAPI) {
			Request.current().format = "json";
			throw new Error(this);
		} else {
			Validation.clear();
			Validation.addError(null, Messages.get(toString()));
		}
	}

	public String toString() {
		return code.toString();
	}

	private class Error extends play.mvc.results.Error {

		private Faulty faulty;

		public Error(Faulty faulty) {
			super(faulty.getMessage());
			this.faulty = faulty;
		}

		public String toString() {
			return faulty.toString();
		}

	}

	@Override
	public String getErrorTitle() {
		return toString();
	}

	@Override
	public String getErrorDescription() {
		return Messages.get(toString());
	}

}
