package com.utils.closure;

/**
 * 可转换
 */
public interface Convertable<Q, T> {
	
	/**
	 * @param q  被转换对象
	 * @return 转换结果
	 */
	T convert(Q q);
}
