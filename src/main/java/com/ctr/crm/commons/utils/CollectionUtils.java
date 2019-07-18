package com.ctr.crm.commons.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * 说明：
 * @author eric
 * @date 2019年5月7日 下午2:29:17
 */
public class CollectionUtils {

	@SafeVarargs
	public static <T> Collection<T> add(final Collection<T> c, final T... elements){
		if(c == null) return null;
		if(elements == null || elements.length == 0) return c;
		Collection<T> result = c;
		for (T t : elements) {
			try {
				result.add(t);
			} catch (UnsupportedOperationException e) {
				result = new ArrayList<>(c);
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * 添加元素，如果重复则不添加，并且返回去重后的集合
	 * @param c
	 * @param elements
	 * @return
	 */
	@SafeVarargs
	public static <T> Collection<T> addNotRepeat(final Collection<T> c, final T... elements){
		if(c == null) return null;
		if(elements == null || elements.length == 0) return c;
		Collection<T> result = new HashSet<>(c);
		for (T t : elements) {
			try {
				result.add(t);
			} catch (UnsupportedOperationException e) {
				result = new HashSet<>(c);
				result.add(t);
			}
		}
		return result;
	}
	
	@SafeVarargs
	public static <T> Collection<T> remove(final Collection<T> c, final T... elements){
		if(c == null) return null;
		if(elements == null || elements.length == 0) return c;
		Collection<T> result = c;
		for (T t : elements) {
			try {
				result.remove(t);
			} catch (UnsupportedOperationException e) {
				result = new ArrayList<>(c);
				result.remove(t);
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		List<Integer> list = Arrays.asList(1,2,3,3);
		System.out.println(list);
		Collection<Integer> c = addNotRepeat(list, 4);
		System.out.println(c);
	}
	
}
