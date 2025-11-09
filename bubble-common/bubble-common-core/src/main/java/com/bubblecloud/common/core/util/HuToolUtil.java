package com.bubblecloud.common.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HuTool的二次包装类
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
public class HuToolUtil {
	/**
	 * 默认枚举转换code和name字段
	 */
	private static final String ENUM_CODE = "code";
	private static final String ENUM_NAME = "name";
	private static final String BASE_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * 有效邮件正则表达式
	 */
	private static final Pattern PATTERN_EMAIL = Pattern.compile(".+@.+\\..+");
	/**
	 * 手机号正则表达式
	 */
	private static final Pattern PATTERN_MOBILE = Pattern.compile("[0-9]{11}");
	/**
	 * 路径分隔符
	 */
	private static final String PATH_SEREPERATOR = "/";

	/**
	 * 判断是否有效邮件
	 *
	 * @param email
	 */
	public static boolean isValidEmail(String email) {
		if (email == null) {
			return false;
		}
		Matcher m = PATTERN_EMAIL.matcher(email);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否有效手机号
	 *
	 * @param mobile
	 */
	public static boolean isValidMobile(String mobile) {
		Matcher m = PATTERN_MOBILE.matcher(mobile);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置字段值
	 *
	 * @param obj       对象
	 * @param fieldName 字段名
	 * @param value     值，值类型必须与字段类型匹配，不会自动转换对象类型
	 * @throws UtilException 包装IllegalAccessException异常
	 */
	public static void setFieldValueIfExist(Object obj, String fieldName, Object value) throws UtilException {
		if (obj != null) {
			if (Map.class.isInstance(obj)) {
				// 如果是map
				((Map) obj).put(fieldName, value);
			} else if (ReflectUtil.hasField(obj.getClass(), fieldName)) {
				ReflectUtil.setFieldValue(obj, fieldName, value);
			}
		}
	}

	/**
	 * 获取字段值
	 *
	 * @param obj       对象
	 * @param fieldName 字段名
	 * @throws UtilException 包装IllegalAccessException异常
	 */
	public static Object getFieldValueIfExist(Object obj, String fieldName) throws UtilException {
		if (obj != null && ReflectUtil.hasField(obj.getClass(), fieldName)) {
			return ReflectUtil.getFieldValue(obj, fieldName);
		}
		return null;
	}

	/**
	 * 格式化字符串 ",,,a,b,c，d,，, b e," ==》a,b,c,d,b,e
	 *
	 * @param string
	 * @return
	 */
	public static List<String> convertStringSplitByDouToList(String string) {
		if (StrUtil.isNotBlank(string)) {
			return Arrays.asList(formatStringSplitByDou(string).split(","));
		} else {
			return null;
		}
	}

	/**
	 * 格式化字符串 ",,,a,b,c，d,，, b e," ==》a,b,c,d,b,e
	 *
	 * @param string
	 * @return
	 */
	public static String formatStringSplitByDou(String string) {
		if (StrUtil.isBlank(string)) {
			return string;
		} else {
			String regex = "[　\\s,， ]+";
			return string.trim().replaceAll(regex, ",").replaceAll("^" + regex, "").replaceAll(regex + "$", "");
		}
	}

	/**
	 * 组合路径和文件
	 *
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public static String joinDir(String dir, String fileName) {
		if (fileName.indexOf(PATH_SEREPERATOR) == 0) {
			fileName = fileName.substring(1);
		}
		return dir + (dir.endsWith(PATH_SEREPERATOR) ? "" : PATH_SEREPERATOR) + fileName;
	}

	/**
	 * 组合路径和文件，返回文件格式
	 *
	 * @param dirs
	 * @return
	 */
	public static File joinDirsAsFile(String... dirs) {
		return new File(joinDirs(dirs));
	}

	/**
	 * 组合多个路径部分
	 *
	 * @param dirs
	 * @return
	 */
	public static String joinDirs(String... dirs) {
		if (ArrayUtil.isNotEmpty(dirs)) {
			return joinDirs(Arrays.asList(dirs));
		} else {
			return null;
		}
	}

	/**
	 * 组合多个路径部分
	 *
	 * @param dirs
	 * @return
	 */
	public static String joinDirs(List<String> dirs) {
		if (CollUtil.isNotEmpty(dirs)) {
			String ret = dirs.get(0);
			for (int i = 1; i < dirs.size(); i++) {
				ret = joinDir(ret, dirs.get(i));
			}
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * 获取文件名扩展名（小写,不带点的）
	 *
	 * @param fileName :文件名
	 * @return
	 */
	public static String getFileExtName(String fileName) {
		if (fileName == null) {
			return "";
		} else {
			int lastIndex = fileName.lastIndexOf(".");
			return (lastIndex == -1) ? "" : fileName.substring(lastIndex + 1).toLowerCase();
		}
	}

	/**
	 * 执行命令
	 *
	 * @param command window:{"cmd.exe /c dir"}
	 * @return
	 * @throws Exception
	 */
	public static String callSystem(String[] command) {
		InputStream is = null;
		try {
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			StringBuffer sb = new StringBuffer();
			byte[] buffer = new byte[256];
			int cnt = 0;
			is = process.getInputStream();
			while ((cnt = is.read(buffer)) >= 0) {
				sb.append(new String(buffer, 0, cnt));
			}
			return sb.toString();
		} catch (Throwable e) {
			e.printStackTrace();
			return e.getMessage();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
