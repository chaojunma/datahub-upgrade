package com.gouuse.datahub.upgrade;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import lombok.Data;

/**
 *	升级脚本配置属性
 */
@Data
@ConfigurationProperties(prefix = UpgradeProperties.PREFIX)
public class UpgradeProperties {
	
	/**
	 * 配置前缀
	 */
	public static final String PREFIX = "upgrade";
	
	/**
	 * 是否开启升级(默认false关闭)
	 */
	private boolean enabled = false;
	
	/**
	 * 版本号
	 */
	private String version;
	
	/**
	 * 应用名称
	 */
	private String appName = "APP-NAME";
	
	/**
	 * 升级脚本
	 */
	private Resource upgradeScript;

}
