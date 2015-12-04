package models;

import java.util.List;

import models.Faulty.Code;
import models.Faulty.Type;

import org.apache.commons.lang.StringUtils;

import play.classloading.enhancers.LocalvariablesNamesEnhancer;
import play.i18n.Messages;

public class Checker {

	private static String findName(Object o) {
		String name = null;
		List<String> names = LocalvariablesNamesEnhancer.LocalVariablesNamesTracer
				.getAllLocalVariableNames(o);
		if (names.size() > 0)
			name = names.get(0);
		return name;
	}

	public static void Required(Code code, Object obj) throws Faulty {

		Type type = Type.REQUIRED;
		if ((obj instanceof String && StringUtils
				.isEmpty(((String) obj).trim())) || obj == null)
			throw new Faulty(type, code, Messages.get(type, findName(obj)));
	}

	public static void Minimum(Code code, double value, double min)
			throws Faulty {
		Type type = Type.MIN;
		if (value < min)
			throw new Faulty(type, code, Messages.get(type, findName(value),
					min));
	}

	public static void Maximum(Code code, double value, double max)
			throws Faulty {
		Type type = Type.MAX;
		if (value > max)
			throw new Faulty(type, code, Messages.get(type, findName(value),
					max));
	}

	public static void NotFound(Code code, Object obj) throws Faulty {
		Type type = Type.NOT_FOUND;
		if (obj == null)
			throw new Faulty(type, code, Messages.get(type, findName(obj)));
	}

	public static void Duplicated(Code code, Object value) throws Faulty {
		Type type = Type.DUPLICATED;
		throw new Faulty(type, code, Messages.get(type, findName(value), value));
	}

	public static void Invalid(Code code, Object value) throws Faulty {
		Type type = Type.INVALID;
		throw new Faulty(type, code, Messages.get(type, findName(value), value));
	}

}
