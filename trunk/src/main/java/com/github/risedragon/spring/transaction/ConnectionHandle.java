package com.github.risedragon.spring.transaction;

import java.sql.Connection;

public interface ConnectionHandle {

	Connection getConnection();

	void releaseConnection(Connection con);

}
