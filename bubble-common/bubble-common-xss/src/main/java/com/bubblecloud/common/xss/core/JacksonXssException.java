package com.bubblecloud.common.xss.core;

import lombok.Getter;

import java.io.IOException;
import java.io.Serial;

/**
 * Jackson XSS 异常类，用于处理 JSON 序列化/反序列化过程中的 XSS 安全问题
 *
 * @author lengleng
 * @date 2025/05/31
 */
@Getter
public class JacksonXssException extends IOException implements XssException {

	@Serial
	private static final long serialVersionUID = 1L;

	private final String input;

	/**
	 * 构造JacksonXssException异常
	 * @param input 引发异常的输入内容
	 * @param message 异常信息
	 */
	public JacksonXssException(String input, String message) {
		super(message);
		this.input = input;
	}

}
