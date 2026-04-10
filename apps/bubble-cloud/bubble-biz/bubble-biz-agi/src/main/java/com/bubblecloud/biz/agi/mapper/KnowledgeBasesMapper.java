package com.bubblecloud.biz.agi.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.KnowledgeBases;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库 ORM映射层
 *
 * @author Rampart
 * @date 2026-02-13 09:30:12
 */
@Mapper
public interface KnowledgeBasesMapper extends UpMapper<KnowledgeBases> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(KnowledgeBases req);
}