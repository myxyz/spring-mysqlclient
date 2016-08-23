package com.yy.risedev.mysql;

import java.util.List;

public class Page<T> {

	int start;
	int max;

	String orderBy;
	boolean orderDesc;

	int total;
	List<T> data;

	public Page() {
		this(0, 0, null, false);
	}

	public Page(int start, int max) {
		this(start, max, null, false);
	}

	public Page(int start, int max, String orderBy, boolean orderDesc) {
		this.start = start;
		this.max = max;
		this.orderBy = orderBy;
		this.orderDesc = orderDesc;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(boolean orderDesc) {
		this.orderDesc = orderDesc;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

}
