package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.service.SystemConfigService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.ConfigQueryDTO;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.ConfigVO;
import org.springframework.stereotype.Service;

/**
 * eb_system_config 系统配置服务实现。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
public class SystemConfigServiceImpl extends UpServiceImpl<SystemConfigMapper, SystemConfig>
		implements SystemConfigService {

	@Override
	public ConfigVO config(ConfigQueryDTO dto) {
		List<SystemConfig> list = this
			.list(Wrappers.lambdaQuery(SystemConfig.class).eq(SystemConfig::getCategory, dto.getType()));
		ConfigVO vo = new ConfigVO();
		for (SystemConfig c : list) {
			if (c.getConfigKey() != null) {
				vo.getEntries().put(c.getConfigKey(), c.getValue() == null ? "" : c.getValue());
			}
		}
		return vo;
	}

}
