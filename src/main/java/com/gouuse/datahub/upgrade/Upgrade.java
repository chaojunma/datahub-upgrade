package com.gouuse.datahub.upgrade;

import java.io.Serializable;
import lombok.Data;

/**
 *	实体类
 */

@Data
public class Upgrade implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final String TABLE  = "upgrade";
	
	public static final String COLUMN = "app_name, app_version, upgrade_time";
	
	public static final String DDL    = "CREATE TABLE upgrade ( "
			+ "app_name varchar(255) NOT NULL, "
			+ "app_version varchar(255) NOT NULL PRIMARY KEY, "
			+ "upgrade_time varchar(255) NOT NULL)";
	
	private String appName;
	
	private String appVersion;
	
	private String upgradeTime;


}
