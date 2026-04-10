package com.bubblecloud.biz.agi.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.ConversationStats;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对话统计 ORM映射层
 *
 * @author rampart
 * @date 2026-02-13 09:38:18
 */
@Mapper
public interface ConversationStatsMapper extends UpMapper<ConversationStats> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(ConversationStats req);
}