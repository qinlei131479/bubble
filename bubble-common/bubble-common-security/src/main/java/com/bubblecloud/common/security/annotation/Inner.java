package com.bubblecloud.common.security.annotation;

import java.lang.annotation.*;

/**
 * 内部注解：用于标记方法或类型是否需要AOP统一处理
 *
 * @author lengleng
 * @date 2025/05/31
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inner {

	/**
	 * 是否AOP统一处理
	 * @return false, true
	 */
	boolean value() default true;

	/**
	 * 需要特殊判空的字段(预留)
	 * @return {}
	 */
	String[] field() default {};

}
