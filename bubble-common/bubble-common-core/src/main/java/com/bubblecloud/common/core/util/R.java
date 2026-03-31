package com.bubblecloud.common.core.util;

import com.bubblecloud.common.core.constant.CommonConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author lengleng
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldNameConstants
public class R<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private int code;

	@Getter
	@Setter
	private String msg;

	@Getter
	@Setter
	private T data;

	public static <T> R<T> ok() {
		return restResult(null, CommonConstants.SUCCESS, null);
	}

	public static <T> R<T> ok(T data) {
		return restResult(data, CommonConstants.SUCCESS, null);
	}

	public static <T> R<T> ok(T data, String msg) {
		return restResult(data, CommonConstants.SUCCESS, msg);
	}

	public static <T> R<T> failed() {
		return restResult(null, CommonConstants.FAIL, null);
	}

	public static <T> R<T> failed(String msg) {
		return restResult(null, CommonConstants.FAIL, msg);
	}

	public static <T> R<T> failed(T data) {
		return restResult(data, CommonConstants.FAIL, null);
	}

	public static <T> R<T> failed(T data, String msg) {
		return restResult(data, CommonConstants.FAIL, msg);
	}

	public static <T> R<T> restResult(T data, int code, String msg) {
		R<T> apiResult = new R<>();
		apiResult.setCode(code);
		apiResult.setData(data);
		apiResult.setMsg(msg);
		return apiResult;
	}

	/**
	 * 与 OA 原 {@code PhpResponse} 成功形态一致：{@code msg} 固定为 {@code ok}，业务载荷在 {@code data}。
	 */
	public static <T> R<T> phpOk(T data) {
		return restResult(data, CommonConstants.SUCCESS, "ok");
	}

	/**
	 * 与 OA 原 {@code PhpResponse} 失败形态一致。
	 */
	public static <T> R<T> phpFailed(String msg) {
		return restResult(null, CommonConstants.FAIL, msg);
	}

	/**
	 * OA / PHP 前端兼容：HTTP 语义状态（200 成功 / 400 失败），与 {@link #code}（0/1）并行序列化。
	 */
	@JsonProperty(value = "status", access = JsonProperty.Access.READ_ONLY)
	public Integer getPhpHttpStatus() {
		if (code == CommonConstants.SUCCESS) {
			return 200;
		}
		if (code == CommonConstants.FAIL) {
			return 400;
		}
		return null;
	}

}
