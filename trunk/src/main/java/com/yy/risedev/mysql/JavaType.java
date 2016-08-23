package com.yy.risedev.mysql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;

import org.springframework.asm.Type;

/**
 * java属性类型
 */
public enum JavaType {

	_boolean(boolean.class), //
	_Boolean(Boolean.class), //
	_char(char.class), //
	_Character(Character.class), //
	_byte(byte.class), //
	_Byte(Byte.class), //
	_short(short.class), //
	_Short(Short.class), //
	_int(int.class), //
	_Integer(Integer.class), //
	_long(long.class), //
	_Long(Long.class), //
	_float(float.class), //
	_Float(Float.class), //
	_double(double.class), //
	_Double(Double.class), //
	_String(String.class), //
	_BigDecimal(BigDecimal.class), //
	_BigInteger(BigInteger.class), //
	_JavaUtilDate(java.util.Date.class), //
	_Date(Date.class), //
	_Time(Time.class), //
	_Timestamp(Timestamp.class), //
	_bytes(byte[].class), //
	_Ref(Ref.class), //
	_URL(URL.class), //
	_SQLXML(SQLXML.class), //
	_Blob(Blob.class), //
	_Clob(Clob.class), //
	_InputStream(InputStream.class), //
	_Reader(Reader.class), //
	_Object(Object.class);

	final Class<?> clazz;
	final String descriptor;

	private JavaType(Class<?> clazz) {
		this.clazz = clazz;
		this.descriptor = Type.getDescriptor(clazz);
	}

	public static JavaType match(String descriptor) {
		for (JavaType type : values()) {
			if (type.descriptor.equals(descriptor)) {
				return type;
			}
		}
		return _Object;
	}

	public static JavaType match(Class<?> clazz) {
		for (JavaType type : values()) {
			if (type.clazz == clazz) {
				return type;
			}
		}
		return _Object;
	}

}
