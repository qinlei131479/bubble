package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.EnterpriseUserJobAnalysisMapper;
import com.bubblecloud.biz.oa.service.EnterpriseUserJobAnalysisService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.EnterpriseUserJobAnalysis;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 企业员工工作分析表 CRUD 实现。
 *
 * @author qinlei
 * @date 2026/4/5 15:30
 */
@Service
public class EnterpriseUserJobAnalysisServiceImpl
		extends UpServiceImpl<EnterpriseUserJobAnalysisMapper, EnterpriseUserJobAnalysis>
		implements EnterpriseUserJobAnalysisService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(EnterpriseUserJobAnalysis dto) {
		return super.create(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(EnterpriseUserJobAnalysis dto) {
		EnterpriseUserJobAnalysis existing = baseMapper.selectById(dto.getId());
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		return super.update(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void upsertByEntAndUser(long entid, long adminId, String rawData) {
		String data = StrUtil.replace(rawData, "\\", "");
		EnterpriseUserJobAnalysis one = baseMapper.selectOne(Wrappers.lambdaQuery(EnterpriseUserJobAnalysis.class)
			.eq(EnterpriseUserJobAnalysis::getEntid, entid)
			.eq(EnterpriseUserJobAnalysis::getUserId, adminId));
		LocalDateTime now = LocalDateTime.now();
		if (ObjectUtil.isNotNull(one)) {
			one.setData(data);
			one.setUpdatedAt(now);
			baseMapper.updateById(one);
		}
		else {
			EnterpriseUserJobAnalysis row = new EnterpriseUserJobAnalysis();
			row.setEntid(entid);
			row.setUserId(adminId);
			row.setData(data);
			row.setCreatedAt(now);
			row.setUpdatedAt(now);
			baseMapper.insert(row);
		}
	}

}
