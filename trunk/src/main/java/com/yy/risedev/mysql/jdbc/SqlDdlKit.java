package com.yy.risedev.mysql.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yy.risedev.mysql.JavaType;
import com.yy.risedev.mysql.MysqlClientException;
import com.yy.risedev.mysql.SqlType;
import com.yy.risedev.mysql.annotation.asm.AsmKit;
import com.yy.risedev.mysql.annotation.data.ClassMetaInfo;
import com.yy.risedev.mysql.annotation.data.ColumnAnnotation;
import com.yy.risedev.mysql.annotation.data.FieldMetaInfo;
import com.yy.risedev.mysql.annotation.data.IndexAnnotation;
import com.yy.risedev.mysql.annotation.data.PrimaryKeyAnnotation;
import com.yy.risedev.mysql.annotation.data.ReferenceAnnotation;
import com.yy.risedev.mysql.annotation.data.TableAnnotation;

public class SqlDdlKit extends SqlKit {

	private static final Log logger = LogFactory.getLog(SqlDdlKit.class);

	public static void processUpdateTable(Connection conn, Collection<ClassMetaInfo> tableMetaInfos) throws SQLException {
		//避免错误,表名都转成大写比较
		Set<String> tableSet = getUpperCaseTableNames(conn);
		for (ClassMetaInfo classMetaInfo : tableMetaInfos) {
			if (tableSet.contains(classMetaInfo.tableName.toUpperCase())) {
				logger.info("Check Table: " + classMetaInfo.tableName);
				checkAndAddColumns(conn, classMetaInfo, classMetaInfo.tableName);
				checkAndAddPrimaryKey(conn, classMetaInfo, classMetaInfo.tableName);
				checkAndAddForeignKey(conn, classMetaInfo, classMetaInfo.tableName);
				checkAndAddIndexes(conn, classMetaInfo, classMetaInfo.tableName);
			} else {
				logger.info("Create Table: " + classMetaInfo.tableName);
				createTable(conn, classMetaInfo);
			}
		}
	}

	public static void processCheckConfig(Connection conn, Collection<SqlMeta> sqlMetas) {
		// @TODO
	}

