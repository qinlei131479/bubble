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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * PHP JWT 鉴权过滤器。
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
		if (StringUtils.hasText(token)) {
			Map<String, Object> claims = tokenService.parseAndValidate(token);
			if (claims != null && claims.get("sub") != null) {
				Long userId = Long.valueOf(String.valueOf(claims.get("sub")));
				String account = claims.get("account") == null ? null : String.valueOf(claims.get("account"));
				OaCurrentUser principal = new OaCurrentUser(userId, account);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null,
						Collections.emptyList());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (!StringUtils.hasText(authorization)) {
			return null;
		}
		if (authorization.startsWith("Bearer ")) {
			return authorization.substring(7);
		}
		return null;
	}

}
