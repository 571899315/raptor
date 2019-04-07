package com.raptor.common.util;

import java.lang.reflect.Modifier;

public class ReflectionUtil {

	public static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	public static boolean isFinal(Class<?> clazz) {
		return Modifier.isFinal(clazz.getModifiers());
	}

}
