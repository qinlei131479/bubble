package com.bubblecloud.biz.agi.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.Conversations;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对话 ORM映射层
 *
 * @author Rampart
 * @date 2026-02-13 09:36:42
 */
@Mapper
public interface ConversationsMapper extends UpMapper<Conversations> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(Conversations req);
}