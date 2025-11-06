package com.bubblecloud.codegen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bubblecloud.codegen.entity.GenTemplateEntity;
import com.pig4cloud.pig.common.core.util.R;

/**
 * 代码生成模板服务接口
 *
 * @author qinlei
 * @date 2025/05/31
 */
public interface GenTemplateService extends IService<GenTemplateEntity> {

	/**
	 * 检查版本信息
	 * @return 返回检查结果，包含版本信息
	 */
	R checkVersion();

	/**
	 * 在线更新
	 * @return 更新结果
	 */
	R onlineUpdate();

}
