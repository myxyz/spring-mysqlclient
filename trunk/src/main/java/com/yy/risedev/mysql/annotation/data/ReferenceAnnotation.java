package com.yy.risedev.mysql.annotation.data;

import java.util.List;

import com.yy.risedev.mysql.annotation.Match;
import com.yy.risedev.mysql.annotation.Option;

public class ReferenceAnnotation {

	public String name;

	public List<String> columns;

	public String targetTable;

	public List<String> targetColumns;

	public Match match;

	public Option onDelete;

	public Option onUpdate;
}
