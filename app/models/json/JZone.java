package models.json;

import models.db.Zone;

import org.apache.commons.lang.StringUtils;

public class JZone extends JModel {

	public String zid;
	public String name;
	public String story;

	public JZone(Zone zone) {
		super(zone);
		this.zid = zone.zid;
		this.name = zone.name;
		if (StringUtils.isNotEmpty(zone.story))
			this.name = zone.name + ": " + zone.story;
		this.story = zone.story;
	}
}
