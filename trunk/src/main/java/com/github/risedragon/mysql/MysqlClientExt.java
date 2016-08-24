package com.github.risedragon.mysql;

import java.sql.SQLException;

public interface MysqlClientExt extends MysqlClient {
	Transaction beginTransaction() throws SQLException;
}
