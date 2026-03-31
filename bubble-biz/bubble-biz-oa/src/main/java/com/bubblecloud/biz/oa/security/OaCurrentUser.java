package com.bubblecloud.biz.oa.security;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OA 当前用户凭证。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OaCurrentUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String account;

}
