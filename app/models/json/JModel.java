package models.json;

import play.db.jpa.Model;

import java.util.ArrayList;
import java.util.List;

public class JModel {

	public Long ID;

	public JModel(Model model) {
		this.ID = model.id;
	}

	public static <J extends JModel, M extends Model> J from(M m, Class<J> c) {
		if (m == null)
			return null;
		J o = null;
		try {
			o = c.getConstructor(m.getClass()).newInstance(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	public static <J extends JModel, M extends Model> List<J> fromList(
			List<M> ms, Class<J> c) {
		List<J> js = new ArrayList<J>();
		for (M m : ms) {
			J j = from(m, c);
			if (j != null)
				js.add(j);
		}
		return js;
	}

}
