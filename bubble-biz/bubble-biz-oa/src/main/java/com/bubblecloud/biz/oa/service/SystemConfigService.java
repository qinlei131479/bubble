package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.ConfigQueryDTO;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.ConfigVO;

/**
 * eb_system_config 系统配置服务。
 *
 * @author qinlei
 */
public interface SystemConfigService extends UpService<SystemConfig> {

	/**
	 * 按类型读取配置为键值 VO。
	 *
	 * @param dto 查询条件
	 * @return 配置 VO
	 */
	ConfigVO config(ConfigQueryDTO dto);

}
