package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.DictDataMapper;
import com.bubblecloud.biz.oa.service.DictDataService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.config.DictDataTreeQueryDTO;
import com.bubblecloud.oa.api.entity.DictData;
import com.bubblecloud.oa.api.vo.config.DictDataTreeNodeVO;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 字典数据服务实现。
 *
 * @author qinlei
 * @date 2026/3/30 下午10:10
 */
@Service
public class DictDataServiceImpl extends UpServiceImpl<DictDataMapper, DictData> implements DictDataService {

	@Override
	public List<DictDataTreeNodeVO> treeDictData(DictDataTreeQueryDTO query) {
		var q = Wrappers.lambdaQuery(DictData.class);
		if (ObjectUtil.isNotNull(query.getTypeId())) {
			q.eq(DictData::getTypeId, query.getTypeId());
		}
		if (StrUtil.isNotBlank(query.getTypes())) {
			q.eq(DictData::getTypeName, query.getTypes());
		}
		if (StrUtil.isNotBlank(query.getName())) {
			q.like(DictData::getName, query.getName());
		}
		if (ObjectUtil.isNotNull(query.getStatus())) {
			q.eq(DictData::getStatus, query.getStatus());
		}
		q.orderByAsc(DictData::getSort).orderByAsc(DictData::getId);
		List<DictData> flat = this.list(q);
		return buildTree(flat);
	}

	private static List<DictDataTreeNodeVO> buildTree(List<DictData> flat) {
		Map<String, List<DictData>> byPid = flat.stream()
			.collect(Collectors.groupingBy(d -> ObjectUtil.isNull(d.getPid()) ? "" : d.getPid()));
		List<DictDataTreeNodeVO> roots = new ArrayList<>();
		for (DictData d : flat) {
			if (ObjectUtil.isNull(d.getPid()) || d.getPid().isEmpty() || "0".equals(d.getPid())) {
				roots.add(toNode(d, byPid));
			}
		}
		return roots;
	}

	private static DictDataTreeNodeVO toNode(DictData d, Map<String, List<DictData>> byPid) {
		DictDataTreeNodeVO n = new DictDataTreeNodeVO();
		n.setId(d.getId());
		n.setName(d.getName());
		n.setValue(d.getValue());
		n.setPid(d.getPid());
		n.setTypeId(d.getTypeId());
		n.setStatus(d.getStatus());
		n.setSort(d.getSort());
		List<DictData> children = byPid.getOrDefault(d.getValue(), List.of());
		if (!children.isEmpty()) {
			List<DictDataTreeNodeVO> ch = new ArrayList<>();
			for (DictData c : children) {
				ch.add(toNode(c, byPid));
			}
			n.setChildren(ch);
		}
		return n;
	}

}
