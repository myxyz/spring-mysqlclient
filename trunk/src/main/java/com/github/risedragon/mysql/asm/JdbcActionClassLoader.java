package com.github.risedragon.mysql.asm;

public class JdbcActionClassLoader extends ClassLoader {

	public static final JdbcActionClassLoader Loader = new JdbcActionClassLoader();

	public JdbcActionClassLoader() {
		super(ClassLoader.getSystemClassLoader());
	}

	public Class<?> defineClass(String name, byte[] data) {
		return super.defineClass(name, data, 0, data.length);
	}

	public Class<?> loadClassAssertNotFailed(String className) {
		try {
			return super.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
