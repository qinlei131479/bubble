package com.bubblecloud.biz.oa.util;

import com.bubblecloud.biz.oa.constant.config.OaCurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import cn.hutool.core.util.ObjectUtil;

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
		if (ObjectUtil.isNull(auth) || ObjectUtil.isNull(auth.getPrincipal())) {
			return null;
		}
		if (auth.getPrincipal() instanceof OaCurrentUser u) {
			return u.getId();
		}
		return null;
	}

}
