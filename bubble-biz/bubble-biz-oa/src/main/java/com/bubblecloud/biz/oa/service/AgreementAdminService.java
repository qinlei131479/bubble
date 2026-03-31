package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.oa.api.entity.Agreement;

/**
 * 用户协议（eb_agreement），对齐 PHP ent/system/treaty。
 *
 * @author qinlei
 * @date 2026/3/31
 */
public interface AgreementAdminService {

	/**
	 * 协议列表，支持按标题模糊、标识精确筛选。
	 * @param title 标题（可选）
	 * @param ident 标识（可选）
	 * @return 协议列表
	 */
	List<Agreement> list(String title, String ident);

	/**
	 * 按主键查询。
	 * @param id 主键
	 * @return 实体，可能为 null
	 */
	Agreement getById(int id);

	/**
	 * 按主键更新。
	 * @param row 含 id 的实体
	 */
	void updateById(Agreement row);

}
