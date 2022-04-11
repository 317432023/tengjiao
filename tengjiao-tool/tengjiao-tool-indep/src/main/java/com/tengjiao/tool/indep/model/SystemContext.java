package com.tengjiao.tool.indep.model;

/**
 * @author kangtengjiao
 */
public final class SystemContext {
	private SystemContext(){}

	private static final ThreadLocal<Object> TL = new ThreadLocal<>();

	public static Object get(Class<?> clazz) {
		Object o = TL.get();
		if (o == null) {
			try {
				o = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return o;
	}

	public static void set(Object o) {
		TL.set(o);
	}

	public static void remove() {
		TL.remove();
	}
}
