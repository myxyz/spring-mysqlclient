package com.yy.risedev.mysql.annotation.data;

import java.util.LinkedHashMap;
import java.util.List;

public class ClassMetaInfo {

	public String internalName;

	public MetaAnnotation metaAnnotation;
	public TableAnnotation tableAnnotation;
	public PrimaryKeyAnnotation primaryKeyAnnotation;
	public List<ReferenceAnnotation> foreignKeyAnnotation;
	public List<IndexAnnotation> indexesAnnotation;
	public OptimisticLockAnnotation optimisticLockAnnotation;

	/*************************************
	 * 由于继承关系，fields, getter, setter采用 逆向解析，如果已经存在则跳过，节省性能
	 *************************************/
	public final LinkedHashMap<String, FieldMetaInfo> fields = new LinkedHashMap<String, FieldMetaInfo>();
	public final LinkedHashMap<String, MethodMetaInfo> getters = new LinkedHashMap<String, MethodMetaInfo>(); // key is column name, but not property
	public final LinkedHashMap<String, MethodMetaInfo> setters = new LinkedHashMap<String, MethodMetaInfo>(); // key is column name, but not property

	/*************************************
	 * 如果带有@Table注解
	 *************************************/
	public String tableName; // table name
	public List<String> keys; // primary key
	public List<String> columns; // column name

	/*************************************
	 * 辅助属性,基类的数量
	 *************************************/
	public int hierarchies = 1;
}
