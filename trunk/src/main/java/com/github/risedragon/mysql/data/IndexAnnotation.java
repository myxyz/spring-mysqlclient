package com.github.risedragon.mysql.data;

import java.util.List;

import com.github.risedragon.mysql.annotation.IndexType;
import com.github.risedragon.mysql.annotation.Using;

public class IndexAnnotation {

	public String name;

	public List<String> columns;

	public IndexType type;

	public Using using;
}
