package com.yy.risedev.mysql;

import java.sql.SQLException;
import java.util.List;

/**
 * 基于mysql的jdbc agent
 */
public interface MysqlClient {

	void init() throws Exception;

	/*********************************************
	 * 通用回调方法
	 *********************************************/
	<T> T callback(ConnectionCallback<T> callback) throws SQLException;

	/*********************************************
	 * 操作@Table的object方法
	 *********************************************/
	/**
	 * 该方法调用注解@Table与@Column自动生成的insert sql. 返回影响的记录数。
	 */
	<T> int insert(T tableObject) throws SQLException;

	/**
	 * 该方法调用注解@Table与@Column自动生成的insert ignore sql. 返回影响的记录数。
	 */
	<T> int insertIgnore(T tableObject) throws SQLException;

	/**
	 * 该方法调用注解@Table与@Column自动生成的insert sql. 返回自增主键的值。
	 */
	<T, R> R insert(T tableObject, Class<R> generatedKeyType) throws SQLException;

	/**
	 * 该方法调用注解@Table与@Column自动生成的insert ignore sql. 返回自增主键的值。
	 */
	<T, R> R insertIgnore(T tableObject, Class<R> generatedKeyType) throws SQLException;

	/**
	 * 该方法调用注解@Table与@Column自动生成的update sql. 返回影响的记录数。
	 */
	<T> int update(T tableObject) throws SQLException;

	/**
	 * 该方法调用注解@Table与@Column自动生成的replace sql. 返回影响的记录数。
	 */
	<T> int replace(T tableObject) throws SQLException;

	/**
	 * 该方法调用注解@Table与@Column自动生成的replace sql. 返回自增主键的值，否则返回null.
	 */
	<T, R> R replace(T tableObject, Class<R> generatedKeyType) throws SQLException;

	/**
	 * 该方法调用注解@Table与@Column自动生成的insert-update-not-null sql. 返回影响的记录数。
	 */
	<T> int merge(T tableObject) throws SQLException;

	/**
	 * 该方法调用注解@Table与@Column自动生成的insert-update-not-null sql. 返回自增主键的值，否则返回null.
	 */
	<T, R> R merge(T tableObject, Class<R> generatedKeyType) throws SQLException;

	/**
	 * 该方法调用注解@Table与@Column自动生成的delete sql. 返回影响的记录数。
	 */
	<T> int delete(T tableObject) throws SQLException;

	<T> int deleteByKey(Class<T> tableType, Object... keys) throws SQLException;

	/**
	 * 该方法调用注解@Table与@Column自动生成的select sql. 返回查询的记录。
	 */
	<T> T select(T tableObject) throws SQLException;

	<T> void select2(T tableObject) throws SQLException;

	<T> T selectByKey(Class<T> tableType, Object... keys) throws SQLException;

	<T> List<T> selectAll(Class<T> tableType) throws SQLException;

	<T> List<T> selectRange(Class<T> tableType, int start, int max) throws SQLException;

	<T> T selectFirst(Class<T> tableType) throws SQLException;

	<T> void selectPage(Class<T> tableType, Page<T> page) throws SQLException;

	/**
	 * insertIgnore方法的批量形式。
	 */
	<T> int[] batchInsertIgnore(T[] tableObject) throws SQLException;

	/**
	 * insertIgnore方法的批量形式。
	 */
	<T> int[] batchInsert(T[] tableObject) throws SQLException;

	/**
	 * insert方法的批量形式。
	 */
	<T, R> R[] batchInsert(T[] tableObject, Class<R> generatedKeyType) throws SQLException;

	/**
	 * insertIgnore方法的批量形式。
	 */
	<T, R> R[] batchInsertIgnore(T[] tableObject, Class<R> generatedKeyType) throws SQLException;

	/**
	 * update方法的批量形式。
	 */
	<T> int[] batchUpdate(T[] tableObject) throws SQLException;

	/**
	 * replace方法的批量形式。
	 */
	<T> int[] batchReplace(T[] tableObject) throws SQLException;

	/**
	 * replace方法的批量形式。
	 */
	<T, R> R[] batchReplace(T[] tableObject, Class<R> generatedKeyType) throws SQLException;

	/**
	 * merge方法的批量形式。
	 */
	<T> int[] batchMerge(T[] tableObject) throws SQLException;

	/**
	 * merge方法的批量形式。
	 */
	<T, R> R[] batchMerge(T[] tableObject, Class<R> generatedKeyType) throws SQLException;

	/**
	 * delete方法的批量形式。
	 */
	<T> int[] batchDelete(T[] tableObjects) throws SQLException;

	<T> int[] batchDeleteByKey(Class<T> tableType, Object[][] keys) throws SQLException;

	/*********************************************
	 * 操作缓存SQL的Query与Update语法
	 *********************************************/
	/**
	 * 该方法调用queryId指定的查询SQL，返回elemType结果列表。参数params一般为map或pojo.
	 */
	<T> List<T> query(String queryId, Class<T> elemType, Object params) throws SQLException;

	/**
	 * 该方法调用queryId指定的查询SQL, 根据需要自动添加limit clause，返回elemType结果列表。参数params一般为map或pojo.
	 */
	<T> List<T> queryRange(String queryId, Class<T> elemType, int start, int max, Object params) throws SQLException;

	/**
	 * 该方法调用queryId指定的查询SQL, 返回第一条记录的elemType结果。参数params一般为map或pojo.
	 */
	<T> T queryFirst(String queryId, Class<T> elemType, Object params) throws SQLException;

	/**
	 * 该方法调用queryId指定的查询SQL，根据需要自动添加limit clause与order by clauase, 返回elemType结果分页列表。参数params一般为map或pojo.
	 */
	<T> void queryPage(String queryId, Class<T> elemType, Page<T> page, Object params) throws SQLException;

	/**
	 * 该方法调用updateId指定的更新SQL，返回影响的记录数。参数params一般为map或pojo.
	 */
	int execute(String updateId, Object params) throws SQLException;

	/**
	 * 该方法调用updateId指定的更新SQL，返回自增主键的值，否则返回null。参数params一般为map或pojo.
	 */
	<R> R execute(String updateId, Object params, Class<R> generatedKeyType) throws SQLException;

	/**
	 * execute的批量形式
	 */
	int[] batchExecute(String updateId, Object[] params) throws SQLException;

	/**
	 * execute的批量形式
	 */
	<R> R[] batchExecute(String updateId, Object[] params, Class<R> generatedKeyType) throws SQLException;

}