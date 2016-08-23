package com.yy.risedev.mysql.annotation.data;

import java.util.List;

import com.yy.risedev.mysql.annotation.IndexType;
import com.yy.risedev.mysql.annotation.Using;

public class IndexAnnotation {

	public String name;

	public List<String> columns;

	public IndexType type;

	public Using using;
}
