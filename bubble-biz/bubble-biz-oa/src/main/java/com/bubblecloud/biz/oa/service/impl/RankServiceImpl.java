package com.bubblecloud.biz.oa.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.RankJobMapper;
import com.bubblecloud.biz.oa.mapper.RankMapper;
import com.bubblecloud.biz.oa.service.RankService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.Rank;
import com.bubblecloud.oa.api.entity.RankJob;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;

/**
 * 职级服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class RankServiceImpl extends UpServiceImpl<RankMapper, Rank> implements RankService {

	@Autowired
	private RankJobMapper rankJobMapper;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public JsonNode buildCreateFormPayload(Long entid) {
		List<RankJob> jobs = rankJobMapper.selectList(Wrappers.lambdaQuery(RankJob.class)
			.eq(ObjectUtil.isNotNull(entid), RankJob::getEntid, entid)
			.eq(RankJob::getStatus, 1)
			.orderByAsc(RankJob::getId)
			.select(RankJob::getId, RankJob::getName));
		ArrayNode tree = objectMapper.createArrayNode();
		for (RankJob j : jobs) {
			ObjectNode o = objectMapper.createObjectNode();
			o.put("id", j.getId());
			o.put("name", j.getName() == null ? "" : j.getName());
			tree.add(o);
		}
		ObjectNode root = objectMapper.createObjectNode();
		root.set("tree", tree);
		root.set("jobInfo", objectMapper.createObjectNode());
		return root;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Rank req) {
		req.setSort(ObjectUtil.defaultIfNull(req.getSort(), 0));
		req.setStatus(ObjectUtil.defaultIfNull(req.getStatus(), 1));
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Rank req) {
		return super.update(req);
	}

}
