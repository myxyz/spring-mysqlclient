package com.github.risedragon.mysql.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQL转换类统一接口
 */
public interface ActionMeta {

	<T> void set(PreparedStatement pstmt, int[] pos, T value) throws SQLException;

	<T> T get(ResultSet rs, Integer pos, Class<T> type) throws SQLException;

}
