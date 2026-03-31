package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.SiteVO;

/**
 * 站点展示配置（聚合 eb_system_config、eb_enterprise）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
public interface SiteService extends UpService<SystemConfig> {

	/**
	 * 站点配置，供登录页等使用。
	 * @return 站点 VO
	 */
	SiteVO site();

}
