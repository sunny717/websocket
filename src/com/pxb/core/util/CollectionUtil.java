/**    
* @Title: CollectionUtil.java  
* @Package com.pxb.core.util  
* @Description: TODO(��һ�仰�������ļ���ʲô)  
* @author panxiaobo    
* @date 2017��7��28�� ����9:12:30  
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
 * @Description: ���߼�����  
 * @author panxiaobo  
 * @date 2017��7��28�� ����9:12:30  
 *    
 */
public class CollectionUtil {
	/**
	 * ����HashMap
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> Map<K, V> newHashMap() {
		return new HashMap<K, V>();
	}
	
	/**
	 * ����TreeMap
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> Map<K, V> newTreeMap() {
		return new TreeMap<K, V>();
	}
	
	/**
	 * ����LinkedHashMap
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> Map<K, V> newLinkedHashMap() {
		return new LinkedHashMap<K, V>();
	}
	
	/**
	 * ����ConcurrentHashMap
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> Map<K, V> newConcurrentHashMap() {
		return new ConcurrentHashMap<K, V>();
	}
	
	/**
	 * ����ArrayList
	 * @param <E>
	 * @return
	 */
	public static <E> List<E> newArrayList() {
		return new ArrayList<E>();
	}
	
	/**
	 * ����LinkedList
	 * @param <E>
	 * @return
	 */
	public static <E> List<E> newLinkedList() {
		return new LinkedList<E>();
	}
	
	/**
	 * ����HashSet
	 * @param <E>
	 * @return
	 */
	public static <E> Set<E> newHashSet() {
		return new HashSet<E>();
	}
	
	/**
	 * ����TreeSet
	 * @param <E>
	 * @return
	 */
	public static <E> Set<E> newTreeSet() {
		return new TreeSet<E>();
	}
	
	/**
	 * ����LinkedHashSet
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
	 * ��Ӷ��Ԫ�ص��б���
	 * @param objects �����б�
	 * @return ����������в�������б�
	 */
	public static <T> List<T> addAll(T ... objects ) {
		List<T> list = newArrayList();
		for (T o : objects) {
			list.add(o);
		}
		return list;
	}
	
	/**
	 * ���б�����Ӷ������
	 * @param list �б�
	 * @param objects �������
	 * @return ��Ӻ���б�
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
