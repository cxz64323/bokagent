package com.bokagent.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreSQL JSONB类型处理器
 */
public class JsonbTypeHandler<T> extends BaseTypeHandler<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> type;

    public JsonbTypeHandler(Class<T> type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 使用通用JSON字符串方式，兼容PostgreSQL和MySQL
            String jsonStr = objectMapper.writeValueAsString(parameter);
            ps.setString(i, jsonStr);
        } catch (Exception e) {
            throw new SQLException("Error converting to JSON", e);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }

    @SuppressWarnings("unchecked")
    private T parseJson(String json) throws SQLException {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return (T) objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new SQLException("Error parsing JSON", e);
        }
    }
}
