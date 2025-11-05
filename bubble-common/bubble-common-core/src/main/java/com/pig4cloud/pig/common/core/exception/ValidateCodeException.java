package com.pig4cloud.pig.common.core.exception;

/**
 * 验证码异常类
 *
 * @author lengleng
 * @date 2018/06/22
 */
public class ValidateCodeException extends RuntimeException {

	private static final long serialVersionUID = -7285211528095468156L;

	public ValidateCodeException() {
	}

	public ValidateCodeException(String msg) {
		super(msg);
	}

}
