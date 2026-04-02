package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.AssessTemplate;

/**
 * 绩效考核模板服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface AssessTemplateService extends UpService<AssessTemplate> {

	void toggleFavorite(Long id);

	void setCover(Long id, String cover);

}
