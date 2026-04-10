package com.bubblecloud.common.core.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类标题：对象转换 类说明：
 *
 * @author qinlei
 * @date 2018/11/22
 */
public class PojoConvertUtil {

	/**
	 * 变量缓存
	 */
	private static final Map<String, Map<String, Field>> cacheFields = new ConcurrentHashMap<>();

	private static final Set<Class> basicClass = new HashSet<>();

	static {
		basicClass.add(Integer.class);
		basicClass.add(Character.class);
		basicClass.add(Byte.class);
		basicClass.add(Float.class);
		basicClass.add(Double.class);
		basicClass.add(Boolean.class);
		basicClass.add(Long.class);
		basicClass.add(Short.class);
		basicClass.add(String.class);
		basicClass.add(BigDecimal.class);
	}

	/**
	 * 将具有相同属性的类型进行转换
	 * @param orig
	 * @param <T>
	 * @return
	 */
	public static <T> T convertPojo(Object orig, Class<T> targetClass) {
		if (orig == null) {
			return null;
		}
		try {
			T target = targetClass.newInstance();
			BeanUtil.copyProperties(orig, target);
			return target;
		}
		catch (Throwable t) {
			return null;
		}
	}

	/**
	 * 转换list
	 * @param list
	 * @param targetClass
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> convertPojoList(List list, Class<T> targetClass) {
		if (CollUtil.isEmpty(list)) {
			return new ArrayList<>();
		}
		List<T> result = new ArrayList<>(list.size());
		for (Object object : list) {
			result.add(convertPojo(object, targetClass));
		}
		return result;
	}

	/**
	 * 获取字段值
	 * @param field
	 * @param obj
	 * @return
	 */
	private static Object getFiledValue(Field field, Object obj) throws IllegalAccessException {
		// 获取原有的访问权限
		boolean access = field.isAccessible();
		try {
			// 设置可访问的权限
			field.setAccessible(true);
			return field.get(obj);
		}
		finally {
			// 恢复访问权限
			field.setAccessible(access);
		}
	}

	/**
	 * 设置方法值
	 * @param field
	 * @param obj
	 * @param value
	 * @throws IllegalAccessException
	 */
	private static void setFieldValue(Field field, Object obj, Object value) throws IllegalAccessException {
		// 获取原有的访问权限
		boolean access = field.isAccessible();
		try {
			// 设置可访问的权限
			field.setAccessible(true);
			field.set(obj, value);
		}
		finally {
			// 恢复访问权限
			field.setAccessible(access);
		}
	}

	/**
	 * 设置Map
	 * @param value
	 * @param origField
	 * @param targetField
	 * @param targetObject
	 * @param <T>
	 */
	private static <T> void setMap(Map value, Field origField, Field targetField, T targetObject)
			throws IllegalAccessException, InstantiationException {
		Type origType = origField.getGenericType();
		Type targetType = targetField.getGenericType();
		if (origType instanceof ParameterizedType && targetType instanceof ParameterizedType) {// 泛型类型
			ParameterizedType origParameterizedType = (ParameterizedType) origType;
			Type[] origTypes = origParameterizedType.getActualTypeArguments();
			ParameterizedType targetParameterizedType = (ParameterizedType) targetType;
			Type[] targetTypes = targetParameterizedType.getActualTypeArguments();
			if (origTypes != null && origTypes.length == 2 && targetTypes != null && targetTypes.length == 2) {// 正常泛型,查看第二个泛型是否不为基本类型
				Class clazz = (Class) origTypes[1];
				if (!isBasicType(clazz) && !clazz.equals(targetTypes[1])) {// 如果不是基本类型并且泛型不一致，则需要继续转换
					Set<Map.Entry> entries = value.entrySet();
					Map targetMap = value.getClass().newInstance();
					for (Map.Entry entry : entries) {
						targetMap.put(entry.getKey(), convertPojo(entry.getValue(), (Class) targetTypes[1]));
					}
					setFieldValue(targetField, targetObject, targetMap);
					return;
				}
			}
		}
		setFieldValue(targetField, targetObject, value);
	}

