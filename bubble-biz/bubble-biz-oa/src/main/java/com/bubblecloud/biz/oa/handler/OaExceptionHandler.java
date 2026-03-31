package com.bubblecloud.biz.oa.handler;

import com.bubblecloud.biz.oa.support.PhpResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * OA 异常统一处理。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestControllerAdvice(basePackages = "com.bubblecloud.biz.oa.controller")
public class OaExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public PhpResponse<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
		return PhpResponse.failed(ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public PhpResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		String msg = ex.getBindingResult().getFieldError() == null ? "参数错误"
				: ex.getBindingResult().getFieldError().getDefaultMessage();
		return PhpResponse.failed(msg);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public PhpResponse<Void> handleConstraintViolationException(ConstraintViolationException ex) {
		return PhpResponse.failed(ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public PhpResponse<Void> handleException(Exception ex) {
		return PhpResponse.failed(ex.getMessage());
	}

}
