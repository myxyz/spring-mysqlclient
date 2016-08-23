package com.yy.risedev.mysql;

import java.sql.SQLException;
import java.sql.Savepoint;

public interface Transaction extends MysqlClient {

	/**
	 * 事务提交后自动释放Connection
	 * 
	 * @throws SQLException
	 */
	void commit() throws SQLException;

	/**
	 * 事务回滚后自动释放Connection
	 * 
	 * @throws SQLException
	 */
	void rollback() throws SQLException;

	void rollback(Savepoint savepoint) throws SQLException;

	Savepoint setSavepoint() throws SQLException;

	Savepoint setSavepoint(String name) throws SQLException;

	void releaseSavepoint(Savepoint savepoint) throws SQLException;

}