	/**
	 * 设置集合
	 * @param value
	 * @param origField
	 * @param targetField
	 * @param targetObject
	 * @param <T>
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static <T> void setCollection(Collection value, Field origField, Field targetField, T targetObject)
			throws IllegalAccessException, InstantiationException {
		Type origType = origField.getGenericType();
		Type targetType = targetField.getGenericType();
		if (origType instanceof ParameterizedType && targetType instanceof ParameterizedType) {// 泛型类型
			ParameterizedType origParameterizedType = (ParameterizedType) origType;
			Type[] origTypes = origParameterizedType.getActualTypeArguments();
			ParameterizedType targetParameterizedType = (ParameterizedType) targetType;
			Type[] targetTypes = targetParameterizedType.getActualTypeArguments();
			if (origTypes != null && origTypes.length == 1 && targetTypes != null && targetTypes.length == 1) {// 正常泛型,查看第二个泛型是否不为基本类型
				Class clazz = (Class) origTypes[0];
				if (!isBasicType(clazz) && !clazz.equals(targetTypes[0])) {// 如果不是基本类型并且泛型不一致，则需要继续转换
					Collection collection = value.getClass().newInstance();
					for (Object obj : value) {
						collection.add(convertPojo(obj, (Class) targetTypes[0]));
					}
					setFieldValue(targetField, targetObject, collection);
					return;
				}
			}
		}
		setFieldValue(targetField, targetObject, value);
	}

	/**
	 * 设置枚举类型
	 * @param value
	 * @param origField
	 * @param targetField
	 * @param targetObject
	 * @param <T>
	 */
	private static <T> void setEnum(Enum value, Field origField, Field targetField, T targetObject) throws Exception {
		if (origField.equals(targetField)) {
			setFieldValue(targetField, targetObject, value);
		}
		else {
			// 枚举类型都具有一个static修饰的valueOf方法
			Method method = targetField.getType().getMethod("valueOf", String.class);
			setFieldValue(targetField, targetObject, method.invoke(null, value.toString()));
		}
	}

	/**
	 * 设置日期类型
	 * @param value
	 * @param targetField
	 * @param targetFieldType
	 * @param targetObject
	 * @param <T>
	 */
	private static <T> void setDate(Date value, Field targetField, Class targetFieldType, T targetObject,
			boolean sameType) throws IllegalAccessException {
		Date date = null;
		if (sameType) {
			date = value;
		}
		else if (targetFieldType.equals(java.sql.Date.class)) {
			date = new java.sql.Date(value.getTime());
		}
		else if (targetFieldType.equals(Date.class)) {
			date = new Date(value.getTime());
		}
		else if (targetFieldType.equals(java.sql.Timestamp.class)) {
			date = new java.sql.Timestamp(value.getTime());
		}
		setFieldValue(targetField, targetObject, date);
	}

	/**
	 * 获取适配方法
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	private static Field getTargetField(Class clazz, String fieldName) {
		String classKey = clazz.getName();
		Map<String, Field> fieldMap = cacheFields.get(classKey);
		if (fieldMap == null) {
			fieldMap = new HashMap<>();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (isStatic(field)) {
					continue;
				}
				fieldMap.put(field.getName(), field);
			}
			cacheFields.put(classKey, fieldMap);
		}
		return fieldMap.get(fieldName);
	}

	/**
	 * 确实是否为基础类型
	 * @param clazz
	 * @return
	 */
	private static boolean isBasicType(Class clazz) {
		return clazz.isPrimitive() || basicClass.contains(clazz);
	}

	/**
	 * 判断变量是否有静态修饰符static
	 * @param field
	 * @return
	 */
	private static boolean isStatic(Field field) {
		return (8 & field.getModifiers()) == 8;
	}

}