	private static void createTable(Connection conn, ClassMetaInfo classMetaInfo) throws SQLException {

		String ddl = genTableDdl(classMetaInfo);
		logger.info(ddl);

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(ddl);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	// 全部表名转成大写判断
	public static Set<String> getUpperCaseTableNames(Connection conn) throws SQLException {
		Set<String> tables = new HashSet<String>();
		ResultSet rs = null;
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			rs = dbmd.getTables(null, null, null, null);
			while (rs.next()) {
				tables.add(rs.getString(3).toUpperCase());
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return tables;
	}

	public static void checkAndAddColumns(Connection conn, ClassMetaInfo classMetaInfo, String table) throws SQLException {

		Map<String, ColInfo> colInfoMap = new HashMap<String, ColInfo>(classMetaInfo.fields.size());

		DatabaseMetaData dbmd = conn.getMetaData();
		ResultSet rs = null;
		try {
			rs = dbmd.getColumns(null, null, table, null);
			while (rs.next()) {
				ColInfo ca = new ColInfo();
				ca.name = rs.getString("COLUMN_NAME"); // COLUMN_NAME
				ca.type = rs.getInt("DATA_TYPE");// DATA_TYPE
				ca.length = rs.getInt("COLUMN_SIZE");// COLUMN_SIZE
				ca.decimals = rs.getInt("DECIMAL_DIGITS");// DECIMAL_DIGITS
				ca.notNull = "YES".equals(rs.getString("IS_NULLABLE"));// NULLABLE
				ca.autoIncrement = "YES".equals(rs.getString("IS_AUTOINCREMENT"));// IS_AUTOINCREMENT
				colInfoMap.put(ca.name, ca);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		ColumnAnnotation columnAnnotation;
		Statement stmt = null;

		for (FieldMetaInfo fieldMetaInfo : classMetaInfo.fields.values()) {
			if ((columnAnnotation = fieldMetaInfo.columnAnnotation) != null) {
				String name = getColumnName(fieldMetaInfo, columnAnnotation);
				ColInfo colInfo = colInfoMap.get(name);
				if (colInfo == null) {
					StringBuilder sb = new StringBuilder(256);
					sb.append("ALTER TABLE ").append(identifier(classMetaInfo.tableName)).append(" ADD COLUMN ").append(genColumnDdl(fieldMetaInfo, columnAnnotation));
					String sql = sb.toString();
					logger.info(sql);

					try {
						stmt = conn.createStatement();
						stmt.executeUpdate(sql);
					} catch (SQLException e) {
						throw new MysqlClientException("为表增加字段失败：" + classMetaInfo.tableName, e);
					} finally {
						if (stmt != null) {
							stmt.close();
						}
					}
				}
			}
		}

	}

	static String getColumnName(FieldMetaInfo fieldMetaInfo, ColumnAnnotation columnAnnotation) {
		String name = columnAnnotation.name;
		if (AsmKit.isEmpty(name)) {
			name = fieldMetaInfo.name;
		}
		return name;
	}

	static SqlType getColumnSqlType(FieldMetaInfo fieldMetaInfo, ColumnAnnotation columnAnnotation) {
		SqlType type = columnAnnotation.type;
		if (type == null) {
			type = defaultSqlType4JavaType(fieldMetaInfo.descriptor);
		}
		return type;
	}

	static Integer getColumnLength(ColumnAnnotation columnAnnotation, SqlType sqlType) {
		Integer length = columnAnnotation.length;
		if (length == null) {
			length = defaultSqlTypeLength(sqlType);
		}
		return length;
	}

	static Integer getColumnDecimal(ColumnAnnotation columnAnnotation, SqlType sqlType) {
		Integer decimals = columnAnnotation.decimals;
		if (decimals == null) {
			decimals = defaultSqlTypeDecimal(sqlType);
		}
		return decimals;
	}

	static class ColInfo {
		String name;
		int type;
		int length;
		int decimals;
		boolean notNull;
		boolean autoIncrement;
	}

	// 需要检测字段中的key
	public static void checkAndAddPrimaryKey(Connection conn, ClassMetaInfo classMetaInfo, String table) throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();

		List<String> keys = new LinkedList<String>();
		ResultSet rs = null;
		try {
			rs = dbmd.getPrimaryKeys(null, null, table);
			while (rs.next()) {
				keys.add(rs.getString("COLUMN_NAME"));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		if (keys.size() == 0) {
			if (classMetaInfo.keys != null && classMetaInfo.keys.size() > 0) {
				StringBuilder sb = new StringBuilder(128);
				sb.append("ALTER TABLE ").append(identifier(classMetaInfo.tableName)).append("ADD PRIMARY KEY(");
				for (String key : classMetaInfo.keys) {
					sb.append(identifier(key)).append(',');
				}
				sb.setCharAt(sb.length() - 1, ')');
				logger.info(sb.toString());

				Statement stmt = null;
				try {
					stmt = conn.createStatement();
					stmt.executeUpdate(sb.toString());
				} catch (SQLException e) {
					throw new MysqlClientException("为表增加主键失败：" + classMetaInfo.tableName, e);
				} finally {
					if (stmt != null) {
						stmt.close();
					}
				}
			}
		} else if (!equalsIgnoreOrder(keys, classMetaInfo.keys)) {
			logger.warn("主键差异：元数据" + classMetaInfo.keys + ",表定义" + keys);
		}
	}

	static boolean equalsIgnoreOrder(List<String> list1, List<String> list2) {
		if (list1 == list2) {
			return true;
		}
		if (list1 == null || list2 == null) {
			return false;
		}
		return list1.size() == list2.size() && list1.containsAll(list2);
	}

	public static void checkAndAddForeignKey(Connection conn, ClassMetaInfo classMetaInfo, String table) throws SQLException {
		List<ReferenceAnnotation> foreignKeyAnnotation = classMetaInfo.foreignKeyAnnotation;
		if (foreignKeyAnnotation != null && foreignKeyAnnotation.size() > 0) {
			Set<String> names = new HashSet<String>();
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = null;
			try {
				rs = dbmd.getImportedKeys(null, null, table);
				while (rs.next()) {
					names.add(rs.getString("FK_NAME"));
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
			for (ReferenceAnnotation ra : foreignKeyAnnotation) {
				if (!names.contains(ra.name)) {
					StringBuilder sb = new StringBuilder(256);
					sb.append("ALTER TABLE ").append(identifier(table)).append(" ADD ").append(genForeignKeyDdl(ra));

					logger.info(sb.toString());
					Statement stmt = null;
					try {
						stmt = conn.createStatement();
						stmt.executeUpdate(sb.toString());
					} catch (SQLException e) {
						throw new MysqlClientException("为表增加外键失败：" + classMetaInfo.tableName, e);
					} finally {
						if (stmt != null) {
							stmt.close();
						}
					}
				}
			}
		}
	}

	// 需要检测字段中的unique
	public static void checkAndAddIndexes(Connection conn, ClassMetaInfo classMetaInfo, String table) throws SQLException {
		List<IndexAnnotation> indexesAnnotation = classMetaInfo.indexesAnnotation;
		if (indexesAnnotation != null && indexesAnnotation.size() > 0) {
			Set<String> names = new HashSet<String>();
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = null;
			try {
				rs = dbmd.getIndexInfo(null, null, table, false, true);
				while (rs.next()) {
					names.add(rs.getString("INDEX_NAME"));
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
			}

			for (IndexAnnotation ia : indexesAnnotation) {
				if (!names.contains(ia.name)) {
					StringBuilder sb = new StringBuilder(256);
					sb.append("ALTER TABLE ").append(identifier(table)).append(" ADD ").append(genIndexesDdl(ia));

					logger.info(sb.toString());
					Statement stmt = null;
					try {
						stmt = conn.createStatement();
						stmt.executeUpdate(sb.toString());
					} catch (SQLException e) {
						throw new MysqlClientException("为表增加索引失败：" + classMetaInfo.tableName, e);
					} finally {
						if (stmt != null) {
							stmt.close();
						}
					}
				}
			}
		}
	}

	public static String genTableDdl(ClassMetaInfo classMetaInfo) {

		StringBuilder cols = new StringBuilder(128);
		for (FieldMetaInfo fieldMetaInfo : classMetaInfo.fields.values()) {
			ColumnAnnotation columnAnnotation = fieldMetaInfo.columnAnnotation;
			if (columnAnnotation != null) {
				if (cols.length() > 0) {
					cols.append(",\n");
				}
				cols.append(genColumnDdl(fieldMetaInfo, columnAnnotation));
			}
		}

		PrimaryKeyAnnotation primaryKeyAnnotation = classMetaInfo.primaryKeyAnnotation;
		if (primaryKeyAnnotation != null) {
			cols.append(",\n");
			cols.append(genPrimaryKeyDdl(primaryKeyAnnotation));
		}

		List<ReferenceAnnotation> foreignKeyAnnotation = classMetaInfo.foreignKeyAnnotation;
		if (foreignKeyAnnotation != null && foreignKeyAnnotation.size() > 0) {
			for (ReferenceAnnotation ra : foreignKeyAnnotation) {
				cols.append(",\n").append(genForeignKeyDdl(ra));
			}
		}

		List<IndexAnnotation> indexesAnnotation = classMetaInfo.indexesAnnotation;
		if (indexesAnnotation != null && indexesAnnotation.size() > 0) {
			for (IndexAnnotation ia : indexesAnnotation) {
				cols.append(",\n").append(genIndexesDdl(ia));
			}
		}

		StringBuilder sb = new StringBuilder(2048);
		sb.append("CREATE TABLE IF NOT EXISTS ").append(identifier(classMetaInfo.tableName)).append("(\n").append(cols).append("\n)");

		TableAnnotation tableAnnotation = classMetaInfo.tableAnnotation;
		if (tableAnnotation.engine != null && tableAnnotation.engine.sqlValue != null) {
			sb.append("\nENGINE=").append(tableAnnotation.engine.sqlValue);
		}

		if (AsmKit.isNotEmpty(tableAnnotation.characterSet)) {
			sb.append("\nDEFAULT CHARACTER SET=").append(tableAnnotation.characterSet);
		}

		if (AsmKit.isNotEmpty(tableAnnotation.collate)) {
			sb.append("\nCOLLATE=").append(tableAnnotation.collate);
		}
		if (AsmKit.isNotEmpty(tableAnnotation.comment)) {
			sb.append("\nCOMMENT=").append('\'').append(tableAnnotation.comment).append('\'');
		}

		return sb.toString();
	}

	static String genPrimaryKeyDdl(PrimaryKeyAnnotation primaryKeyAnnotation) {
		StringBuilder cols = new StringBuilder(256);
		cols.append("PRIMARY KEY(");
		for (String key : primaryKeyAnnotation.columns) {
			cols.append(identifier(key)).append(',');
		}
		cols.setCharAt(cols.length() - 1, ')');

		if (primaryKeyAnnotation.using != null && primaryKeyAnnotation.using.sqlValue != null) {
			cols.append(" USING ").append(primaryKeyAnnotation.using.sqlValue);
		}
		return cols.toString();
	}

	static String genIndexesDdl(IndexAnnotation ia) {
		StringBuilder cols = new StringBuilder(1024);

		if (ia.type != null && ia.type.sqlValue != null) {
			cols.append(ia.type.sqlValue).append(" ");
		}
		cols.append("INDEX ").append(identifier(ia.name)).append("(");
		for (String col : ia.columns) {
			cols.append(identifier(col)).append(',');
		}
		cols.setCharAt(cols.length() - 1, ')');
		if (ia.using != null && ia.using.sqlValue != null) {
			cols.append(" USING ").append(ia.using.sqlValue);
		}
		return cols.toString();
	}

	static String genForeignKeyDdl(ReferenceAnnotation ra) {
		StringBuilder cols = new StringBuilder(1024);

		cols.append("CONSTRAINT ").append(identifier(ra.name)).append(" FOREIGN KEY(");
		for (String col : ra.columns) {
			cols.append(identifier(col)).append(',');
		}
		cols.setCharAt(cols.length() - 1, ')');
		cols.append(" REFERENCES ").append(identifier(ra.targetTable)).append("(");
		for (String col : ra.targetColumns) {
			cols.append(identifier(col)).append(',');
		}
		cols.setCharAt(cols.length() - 1, ')');

		return cols.toString();
	}

	static String genColumnDdl(FieldMetaInfo fieldMetaInfo, ColumnAnnotation columnAnnotation) {
		StringBuilder cols = new StringBuilder(128);

		String name = getColumnName(fieldMetaInfo, columnAnnotation);
		SqlType sqlType = getColumnSqlType(fieldMetaInfo, columnAnnotation);
		cols.append(identifier(name)).append(" ").append(sqlType.sqlValue);

		Integer length = getColumnLength(columnAnnotation, sqlType);
		if (length != null) {
			cols.append("(").append(length);
			Integer decimals = getColumnDecimal(columnAnnotation, sqlType);
			if (decimals != null) {
				cols.append(",").append(decimals);
			}
			cols.append(")");
		}
		if (Boolean.TRUE.equals(columnAnnotation.notNull)) {
			cols.append(" NOT NULL");
		}
		if (Boolean.TRUE.equals(columnAnnotation.unique)) {
			cols.append(" UNIQUE");
		}
		if (Boolean.TRUE.equals(columnAnnotation.autoIncrement)) {
			cols.append(" AUTO_INCREMENT");
		}
		if (Boolean.TRUE.equals(columnAnnotation.key)) {
			cols.append(" PRIMARY KEY");
		}
		if (AsmKit.isNotEmpty(columnAnnotation.defaultValue)) {
			cols.append(" DEFAULT ").append(columnAnnotation.defaultValue);
		}

		return cols.toString();
	}

	static Integer defaultSqlTypeLength(SqlType sqlType) {
		switch (sqlType) {
		case VARCHAR:
		case VARCHAR_BINARY:
			return 255;
		default:
			return null;
		}

	}

	static Integer defaultSqlTypeDecimal(SqlType sqlType) {
		switch (sqlType) {
		default:
			return null;
		}

	}

	static SqlType defaultSqlType4JavaType(String descriptor) {
		JavaType javaType = JavaType.match(descriptor);
		switch (javaType) {
		case _boolean:
		case _Boolean:
			return SqlType.BIT;
		case _char:
		case _Character:
			return SqlType.CHAR;
		case _byte:
		case _Byte:
			return SqlType.TINYINT;
		case _short:
		case _Short:
			return SqlType.SMALLINT;
		case _int:
		case _Integer:
			return SqlType.INT;
		case _long:
		case _Long:
			return SqlType.BIGINT;
		case _float:
		case _Float:
			return SqlType.FLOAT;
		case _double:
		case _Double:
			return SqlType.DOUBLE;
		case _String:
			return SqlType.VARCHAR;
		case _BigDecimal:
			return SqlType.DECIMAL;
		case _BigInteger:
			return SqlType.NUMERIC;
		case _JavaUtilDate:
			return SqlType.DATETIME;
		case _Date:
			return SqlType.DATE;
		case _Time:
			return SqlType.TIME;
		case _Timestamp:
			return SqlType.TIMESTAMP;
		case _bytes:
			return SqlType.BINARY;
		case _Ref:
			return SqlType.VARCHAR;
		case _URL:
			return SqlType.VARCHAR;
		case _Blob:
			return SqlType.BLOB;
		case _Clob:
			return SqlType.TEXT;
		case _SQLXML:
			return SqlType.VARCHAR;
		case _InputStream:
			return SqlType.LONGBLOB;
		case _Reader:
			return SqlType.LONGTEXT;
		default:
			throw new MysqlClientException("Can't convert javaType to any other sqlType: " + javaType);
		}
	}
}
