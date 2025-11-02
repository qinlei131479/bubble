package com.bubblecloud.codegen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bubblecloud.codegen.entity.GenTemplateEntity;
import com.pig4cloud.pig.common.core.util.R;

/**
 * 模板
 *
 * @author PIG
 * @date 2023-02-21 17:15:44
 */
public interface GenTemplateService extends IService<GenTemplateEntity> {

	/**
	 * 检查版本
	 * @return {@link R }
	 */
	R checkVersion();

	/**
	 * 在线更新
	 * @return {@link R }
	 */
	R onlineUpdate();

}
