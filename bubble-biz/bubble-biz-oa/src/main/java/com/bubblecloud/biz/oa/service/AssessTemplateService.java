package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.AssessTemplateSaveDTO;
import com.bubblecloud.oa.api.entity.AssessTemplate;
import com.bubblecloud.oa.api.vo.SimplePageVO;

/**
 * 绩效考核模板服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface AssessTemplateService extends UpService<AssessTemplate> {

	SimplePageVO pageTemplate(Pg<AssessTemplate> pg, AssessTemplate query);

	void createTemplate(AssessTemplateSaveDTO dto);

	void updateTemplate(long id, AssessTemplateSaveDTO dto);

	void removeTemplate(long id);

	void toggleFavorite(long id);

	void setCover(long id, String cover);

}
