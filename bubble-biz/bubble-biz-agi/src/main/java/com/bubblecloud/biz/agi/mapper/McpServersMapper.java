package com.bubblecloud.biz.agi.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.McpServers;
import org.apache.ibatis.annotations.Mapper;

/**
 * MCP服务表 ORM映射层
 *
 * @author Rampart
 * @date 2026-02-12 18:18:39
 */
@Mapper
public interface McpServersMapper extends UpMapper<McpServers> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(McpServers req);
}