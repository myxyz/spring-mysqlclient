package com.github.risedragon.mysql.data;

import java.util.List;

import com.github.risedragon.mysql.annotation.Match;
import com.github.risedragon.mysql.annotation.Option;

public class ReferenceAnnotation {

	public String name;

	public List<String> columns;

	public String targetTable;

	public List<String> targetColumns;

	public Match match;

	public Option onDelete;

	public Option onUpdate;
}
