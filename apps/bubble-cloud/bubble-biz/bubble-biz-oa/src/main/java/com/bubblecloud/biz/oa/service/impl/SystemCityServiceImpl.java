package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.SystemCityMapper;
import com.bubblecloud.biz.oa.service.SystemCityService;
import com.bubblecloud.oa.api.entity.SystemCity;
import com.bubblecloud.oa.api.vo.common.CityTreeNodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;

/**
 * {@link SystemCityService} 实现。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
@Service
@RequiredArgsConstructor
public class SystemCityServiceImpl implements SystemCityService {

	private final SystemCityMapper systemCityMapper;

	@Override
	public List<CityTreeNodeVO> cityTree() {
		List<SystemCity> rows = systemCityMapper
			.selectList(Wrappers.lambdaQuery(SystemCity.class).orderByAsc(SystemCity::getId));
		if (ObjectUtil.isEmpty(rows)) {
			return List.of();
		}
		List<CityTreeNodeVO> nodes = new ArrayList<>(rows.size());
		for (SystemCity r : rows) {
			CityTreeNodeVO n = new CityTreeNodeVO();
			n.setValue(r.getCityId());
			n.setLabel(ObjectUtil.defaultIfNull(r.getName(), ""));
			n.setPid(ObjectUtil.defaultIfNull(r.getParentId(), 0));
			n.setChildren(new ArrayList<>());
			nodes.add(n);
		}
		Map<Integer, CityTreeNodeVO> byCityId = new HashMap<>(nodes.size());
		for (CityTreeNodeVO n : nodes) {
			byCityId.put(n.getValue(), n);
		}
		List<CityTreeNodeVO> roots = new ArrayList<>();
		for (CityTreeNodeVO n : nodes) {
			Integer p = n.getPid();
			if (ObjectUtil.isNull(p) || p == 0) {
				roots.add(n);
				continue;
			}
			CityTreeNodeVO parent = byCityId.get(p);
			if (ObjectUtil.isNotNull(parent)) {
				parent.getChildren().add(n);
			}
			else {
				roots.add(n);
			}
		}
		return roots;
	}

}
