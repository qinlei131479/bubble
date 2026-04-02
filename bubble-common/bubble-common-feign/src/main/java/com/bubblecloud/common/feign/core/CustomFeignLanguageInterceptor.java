package com.bubblecloud.common.feign.core;

import com.bubblecloud.common.core.util.WebUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * feign 语言环境透传拦截器
 *
 * @author qinlei
 * @date 2025/3/26
 * <p>
 */
public class CustomFeignLanguageInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		WebUtils.getRequest().ifPresent(request -> {
			String language = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
			if (StringUtils.hasText(language)) {
				template.header(HttpHeaders.ACCEPT_LANGUAGE, language);
			}
		});
	}

}
