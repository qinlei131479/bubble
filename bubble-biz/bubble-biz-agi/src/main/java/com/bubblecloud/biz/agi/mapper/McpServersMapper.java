package com.bubblecloud.biz.agi.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

}