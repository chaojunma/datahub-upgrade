package com.gouuse.datahub.upgrade;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.Order;

/**
 * 升级脚本配置类
 */

// 设置加载顺序
@Order(PriorityOrdered.HIGHEST_PRECEDENCE)
@Configuration
@ConditionalOnProperty(prefix = UpgradeProperties.PREFIX, value = "enabled", matchIfMissing = false)
@EnableConfigurationProperties(UpgradeProperties.class)
public class UpgradeAutoConfiguration implements ApplicationListener<ApplicationEvent>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UpgradeAutoConfiguration.class);

	/**
	 * 是否已更新过
	 */
	private boolean upgraded;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private UpgradeProperties properties;
	
	/**
	 * 容器加载完毕后执行
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		
		// 若已更新, 直接返回
		if(upgraded) {
			return;
		}
		
		// 创建升级脚本
		ScriptUpgrade script = new ScriptUpgrade(properties);
		// 执行脚本
		try {
			script.excute(dataSource);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		// 设置成已更新
		upgraded = true;
	}
	

}
