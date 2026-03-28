package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.vo.SiteVO;

/**
 * 站点展示配置（聚合 eb_system_config、eb_enterprise）。
 */
public interface SiteService {

	/**
	 * 站点配置，供登录页等使用。
	 * @return 站点 VO
	 */
	SiteVO site();

}
