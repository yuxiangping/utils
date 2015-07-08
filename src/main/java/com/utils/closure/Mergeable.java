package com.utils.closure;

/**
 * 可合并
 */
public interface Mergeable<T> {
	
	/**
	 *@param t1  合并目标对象
	 *@param t2  合并目标对象
	 *@return 合并结果
	 */
	 T merger(T t1, T t2);
}
