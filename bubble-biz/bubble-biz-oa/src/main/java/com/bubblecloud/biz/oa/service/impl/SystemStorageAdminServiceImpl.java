package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemConfigMapper;
import com.bubblecloud.biz.oa.mapper.SystemStorageMapper;
import com.bubblecloud.biz.oa.service.SystemStorageAdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.SystemConfig;
import com.bubblecloud.oa.api.entity.SystemStorage;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class SystemStorageAdminServiceImpl extends UpServiceImpl<SystemStorageMapper, SystemStorage>
		implements SystemStorageAdminService {

	private final SystemConfigMapper systemConfigMapper;

	@Override
	public List<SystemStorage> list(Integer type) {
		var q = Wrappers.lambdaQuery(SystemStorage.class)
			.eq(SystemStorage::getIsDelete, 0)
			.orderByDesc(SystemStorage::getId);
		if (ObjectUtil.isNotNull(type) && type > 0) {
			q.eq(SystemStorage::getType, type);
		}
		return baseMapper.selectList(q);
	}

	@Override
	public SystemStorage get(int id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveStorage(int type, String accessKey, String name, String region, String acl) {
		int now = (int) (System.currentTimeMillis() / 1000L);
		SystemStorage s = new SystemStorage();
		s.setType(type);
		s.setAccessKey(ObjectUtil.isNull(accessKey) ? "" : accessKey);
		s.setName(ObjectUtil.isNull(name) ? "" : name);
		s.setRegion(ObjectUtil.isNull(region) ? "" : region);
		s.setAcl(StrUtil.isNotBlank(acl) ? acl : "public-read");
		s.setDomain("");
		s.setCdn("");
		s.setCname("");
		s.setIsSsl(0);
		s.setStatus(0);
		s.setIsDelete(0);
		s.setAddTime(now);
		s.setUpdateTime(now);
		s.setCreatedAt(LocalDateTime.now());
		s.setUpdatedAt(LocalDateTime.now());
		baseMapper.insert(s);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateDomain(int id, String domain, String cdn) {
		SystemStorage s = baseMapper.selectById(id);
		if (ObjectUtil.isNull(s)) {
			throw new IllegalArgumentException("记录不存在");
		}
		s.setDomain(domain);
		if (ObjectUtil.isNotNull(cdn)) {
			s.setCdn(cdn);
		}
		s.setUpdateTime((int) (System.currentTimeMillis() / 1000L));
		s.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(s);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setActiveStatus(int id) {
		SystemStorage info = baseMapper.selectById(id);
		if (ObjectUtil.isNull(info)) {
			throw new IllegalArgumentException("记录不存在");
		}
		if (StrUtil.isBlank(info.getDomain())) {
			throw new IllegalArgumentException("请先设置空间域名");
		}
		baseMapper.update(null,
				Wrappers.lambdaUpdate(SystemStorage.class)
					.eq(SystemStorage::getType, info.getType())
					.set(SystemStorage::getStatus, 0));
		info.setStatus(1);
		info.setIsDelete(0);
		info.setUpdateTime((int) (System.currentTimeMillis() / 1000L));
		info.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(info);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteStorage(int id) {
		SystemStorage s = baseMapper.selectById(id);
		if (ObjectUtil.isNull(s)) {
			return;
		}
		s.setIsDelete(1);
		s.setUpdateTime((int) (System.currentTimeMillis() / 1000L));
		s.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(s);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setUploadType(int type) {
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
	public int getUploadType() {
		SystemConfig row = systemConfigMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
			.eq(SystemConfig::getConfigKey, "upload_type")
			.eq(SystemConfig::getEntid, 0));
		if (ObjectUtil.isNull(row) || ObjectUtil.isNull(row.getValue())) {
			return 1;
		}
		try {
			return Integer.parseInt(row.getValue().trim());
		}
		catch (NumberFormatException ex) {
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

}
