package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.HayGroupDataMapper;
import com.bubblecloud.biz.oa.mapper.HayGroupMapper;
import com.bubblecloud.biz.oa.service.HayGroupService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.HayGroup;
import com.bubblecloud.oa.api.entity.HayGroupData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 海氏评估组服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
@RequiredArgsConstructor
public class HayGroupServiceImpl extends UpServiceImpl<HayGroupMapper, HayGroup> implements HayGroupService {

	private final HayGroupDataMapper hayGroupDataMapper;

	@Override
	public List<HayGroupData> dataList(Long groupId) {
		return hayGroupDataMapper.selectList(
				Wrappers.lambdaQuery(HayGroupData.class).eq(HayGroupData::getGroupId, groupId)
						.orderByDesc(HayGroupData::getId));
	}

	@Override
	public List<HayGroupData> historyList(Long groupId) {
		return hayGroupDataMapper.selectList(
				Wrappers.lambdaQuery(HayGroupData.class).eq(HayGroupData::getGroupId, groupId)
						.orderByDesc(HayGroupData::getAssessTime));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(HayGroup req) {
		req.setStatus(ObjectUtil.defaultIfNull(req.getStatus(), 1));
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(HayGroup req) {
		return super.update(req);
	}

}
