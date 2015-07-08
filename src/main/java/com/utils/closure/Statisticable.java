package com.utils.closure;

/**
 * 可统计
 */
public interface Statisticable<Q, T> {
	/**
	 * @param q 初始值
	 * @param t 统计对象
	 * @return 统计结果
	 */
	 Q statistic(Q q, T t);
}
