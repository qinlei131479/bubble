package com.bubblecloud.biz.agi.mapper;


import com.bubblecloud.agi.api.entity.AgentConfigs;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qinlei
 */
@Mapper
public interface AgentConfigsMapper extends UpMapper<AgentConfigs> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(AgentConfigs req);
}
