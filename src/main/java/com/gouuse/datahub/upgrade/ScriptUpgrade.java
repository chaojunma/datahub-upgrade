package com.gouuse.datahub.upgrade;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *	脚本升级类
 */
public class ScriptUpgrade {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScriptUpgrade.class);

	private UpgradeProperties properties;
	
	public ScriptUpgrade(UpgradeProperties properties) {
		this.properties = properties;
	}
	
	/**
	 * 脚本升级执行方法
	 * @throws SQLException 
	 */
	public void excute(DataSource dataSource) throws Exception {
		if(properties == null 
				|| dataSource == null 
				|| StringUtils.isEmpty(properties.getVersion()) 
				|| properties.getUpgradeScript() == null) {
			return;
		}
		
		LOGGER.info("###  upgrade database to version {} begining ... ###", properties.getVersion());
		
		Connection conn = null;
		ResultSet resultSet = null;
		
		try {
			// 获取数据库连接
			conn = dataSource.getConnection();
			// 获取所有列
			resultSet = conn.getMetaData().getColumns(null, null, Upgrade.TABLE, "%");
			// 如表不存在,则创建表
			if(!resultSet.next()) {
				createTable(conn);
			}
			
			//已更新此版本, 则直接返回
			if(existVersion(conn)) {
				return;
			}
			
			// 执行脚本
			executeScript(conn);
			
		}  finally {
			if(resultSet != null) {
				resultSet.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
		
		LOGGER.info("upgrade database to version {} finished",     properties.getVersion());
		
	} 
	
	
	/**
	 * 创建表
	 * @param conn
	 * @throws SQLException 
	 */
	public void createTable(Connection conn) throws SQLException {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			LOGGER.info("### begin create table, the sql is: {} ###", Upgrade.DDL);
			stmt.execute(Upgrade.DDL);
		} finally {
			if(stmt != null) {
				stmt.close();
			}
		}
	}
	
	
	/**
	 * 判断是否已更新此版本
	 * @param conn
	 * @return
	 * @throws Exception 
	 */
	private boolean existVersion(Connection conn) throws SQLException {
		String sql = "select count(1) from %s where app_version = '%s'";
		sql = String.format(sql, Upgrade.TABLE, properties.getVersion());
		
		Statement stmt = null;
		ResultSet resultSet  = null;
		
		try {
			stmt = conn.createStatement();
			resultSet = stmt.executeQuery(sql);
			
			if(!resultSet.next() || resultSet.getInt(1) <= 0) {
				return false;
			}
		} finally {
			if(resultSet != null) {
				resultSet.close();
			}
			if(stmt != null) {
				stmt.close();
			}
		}
		
		return true;
	}
	
	
	/**
	 * 执行脚本
	 * @param conn
	 * @throws SQLException 
	 */
	public void executeScript(Connection conn) throws Exception {
		LOGGER.info("execute upgrade script {}", properties.getUpgradeScript().getFilename());
		String insertVersion = "insert into %s values ('%s', '%s', '%s')";
		insertVersion = String.format(insertVersion, 
				Upgrade.TABLE, 
				properties.getAppName(),
				properties.getVersion(),
				DateFormatUtils.format(System.currentTimeMillis(), 
				"yyyy-MM-dd HH:mm:ss"));
		
		BufferedReader reader = null;
		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			LOGGER.info(insertVersion);
			stmt.addBatch(insertVersion);
			
			if(properties.getUpgradeScript().exists()) {
				InputStreamReader inputStreamReader = new InputStreamReader(properties.getUpgradeScript().getInputStream());
				reader = new BufferedReader(inputStreamReader);
				StringBuilder sql = new StringBuilder();
				while(reader.ready()) {
					
					String line = reader.readLine();
					// 跳过空行及注释
					if(StringUtils.isEmpty(line) || line.startsWith("#") || line.startsWith("--")) {
						continue;
					}
					
					// 拼接SQL
					sql.append(line);
					
					if(line.trim().endsWith(";")) { // SQL结束
						LOGGER.info("### {} ###", sql.toString());
						// 添加批量操作
						stmt.addBatch(sql.toString());
						// 清空SQL
						sql.setLength(0);
					}
				}
			}
			
			// 批量执行SQL
			stmt.executeBatch();
			
		} finally {
			IOUtils.closeQuietly(reader);
			if(stmt != null) {
				stmt.close();
			}
		}

	}
}
