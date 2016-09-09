package com.github.risedragon.mysql;

import java.sql.SQLException;

/**
 * MysqlClient extend interface to support connection transaction.
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public interface MysqlClientExt extends MysqlClient {

	/**
	 * Begin a new transaction
	 * 
	 * @return the transaction object
	 * @throws SQLException
	 *             if there is database access error
	 */
	Transaction beginTransaction() throws SQLException;
}
