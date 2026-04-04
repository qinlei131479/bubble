package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

	private static final String[] METHOD_CONFIG_KEYS = { "upload_type", "thumb_big_height", "thumb_big_width",
			"thumb_mid_height", "thumb_mid_width", "thumb_small_height", "thumb_small_width", "image_watermark_status",
			"watermark_type", "watermark_image", "watermark_opacity", "watermark_position", "watermark_rotate",
			"watermark_text", "watermark_text_angle", "watermark_text_color", "watermark_text_size", "watermark_x",
			"watermark_y" };

	private final SystemConfigMapper systemConfigMapper;

	private final ObjectMapper objectMapper;

	@Override
	public List<SystemStorage> listStorages(Integer type) {
		return list(Wrappers.lambdaQuery(SystemStorage.class)
			.eq(SystemStorage::getIsDelete, 0)
			.eq(ObjectUtil.isNotNull(type), SystemStorage::getType, type)
			.orderByDesc(SystemStorage::getId));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateDomain(SystemStorage dto) {
		SystemStorage s = baseMapper.selectById(dto.getId());
		if (ObjectUtil.isNull(s)) {
			throw new IllegalArgumentException("记录不存在");
		}
		s.setDomain(dto.getDomain());
		s.setCdn(dto.getCdn());
		s.setUpdateTime((int) (System.currentTimeMillis() / 1000L));
		s.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(s);
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
		List<SystemStorage> sameType = list(Wrappers.lambdaQuery(SystemStorage.class)
			.eq(SystemStorage::getType, info.getType())
			.eq(SystemStorage::getIsDelete, 0));
		for (SystemStorage o : sameType) {
			if (ObjectUtil.equal(o.getId(), id)) {
				continue;
			}
			o.setStatus(0);
			o.setUpdateTime((int) (System.currentTimeMillis() / 1000L));
			o.setUpdatedAt(LocalDateTime.now());
			baseMapper.updateById(o);
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
		body.fields().forEachRemaining(e -> upsertConfigKey(e.getKey(), e.getValue().asText()));
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveCredentialConfig(JsonNode body) {
		if (ObjectUtil.isNull(body) || !body.isObject()) {
			return;
		}
		int type = body.path("type").asInt(0);
		if (type == 1) {
			return;
		}
		String accessKey = text(body, "accessKey");
		String secretKey = text(body, "secretKey");
		if (StrUtil.isBlank(accessKey) || StrUtil.isBlank(secretKey)) {
			return;
		}
		String appid = text(body, "appid");
		String storageRegion = text(body, "storageRegion");
		switch (type) {
			case 2 -> {
				upsertConfigKey("qiniu_accessKey", accessKey);
				upsertConfigKey("qiniu_secretKey", secretKey);
			}
			case 3 -> {
				upsertConfigKey("accessKey", accessKey);
				upsertConfigKey("secretKey", secretKey);
			}
			case 4 -> {
				upsertConfigKey("tengxun_accessKey", accessKey);
				upsertConfigKey("tengxun_secretKey", secretKey);
				if (StrUtil.isNotBlank(appid)) {
					upsertConfigKey("tengxun_appid", appid);
				}
			}
			case 5 -> {
				upsertConfigKey("jd_accessKey", accessKey);
				upsertConfigKey("jd_secretKey", secretKey);
				if (StrUtil.isNotBlank(storageRegion)) {
					upsertConfigKey("jd_storage_region", storageRegion);
				}
			}
			case 6 -> {
				upsertConfigKey("hw_accessKey", accessKey);
				upsertConfigKey("hw_secretKey", secretKey);
			}
			case 7 -> {
				upsertConfigKey("ty_accessKey", accessKey);
				upsertConfigKey("ty_secretKey", secretKey);
			}
			default -> {
			}
		}
	}

	@Override
	public ObjectNode storageMethodDetail() {
		ObjectNode root = objectMapper.createObjectNode();
		for (String key : METHOD_CONFIG_KEYS) {
			SystemConfig row = systemConfigMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
				.eq(SystemConfig::getConfigKey, key)
				.eq(SystemConfig::getEntid, 0)
				.last("LIMIT 1"));
			String val = (row == null || row.getValue() == null) ? "" : row.getValue();
			root.put(key, val);
		}
		return root;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteStorage(Long id) {
		if (ObjectUtil.isNull(id)) {
			throw new IllegalArgumentException("参数错误");
		}
		SystemStorage s = baseMapper.selectById(id);
		if (ObjectUtil.isNull(s) || ObjectUtil.defaultIfNull(s.getIsDelete(), 0) != 0) {
			throw new IllegalArgumentException("删除的云存储空间不存在");
		}
		if (ObjectUtil.defaultIfNull(s.getStatus(), 0) == 1) {
			throw new IllegalArgumentException("云存储正在使用中,需要启动其他空间才能删除");
		}
		s.setIsDelete(1);
		s.setUpdateTime((int) (System.currentTimeMillis() / 1000L));
		s.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(s);
		return true;
	}

	private void upsertConfigKey(String key, String value) {
		SystemConfig row = systemConfigMapper.selectOne(Wrappers.lambdaQuery(SystemConfig.class)
			.eq(SystemConfig::getConfigKey, key)
			.eq(SystemConfig::getEntid, 0));
		if (ObjectUtil.isNotNull(row)) {
			row.setValue(value);
			systemConfigMapper.updateById(row);
		}
	}

	private static String text(JsonNode n, String field) {
		if (ObjectUtil.isNull(n) || !n.has(field) || n.get(field).isNull()) {
			return "";
		}
		return n.get(field).asText("");
	}

}
