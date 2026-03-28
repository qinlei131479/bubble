package com.bubblecloud.biz.oa.support;

import java.io.Serializable;

import lombok.Data;

/**
 * 兼容 PHP 前端的响应结构。
 *
 * @param <T> 数据类型
 */
@Data
public class PhpResponse<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer status;

	private String msg;

	private T data;

	public static <T> PhpResponse<T> ok(T data) {
		PhpResponse<T> response = new PhpResponse<>();
		response.setStatus(200);
		response.setMsg("ok");
		response.setData(data);
		return response;
	}

	public static <T> PhpResponse<T> failed(String msg) {
		PhpResponse<T> response = new PhpResponse<>();
		response.setStatus(400);
		response.setMsg(msg);
		response.setData(null);
		return response;
	}

}
