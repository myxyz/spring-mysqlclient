package com.github.risedragon.mysql;

public class MysqlClientException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MysqlClientException() {
		super();
	}

	public MysqlClientException(String msg) {
		super(msg);
	}

	public MysqlClientException(Throwable t) {
		super(t);
	}

	public MysqlClientException(String msg, Throwable t) {
		super(msg, t);
	}
}
