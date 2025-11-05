package com.pig4cloud.pig.auth.support.handler;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import com.pig4cloud.pig.common.core.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * 表单登录失败处理逻辑
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Slf4j
public class FormAuthenticationFailureHandler implements AuthenticationFailureHandler {

	/**
	 * 当认证失败时调用
	 * @param request 认证尝试发生的请求
	 * @param response 响应对象
	 * @param exception 拒绝认证时抛出的异常
	 */
	@Override
	@SneakyThrows
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) {
		log.debug("表单登录失败:{}", exception.getLocalizedMessage());

		// 获取当前请求的context-path
		String contextPath = request.getContextPath();

		// 构建重定向URL，加入context-path
		String url = HttpUtil.encodeParams(
				String.format("%s/token/login?error=%s", contextPath, exception.getMessage()),
				CharsetUtil.CHARSET_UTF_8);

		try {
			WebUtils.getResponse().sendRedirect(url);
		}
		catch (IOException e) {
			log.error("重定向失败", e);
		}
	}

}
