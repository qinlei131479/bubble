package com.bubblecloud.common.mybatis.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL pgvector(Type: vector) 与 Java float[] 的 MyBatis 类型处理器。
 * <p>
 * pgvector 文本表示通常为: [0.1,0.2,0.3]
 *
 * @author qinlei
 */
@MappedTypes({float[].class})
@MappedJdbcTypes(JdbcType.OTHER)
public class PgVectorTypeHandler extends BaseTypeHandler<float[]> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, float[] parameter, JdbcType jdbcType) throws SQLException {
		PGobject pg = new PGobject();
		pg.setType("vector");
		pg.setValue(toVectorLiteral(parameter));
		ps.setObject(i, pg);
	}

	@Override
	public float[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return parseVector(rs.getString(columnName));
	}

	@Override
	public float[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return parseVector(rs.getString(columnIndex));
	}

	@Override
	public float[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return parseVector(cs.getString(columnIndex));
	}

	private static String toVectorLiteral(float[] vector) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int idx = 0; idx < vector.length; idx++) {
			if (idx > 0) {
				sb.append(',');
			}
			// 使用 Float.toString 避免科学计数法的奇怪格式
			sb.append(Float.toString(vector[idx]));
		}
		sb.append(']');
		return sb.toString();
	}

	private static float[] parseVector(String raw) {
		if (raw == null) {
			return null;
		}
		String s = raw.trim();
		if (s.isEmpty()) {
			return null;
		}
		// 常见输出: [1,2,3]，少数场景可能为 (1,2,3) 或带空格
		if ((s.startsWith("[") && s.endsWith("]")) || (s.startsWith("(") && s.endsWith(")"))) {
			s = s.substring(1, s.length() - 1).trim();
		}
		if (s.isEmpty()) {
			return new float[0];
		}
		String[] parts = s.split(",");
		List<Float> values = new ArrayList<>(parts.length);
		for (String part : parts) {
			String p = part.trim();
			if (p.isEmpty()) {
				continue;
			}
			values.add(Float.parseFloat(p));
		}
		float[] out = new float[values.size()];
		for (int i = 0; i < values.size(); i++) {
			out[i] = values.get(i);
		}
		return out;
	}
}

