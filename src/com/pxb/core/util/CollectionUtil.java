/**    
* @Title: CollectionUtil.java  
* @Package com.pxb.core.util  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年7月28日 上午9:12:30  
* @version V1.0.0    
*/  
package com.pxb.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**  
 * @ClassName: CollectionUtil  
 * @Description: 工具集合类  
 * @author panxiaobo  
 * @date 2017年7月28日 上午9:12:30  
 *    
 */
public class CollectionUtil {
	/**
	 * 泛型HashMap
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> Map<K, V> newHashMap() {
		return new HashMap<K, V>();
	}
	
	/**
	 * 泛型TreeMap
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> Map<K, V> newTreeMap() {
		return new TreeMap<K, V>();
	}
	
	/**
	 * 泛型LinkedHashMap
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> Map<K, V> newLinkedHashMap() {
		return new LinkedHashMap<K, V>();
	}
	
	/**
	 * 泛型ConcurrentHashMap
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> Map<K, V> newConcurrentHashMap() {
		return new ConcurrentHashMap<K, V>();
	}
	
	/**
	 * 泛型ArrayList
	 * @param <E>
	 * @return
	 */
	public static <E> List<E> newArrayList() {
		return new ArrayList<E>();
	}
	
	/**
	 * 泛型LinkedList
	 * @param <E>
	 * @return
	 */
	public static <E> List<E> newLinkedList() {
		return new LinkedList<E>();
	}
	
	/**
	 * 泛型HashSet
	 * @param <E>
	 * @return
	 */
	public static <E> Set<E> newHashSet() {
		return new HashSet<E>();
	}
	
	/**
	 * 泛型TreeSet
	 * @param <E>
	 * @return
	 */
	public static <E> Set<E> newTreeSet() {
		return new TreeSet<E>();
	}
	
	/**
	 * 泛型LinkedHashSet
	 * @param <E>
	 * @return
	 */
	public static <E> Set<E> newLinkedHashSet() {
		return new LinkedHashSet<E>();
	}
	
	public static<E> List<E> newCopyOnWriteArrayList() {
		return new CopyOnWriteArrayList<E>();
	}
	
	/**
	 * 添加多个元素到列表中
	 * @param objects 参数列表
	 * @return 返回添加所有参数后的列表
	 */
	public static <T> List<T> addAll(T ... objects ) {
		List<T> list = newArrayList();
		for (T o : objects) {
			list.add(o);
		}
		return list;
	}
	
	/**
	 * 向列表中添加多个对象
	 * @param list 列表
	 * @param objects 多个对象
	 * @return 添加后的列表
	 */
	public static <T> List<T> addAll(List<T> list, T ... objects ) {
		if (list == null) {
			list = newArrayList();
		}
		for (T o : objects) {
			list.add(o);
		}
		return list;
	}
	
	public static void main(String[] args) {
		Map<String, String> map = CollectionUtil.newHashMap();
		map.put("1", "1");
		List<String> list = CollectionUtil.newArrayList();
		List<String> list2 = CollectionUtil.newLinkedList();
		List<String> list3 = CollectionUtil.<String>newArrayList();
		Set<String> set = CollectionUtil.newHashSet();
		Set<String> set2 = CollectionUtil.newTreeSet();
	}
}
