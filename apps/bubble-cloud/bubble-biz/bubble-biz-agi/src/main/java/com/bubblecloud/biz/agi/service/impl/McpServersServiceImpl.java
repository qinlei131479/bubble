package com.bubblecloud.biz.agi.service.impl;

import com.bubblecloud.agi.api.entity.SupplierModel;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.McpServers;
import com.bubblecloud.biz.agi.mapper.McpServersMapper;
import com.bubblecloud.biz.agi.service.McpServersService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * MCP服务表
 *
 * @author Rampart
 * @date 2026-02-12 18:18:39
 */
@Service
public class McpServersServiceImpl extends UpServiceImpl<McpServersMapper, McpServers> implements McpServersService {

	@Override
	public R create(McpServers req) {
		Long count = this.lambdaQuery().eq(McpServers::getName, req.getName()).count();
		if (count > 0) {
			return R.failed("服务名称已存在");
		}
		return super.create(req);
	}

	@Override
	public R update(McpServers req) {
		McpServers servers = baseMapper.selectById(req.getId());
		if (Objects.isNull(servers)) {
			return R.failed("服务不存在");
		}
		Long count = this.lambdaQuery().eq(McpServers::getName, req.getName()).ne(McpServers::getId, req.getId()).count();
		if (count > 0) {
			return R.failed("服务名称已存在");
		}
		return super.update(req);
	}
}