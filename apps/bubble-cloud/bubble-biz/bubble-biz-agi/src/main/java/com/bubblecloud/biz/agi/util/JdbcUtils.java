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

/**
 * JDBC 连接工具类
 *
 * @author BubbleCloud
 * @date 2026-03-02
 */
@Slf4j
public class JdbcUtils {

	/**
	 * 数据源类型（code、驱动、URL 构建规则）
	 */
	private enum DsType {
		MYSQL("mysql", "com.mysql.cj.jdbc.Driver", true) {
			@Override
			String buildJdbcUrl(DatasourceTestDTO dto) {
				return StrUtil.format(
						"jdbc:mysql://{}:{}/{}?characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true",
						dto.getHost(),
						dto.getPort(),
						StrUtil.blankToDefault(dto.getDsName(), "")
				);
			}
		},
		PG("pg", "org.postgresql.Driver", false) {
			@Override
			String buildJdbcUrl(DatasourceTestDTO dto) {
				return StrUtil.format(
						"jdbc:postgresql://{}:{}/{}",
						dto.getHost(),
						dto.getPort(),
						StrUtil.blankToDefault(dto.getDsName(), "")
				);
			}
		},
		ORACLE("oracle", "oracle.jdbc.OracleDriver", false) {
			@Override
			String buildJdbcUrl(DatasourceTestDTO dto) {
				String mode = dto.getMode();
				if (StrUtil.equals("sid", mode)) {
					return StrUtil.format(
							"jdbc:oracle:thin:@{}:{}:{}",
							dto.getHost(),
							dto.getPort(),
							StrUtil.blankToDefault(dto.getDsName(), "ORCL")
					);
				}
				return StrUtil.format(
						"jdbc:oracle:thin:@//{}:{}/{}",
						dto.getHost(),
						dto.getPort(),
						StrUtil.blankToDefault(dto.getDbSchema(), "ORCL")
				);
			}
		},
		SQL_SERVER("sqlServer", "com.microsoft.sqlserver.jdbc.SQLServerDriver", false) {
			@Override
			String buildJdbcUrl(DatasourceTestDTO dto) {
				return StrUtil.format(
						"jdbc:sqlserver://{}:{};database={};characterEncoding=UTF-8",
						dto.getHost(),
						dto.getPort(),
						StrUtil.blankToDefault(dto.getDsName(), "")
				);
			}
		},
		CK("ck", "com.clickhouse.jdbc.ClickHouseDriver", true) {
			@Override
			String buildJdbcUrl(DatasourceTestDTO dto) {
				return StrUtil.format(
						"jdbc:clickhouse://{}:{}/{}",
						dto.getHost(),
						dto.getPort(),
						StrUtil.blankToDefault(dto.getDsName(), "default")
				);
			}
		},
		DM("dm", "dm.jdbc.driver.DmDriver", false) {
			@Override
			String buildJdbcUrl(DatasourceTestDTO dto) {
				return StrUtil.format(
						"jdbc:dm://{}:{}/{}",
						dto.getHost(),
						dto.getPort(),
						StrUtil.blankToDefault(dto.getDsName(), "")
				);
			}
		},
		DORIS("doris", "com.mysql.cj.jdbc.Driver", true) {
			@Override
			String buildJdbcUrl(DatasourceTestDTO dto) {
				return StrUtil.format(
						"jdbc:mysql://{}:{}/{}",
						dto.getHost(),
						dto.getPort(),
						StrUtil.blankToDefault(dto.getDsName(), "")
				);
			}
		},
		STARROCKS("starrocks", "com.mysql.cj.jdbc.Driver", true) {
			@Override
			String buildJdbcUrl(DatasourceTestDTO dto) {
				return StrUtil.format(
						"jdbc:mysql://{}:{}/{}",
						dto.getHost(),
						dto.getPort(),
						StrUtil.blankToDefault(dto.getDsName(), "")
				);
			}
		};

		private final String code;
		private final String driverClass;
		private final boolean schemaFromDbName;

		DsType(String code, String driverClass, boolean schemaFromDbName) {
			this.code = code;
			this.driverClass = driverClass;
			this.schemaFromDbName = schemaFromDbName;
		}

		abstract String buildJdbcUrl(DatasourceTestDTO dto);

		String getCode() {
			return code;
		}

		String getDriverClass() {
			return driverClass;
		}

		boolean isSchemaFromDbName() {
			return schemaFromDbName;
		}

		static DsType ofCode(String code) {
			if (StrUtil.isBlank(code)) {
				return null;
			}
			for (DsType type : DsType.values()) {
				if (StrUtil.equals(type.code, code)) {
					return type;
				}
			}
			return null;
		}
	}

	private static DsType resolveDsType(DatasourceTestDTO dto) {
		return ObjectUtil.isNull(dto) ? null : DsType.ofCode(dto.getDsType());
	}

	/**
	 * 获取 JDBC URL
	 */
	public static String buildJdbcUrl(DatasourceTestDTO dto) {
		String extraJdbc = dto.getExtraJdbc();
		if (ObjectUtil.isNull(dto) || StrUtil.isBlank(dto.getDsType()) || StrUtil.isBlank(dto.getHost())
				|| ObjectUtil.isNull(dto.getPort()) || StrUtil.isBlank(dto.getDsName())) {
			return null;
		}
		DsType dsType = resolveDsType(dto);
		if (ObjectUtil.isNull(dsType)) {
			return null;
		}
		StringBuilder url = new StringBuilder(dsType.buildJdbcUrl(dto));
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
		if (ObjectUtil.isNull(jdbcUrl)) {
			result.setConnected(false);
			result.setErrorMessage("不支持的数据源类型: " + dto.getDsType());
			return result;
		}
		DsType dsType = resolveDsType(dto);
		if (ObjectUtil.isNull(dsType) || StrUtil.isBlank(dsType.getDriverClass())) {
			result.setConnected(false);
			result.setErrorMessage("未找到数据源类型对应的驱动: " + dto.getDsType());
			return result;
		}

		String driverClass = dsType.getDriverClass();
		Connection connection = null;
		try {
			// 加载驱动
			Class.forName(driverClass);
			connection = DriverManager.getConnection(jdbcUrl, dto.getUsername(), dto.getPassword());
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
		if (ObjectUtil.isNull(jdbcUrl)) {
			return tables;
		}

		DsType dsType = resolveDsType(dto);
		if (ObjectUtil.isNull(dsType) || StrUtil.isBlank(dsType.getDriverClass())) {
			return tables;
		}

		String driverClass = dsType.getDriverClass();
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
			String schema = dsType.isSchemaFromDbName() ? dto.getDsName() : dto.getDbSchema();

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
		if (ObjectUtil.isNull(jdbcUrl)) {
			return tableInfoList;
		}

		DsType dsType = resolveDsType(dto);
		if (ObjectUtil.isNull(dsType) || StrUtil.isBlank(dsType.getDriverClass())) {
			return tableInfoList;
		}

		String driverClass = dsType.getDriverClass();
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
			String schema = dsType.isSchemaFromDbName() ? dto.getDsName() : dto.getDbSchema();

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
		if (ObjectUtil.isNull(jdbcUrl)) {
			return fields;
		}

		DsType dsType = resolveDsType(dto);
		if (ObjectUtil.isNull(dsType) || StrUtil.isBlank(dsType.getDriverClass())) {
			return fields;
		}

		String driverClass = dsType.getDriverClass();
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
			String schema = dsType.isSchemaFromDbName() ? dto.getDsName() : dto.getDbSchema();

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
