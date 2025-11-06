package com.bubblecloud.daemon.quartz.exception;

import java.io.Serial;

/**
 * 定时任务异常类
 *
 * @author qinlei
 * @date 2025/05/31
 */
public class TaskException extends Exception {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 无参构造方法，创建一个TaskException实例
	 */
	public TaskException() {
		super();
	}

	/**
	 * 构造方法，使用指定消息创建TaskException实例
	 * @param msg 异常信息
	 */
	public TaskException(String msg) {
		super(msg);
	}

}
