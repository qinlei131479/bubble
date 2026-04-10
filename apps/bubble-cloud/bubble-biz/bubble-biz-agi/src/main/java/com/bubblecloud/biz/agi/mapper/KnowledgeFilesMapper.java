package com.bubblecloud.biz.agi.mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.KnowledgeFiles;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库文件 ORM映射层
 *
 * @author Rampart
 * @date 2026-02-13 09:33:20
 */
@Mapper
public interface KnowledgeFilesMapper extends UpMapper<KnowledgeFiles> {

	/**
	 * 自定义更新，需要自行实现xml SQL
	 *
	 * @param req
	 * @return
	 */
	int updateCustom(KnowledgeFiles req);
}