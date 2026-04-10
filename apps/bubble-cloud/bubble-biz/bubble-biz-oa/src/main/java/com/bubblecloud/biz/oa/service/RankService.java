package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Rank;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 职级服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface RankService extends UpService<Rank> {

	/**
	 * 创建职级表单数据（对齐 PHP {@code PositionService::resourceCreate}：岗位树 + 空 jobInfo）。
	 */
	JsonNode buildCreateFormPayload(Long entid);

}
