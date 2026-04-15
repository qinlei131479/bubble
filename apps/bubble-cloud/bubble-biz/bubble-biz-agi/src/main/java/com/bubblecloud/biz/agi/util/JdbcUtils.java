package com.bubblecloud.biz.agi.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.bubblecloud.agi.api.dto.DatasourceTestDTO;
import com.bubblecloud.agi.api.vo.DatasourceTestResultVO;
import com.bubblecloud.agi.api.vo.TableInfoVO;
import com.bubblecloud.agi.api.entity.DatasourceTableField;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JDBC 连接工具类
 *
 * @author BubbleCloud
 * @date 2026-03-02
 */
@Slf4j
public class JdbcUtils {

	/**
	 * 缓存驱动类
	 */
	private static final Map<String, String> DRIVER_MAP = new ConcurrentHashMap<>();

	static {
		DRIVER_MAP.put("mysql", "com.mysql.cj.jdbc.Driver");
		DRIVER_MAP.put("pg", "org.postgresql.Driver");
		DRIVER_MAP.put("oracle", "oracle.jdbc.OracleDriver");
		DRIVER_MAP.put("sqlServer", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		DRIVER_MAP.put("ck", "com.clickhouse.jdbc.ClickHouseDriver");
		DRIVER_MAP.put("dm", "dm.jdbc.driver.DmDriver");
	}

	/**
	 * 获取 JDBC URL
	 */
	public static String buildJdbcUrl(DatasourceTestDTO dto) {
		String dsType = dto.getDsType();
		String host = dto.getHost();
		Integer port = dto.getPort();
		String dbName = dto.getDsName();
		String dbSchema = dto.getDbSchema();
		String extraJdbc = dto.getExtraJdbc();

		if (StrUtil.isBlank(dsType) || StrUtil.isBlank(host) || ObjectUtil.isNull(port)
				|| StrUtil.isBlank(dbName)) {
			return null;
		}

		StringBuilder url = new StringBuilder();

		switch (dsType) {
			case "mysql":
				url.append("jdbc:mysql://").append(host).append(":").append(port)
						.append("/").append(dbName != null ? dbName : "")
						.append("?characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true");
				break;
			case "pg":
				url.append("jdbc:postgresql://").append(host).append(":").append(port)
						.append("/").append(dbName != null ? dbName : "");
				break;
			case "oracle":
				String mode = dto.getMode();
				if ("sid".equals(mode)) {
					url.append("jdbc:oracle:thin:@").append(host).append(":").append(port)
							.append(":").append(dbName != null ? dbName : "ORCL");
				} else {
					url.append("jdbc:oracle:thin:@//").append(host).append(":").append(port)
							.append("/").append(dbSchema != null ? dbSchema : "ORCL");
				}
				break;
			case "sqlServer":
				url.append("jdbc:sqlserver://").append(host).append(":").append(port)
						.append(";database=").append(dbName != null ? dbName : "")
						.append(";characterEncoding=UTF-8");
				break;
			case "ck":
				url.append("jdbc:clickhouse://").append(host).append(":").append(port)
						.append("/").append(dbName != null ? dbName : "default");
				break;
			case "dm":
				url.append("jdbc:dm://").append(host).append(":").append(port)
						.append("/").append(dbName != null ? dbName : "");
				break;
			case "doris":
				url.append("jdbc:mysql://").append(host).append(":").append(port)
						.append("/").append(dbName != null ? dbName : "");
				break;
			case "starrocks":
				url.append("jdbc:mysql://").append(host).append(":").append(port)
						.append("/").append(dbName != null ? dbName : "");
				break;
			default:
				return null;
		}

		if (StrUtil.isNotBlank(extraJdbc)) {
			if (url.toString().contains("?")) {
				url.append("&").append(extraJdbc);
			} else {
				url.append("?").append(extraJdbc);
			}
		}

		return url.toString();
	}

	/**
	 * 测试数据库连接
	 */
	public static DatasourceTestResultVO testConnection(DatasourceTestDTO dto) {
		DatasourceTestResultVO result = new DatasourceTestResultVO();

		String jdbcUrl = buildJdbcUrl(dto);
		if (jdbcUrl == null) {
			result.setConnected(false);
			result.setErrorMessage("不支持的数据源类型: " + dto.getDsType());
			return result;
		}

		String driverClass = DRIVER_MAP.get(dto.getDsType());
		if (driverClass == null) {
			result.setConnected(false);
			result.setErrorMessage("未找到数据源类型对应的驱动: " + dto.getDsType());
			return result;
		}

		Connection connection = null;
		try {
			// 加载驱动
			Class.forName(driverClass);

			// 设置超时
			int timeout = dto.getTimeout() != null ? dto.getTimeout() : 30;

			// 获取连接
			connection = DriverManager.getConnection(
					jdbcUrl,
					dto.getUsername(),
					dto.getPassword()
			);

			// 获取数据库信息
			DatabaseMetaData metaData = connection.getMetaData();
			result.setConnected(true);
			result.setDatabaseName(metaData.getDatabaseProductName());
			result.setDatabaseVersion(metaData.getDatabaseProductVersion());
			result.setErrorMessage(null);

			log.info("数据库连接测试成功: {} - {}", dto.getDsType(), jdbcUrl);

		} catch (ClassNotFoundException e) {
			result.setConnected(false);
			result.setErrorMessage("未找到数据库驱动: " + driverClass);
			log.error("数据库驱动未找到: {}", driverClass, e);
		} catch (SQLException e) {
			result.setConnected(false);
			result.setErrorMessage("数据库连接失败: " + e.getMessage());
			log.error("数据库连接失败: {}", jdbcUrl, e);
		} finally {
			closeQuietly(connection);
		}

		return result;
	}

	/**
	 * 获取数据库表列表
	 */
	public static List<String> getTables(DatasourceTestDTO dto) {
		List<String> tables = new ArrayList<>();

		String jdbcUrl = buildJdbcUrl(dto);
		if (jdbcUrl == null) {
			return tables;
		}

		String driverClass = DRIVER_MAP.get(dto.getDsType());
		if (driverClass == null) {
			return tables;
		}

		Connection connection = null;
		ResultSet rs = null;

		try {
			Class.forName(driverClass);
			connection = DriverManager.getConnection(
					jdbcUrl,
					dto.getUsername(),
					dto.getPassword()
			);

			DatabaseMetaData metaData = connection.getMetaData();
			String schema = dto.getDbSchema();

			// 根据数据库类型处理 schema
			if ("mysql".equals(dto.getDsType()) || "ck".equals(dto.getDsType()) || "doris".equals(dto.getDsType()) || "starrocks".equals(dto.getDsType())) {
				schema = dto.getDsName();
			}

			rs = metaData.getTables(
					schema,
					null,
					"%",
					new String[]{"TABLE", "VIEW"}
			);

			while (rs.next()) {
				tables.add(rs.getString("TABLE_NAME"));
			}

			log.info("获取数据库表列表成功: {} - {} 张表", jdbcUrl, tables.size());

		} catch (Exception e) {
			log.error("获取数据库表列表失败: {}", jdbcUrl, e);
		} finally {
			closeQuietly(rs);
			closeQuietly(connection);
		}

		return tables;
	}

	/**
	 * 获取数据库表详细信息（包含表注释）
	 */
	public static List<TableInfoVO> getTableInfo(DatasourceTestDTO dto) {
		List<TableInfoVO> tableInfoList = new ArrayList<>();

		String jdbcUrl = buildJdbcUrl(dto);
		if (jdbcUrl == null) {
			return tableInfoList;
		}

		String driverClass = DRIVER_MAP.get(dto.getDsType());
		if (driverClass == null) {
			return tableInfoList;
		}

		Connection connection = null;
		ResultSet rs = null;

		try {
			Class.forName(driverClass);
			connection = DriverManager.getConnection(
					jdbcUrl,
					dto.getUsername(),
					dto.getPassword()
			);

			DatabaseMetaData metaData = connection.getMetaData();
			String schema = dto.getDbSchema();

			// 根据数据库类型处理 schema
			if ("mysql".equals(dto.getDsType()) || "ck".equals(dto.getDsType()) || "doris".equals(dto.getDsType()) || "starrocks".equals(dto.getDsType())) {
				schema = dto.getDsName();
			}

			rs = metaData.getTables(
					schema,
					null,
					"%",
					new String[]{"TABLE", "VIEW"}
			);

			while (rs.next()) {
				TableInfoVO tableInfo = new TableInfoVO();
				tableInfo.setTableName(rs.getString("TABLE_NAME"));
				tableInfo.setTableComment(rs.getString("REMARKS") != null ? rs.getString("REMARKS") : "");
				tableInfo.setTableType(rs.getString("TABLE_TYPE"));
				tableInfoList.add(tableInfo);
			}

			log.info("获取数据库表详细信息成功: {} - {} 张表", jdbcUrl, tableInfoList.size());

		} catch (Exception e) {
			log.error("获取数据库表详细信息失败: {}", jdbcUrl, e);
		} finally {
			closeQuietly(rs);
			closeQuietly(connection);
		}

		return tableInfoList;
	}

	/**
	 * 获取表字段信息
	 */
	public static List<DatasourceTableField> getTableFields(DatasourceTestDTO dto, String tableName) {
		List<DatasourceTableField> fields = new ArrayList<>();

		String jdbcUrl = buildJdbcUrl(dto);
		if (jdbcUrl == null) {
			return fields;
		}

		String driverClass = DRIVER_MAP.get(dto.getDsType());
		if (driverClass == null) {
			return fields;
		}

		Connection connection = null;
		ResultSet rs = null;

		try {
			Class.forName(driverClass);
			connection = DriverManager.getConnection(
					jdbcUrl,
					dto.getUsername(),
					dto.getPassword()
			);

			DatabaseMetaData metaData = connection.getMetaData();
			String schema = dto.getDbSchema();

			// 根据数据库类型处理 schema
			if ("mysql".equals(dto.getDsType()) || "ck".equals(dto.getDsType()) || "doris".equals(dto.getDsType()) || "starrocks".equals(dto.getDsType())) {
				schema = dto.getDsName();
			}

			rs = metaData.getColumns(schema, null, tableName, "%");

			int order = 0;
			while (rs.next()) {
				DatasourceTableField field = new DatasourceTableField();
				field.setFieldName(rs.getString("COLUMN_NAME"));
				field.setFieldType(rs.getString("TYPE_NAME"));
				String remark = rs.getString("REMARKS") != null ? rs.getString("REMARKS") : "";
				field.setFieldComment(remark);
				// 自定义注释默认等于字段注释
				field.setCustomComment(remark);
				field.setWeight(order++);
				fields.add(field);
			}

			log.info("获取表字段信息成功: {}.{} - {} 个字段", schema, tableName, fields.size());

		} catch (Exception e) {
			log.error("获取表字段信息失败: {}.{}", dto.getDsName(), tableName, e);
		} finally {
			closeQuietly(rs);
			closeQuietly(connection);
		}

		return fields;
	}

	/**
	 * 关闭连接
	 */
	private static void closeQuietly(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				log.warn("关闭数据库连接失败", e);
			}
		}
	}

	/**
	 * 关闭结果集
	 */
	private static void closeQuietly(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.warn("关闭结果集失败", e);
			}
		}
	}
}
