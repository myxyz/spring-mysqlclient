package com.yy.risedev.mysql;

import java.sql.SQLException;

public interface MysqlClientExt extends MysqlClient {
	Transaction beginTransaction() throws SQLException;
}
