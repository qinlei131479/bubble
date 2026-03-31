package com.bubblecloud.biz.oa.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 当前登录用户（PHP JWT）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public final class OaSecurityUtil {

	private OaSecurityUtil() {
	}

	public static Long currentUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null) {
			return null;
		}
		if (auth.getPrincipal() instanceof OaCurrentUser u) {
			return u.getId();
		}
		return null;
	}

}
