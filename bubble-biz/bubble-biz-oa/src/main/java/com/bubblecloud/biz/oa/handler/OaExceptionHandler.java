package com.bubblecloud.biz.oa.handler;

import com.bubblecloud.common.core.util.R;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import cn.hutool.core.util.ObjectUtil;

/**
 * OA 异常统一处理。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@RestControllerAdvice(basePackages = "com.bubblecloud.biz.oa.controller")
public class OaExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public R<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
		return R.phpFailed(ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		String msg = ObjectUtil.isNull(ex.getBindingResult().getFieldError()) ? "参数错误"
				: ex.getBindingResult().getFieldError().getDefaultMessage();
		return R.phpFailed(msg);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public R<Void> handleConstraintViolationException(ConstraintViolationException ex) {
		return R.phpFailed(ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public R<Void> handleException(Exception ex) {
		return R.phpFailed(ex.getMessage());
	}

}
