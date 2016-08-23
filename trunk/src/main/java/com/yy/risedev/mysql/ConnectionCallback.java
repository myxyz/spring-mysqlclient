package com.yy.risedev.mysql;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionCallback<T> {

	T doInConnection(Connection conn) throws SQLException;

}
