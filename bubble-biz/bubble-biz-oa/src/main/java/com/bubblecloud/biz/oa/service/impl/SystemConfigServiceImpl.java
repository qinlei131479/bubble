package com.bubblecloud.biz.oa.service.impl;

import java.util.List;
import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.service.SystemConfigService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.ConfigQueryDTO;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.vo.ConfigVO;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;

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
			if (ObjectUtil.isNotNull(c.getConfigKey())) {
				vo.getEntries().put(c.getConfigKey(), ObjectUtil.isNull(c.getValue()) ? "" : c.getValue());
			}
		}
		return vo;
	}

	@Override
	public boolean isRegistrationOpen() {
		SystemConfig reg = this.getOne(Wrappers.lambdaQuery(SystemConfig.class)
			.eq(SystemConfig::getConfigKey, "registration_open")
			.last("LIMIT 1"), false);
		return ObjectUtil.isNotNull(reg) && ("1".equals(reg.getValue()) || "true".equalsIgnoreCase(reg.getValue()));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(SystemConfig req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(SystemConfig req) {
		return super.update(req);
	}

}
