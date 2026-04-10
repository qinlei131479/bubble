package com.bubblecloud.biz.oa.service;

import java.util.List;

/**
 * 跟进记录与 {@code eb_system_attach} 关联（对齐 PHP {@code AttachService::saveRelation}）。
 *
 * @author qinlei
 * @date 2026/4/6 18:45
 */
public interface FollowAttachRelationService {

	/**
	 * @param entid 企业 ID，PHP 侧固定 1
	 * @param uid 上传用户 ID 字符串
	 * @param followId 跟进记录 ID
	 * @param attachIds 为 {@code null} 时不处理；为空列表时删除该跟进下原有附件关联并删文件
	 */
	void saveRelation(int entid, String uid, long followId, List<Integer> attachIds);

}
