package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;

import com.bubblecloud.common.core.util.R;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.mapper.SystemStorageMapper;
import com.bubblecloud.biz.oa.service.SystemStorageService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.entity.SystemStorage;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 云存储配置实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
@RequiredArgsConstructor
public class SystemStorageServiceImpl extends UpServiceImpl<SystemStorageMapper, SystemStorage>
		implements SystemStorageService {

	private final SystemConfigMapper systemConfigMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateDomain(SystemStorage dto) {
		SystemStorage s = baseMapper.selectById(dto.getId());
		if (ObjectUtil.isNull(s)) {
			throw new IllegalArgumentException("记录不存在");
		}
		baseMapper.updateById(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatus(Long id) {
		SystemStorage info = baseMapper.selectById(id);
		if (ObjectUtil.isNull(info)) {
			throw new IllegalArgumentException("记录不存在");
		}
		if (StrUtil.isBlank(info.getDomain())) {
			throw new IllegalArgumentException("请先设置空间域名");
		}
		info.setStatus(1);
		info.setIsDelete(0);
		info.setUpdateTime((int) (System.currentTimeMillis() / 1000L));
		info.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(info);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setUploadType(Integer type) {
		long active = baseMapper.selectCount(Wrappers.lambdaQuery(SystemStorage.class)
				.eq(SystemStorage::getType, type)
				.eq(SystemStorage::getStatus, 1)
				.eq(SystemStorage::getIsDelete, 0));
		if (active == 0 && type != 1) {
			throw new IllegalArgumentException("没有正在使用的存储空间");
		}
		SystemConfig row = systemConfigMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
				.eq(SystemConfig::getConfigKey, "upload_type")
				.eq(SystemConfig::getEntid, 0));
		if (ObjectUtil.isNotNull(row)) {
			row.setValue(String.valueOf(type));
			systemConfigMapper.updateById(row);
		}
	}

	@Override
	public Integer getUploadType() {
		SystemConfig row = systemConfigMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
				.eq(SystemConfig::getConfigKey, "upload_type")
				.eq(SystemConfig::getEntid, 0));
		if (ObjectUtil.isNull(row) || ObjectUtil.isNull(row.getValue())) {
			return 1;
		}
		try {
			return Integer.parseInt(row.getValue().trim());
		} catch (NumberFormatException ex) {
			return 1;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveBasicConfig(JsonNode body) {
		if (ObjectUtil.isNull(body) || !body.isObject()) {
			return;
		}
		body.fields().forEachRemaining(e -> upsertConfig(e.getKey(), e.getValue().asText()));
	}

	private void upsertConfig(String key, String value) {
		SystemConfig row = systemConfigMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
				.eq(SystemConfig::getConfigKey, key)
				.eq(SystemConfig::getEntid, 0));
		if (ObjectUtil.isNotNull(row)) {
			row.setValue(value);
			systemConfigMapper.updateById(row);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(SystemStorage req) {
		req.setAcl(StrUtil.isNotBlank(req.getAcl()) ? req.getAcl() : "public-read");
		req.setIsSsl(0);
		req.setStatus(0);
		req.setIsDelete(0);
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(SystemStorage req) {
		return super.update(req);
	}

}
