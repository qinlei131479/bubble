package com.pig4cloud.pig.common.log.annotation;

import java.lang.annotation.*;

/**
 * 系统日志注解：用于标记需要记录操作日志的方法
 *
 * @author lengleng
 * @date 2025/05/31
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

	/**
	 * 描述
	 * @return {String}
	 */
	String value() default "";

	/**
	 * spel 表达式
	 * @return 日志描述
	 */
	String expression() default "";

}
