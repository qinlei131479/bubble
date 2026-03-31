package com.bubblecloud.biz.oa.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * PHP JWT 鉴权过滤器。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Component
public class OaPhpJwtAuthenticationFilter extends OncePerRequestFilter {

	private final OaPhpJwtTokenService tokenService;

	public OaPhpJwtAuthenticationFilter(OaPhpJwtTokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = resolveToken(request);
		if (StrUtil.isNotBlank(token)) {
			Map<String, Object> claims = tokenService.parseAndValidate(token);
			if (ObjectUtil.isNotNull(claims) && ObjectUtil.isNotNull(claims.get("sub"))) {
				Long userId = Long.valueOf(String.valueOf(claims.get("sub")));
				String account = ObjectUtil.isNull(claims.get("account")) ? null : String.valueOf(claims.get("account"));
				OaCurrentUser principal = new OaCurrentUser(userId, account);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
						null, Collections.emptyList());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (StrUtil.isBlank(authorization)) {
			return null;
		}
		if (authorization.startsWith("Bearer ")) {
			return authorization.substring(7);
		}
		return null;
	}

}
