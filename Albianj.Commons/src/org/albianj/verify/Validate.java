package org.albianj.verify;

import java.util.Collection;
import java.util.Map;

public class Validate {

	public static boolean isNullOrEmpty(Collection<?> collection) {
		return null == collection || collection.isEmpty();
	}

	public static boolean isNullOrEmpty(@SuppressWarnings("rawtypes") Map map) {
		return null == map || map.isEmpty();
	}

	public static boolean isNull(@SuppressWarnings("rawtypes") Map map) {
		return null == map;
	}

	public static boolean isNullOrEmpty(String value) {
		return null == value || value.isEmpty();
	}

	public static boolean isNullOrEmptyOrAllSpace(String value) {
		return null == value || value.trim().isEmpty();
	}

}
