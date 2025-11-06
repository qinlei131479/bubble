package com.bubblecloud.common.core.exception;

import lombok.NoArgsConstructor;

/**
 * 受检异常类，继承自RuntimeException
 *
 * @author lengleng
 * @date 2025/05/30
 */
@NoArgsConstructor
public class CheckedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CheckedException(String message) {
		super(message);
	}

	public CheckedException(Throwable cause) {
		super(cause);
	}

	public CheckedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CheckedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
