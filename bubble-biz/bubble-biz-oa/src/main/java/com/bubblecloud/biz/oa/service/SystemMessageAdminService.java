package com.bubblecloud.biz.oa.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Message;

/**
 * 系统消息配置（eb_message），对齐 PHP ent/system/message。
 *
 * @author qinlei
 * @date 2026/3/31
 */
public interface SystemMessageAdminService extends UpService<Message> {

	/**
	 * 分页列表。
	 * @param cateId 分类 ID，可选
	 * @param title 标题模糊，可选
	 * @param entid 企业 ID
	 * @param page 页码（PHP 约定）
	 * @param limit 每页条数
	 * @return 分页结果
	 */
	Page<Message> pageList(Integer cateId, String title, int entid, int page, int limit);

	/**
	 * 按主键查询。
	 * @param id 主键
	 * @return 实体
	 */
	Message getById(long id);

}
