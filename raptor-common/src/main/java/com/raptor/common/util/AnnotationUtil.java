package com.raptor.common.util;

import java.util.ArrayList;
import java.util.List;

import com.raptor.common.annotation.RaptorServer;

public class AnnotationUtil {

	/***
	 * 获取server上的类地址
	 * @param packageNames
	 * @return
	 */
	public static List<Class<?>> listByAnnotation(String[] packageNames) {
		List<Class<?>> clsList = ClassUtil.getClasses(packageNames);
		List<Class<?>> result = new ArrayList<>();
		if (clsList != null && clsList.size() > 0) {
			for (Class<?> clazz : clsList) {
				boolean isInterface = clazz.isInterface();
				if (isInterface) {
					boolean annotation = clazz.isAnnotationPresent(RaptorServer.class);
					if (annotation) {
						result.add(clazz);
					}
				}

			}
		}
		return result;
	}
}
