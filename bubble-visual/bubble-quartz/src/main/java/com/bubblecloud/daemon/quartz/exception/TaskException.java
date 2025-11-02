package com.bubblecloud.daemon.quartz.exception;

/**
 * 定时任务异常
 *
 * @author 郑健楠
 */
public class TaskException extends Exception {

	public TaskException() {
		super();
	}

	public TaskException(String msg) {
		super(msg);
	}

}
