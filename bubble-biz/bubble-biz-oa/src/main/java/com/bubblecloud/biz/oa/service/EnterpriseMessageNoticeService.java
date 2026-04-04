package com.bubblecloud.biz.oa.service;

import java.util.List;
import java.util.Map;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.EnterpriseMessageNotice;
import com.bubblecloud.oa.api.vo.common.CommonMessageVO;

/**
 * 公共消息列表（对齐 PHP NoticeRecordService::getMessageList）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
public interface EnterpriseMessageNoticeService extends UpService<EnterpriseMessageNotice> {

	/**
	 * @param isReadFilter 与 PHP {@code is_read} 一致：{@code null} 表示不按已读过滤；{@code 0}/{@code 1} 过滤未读/已读。
	 *                     {@code GET ent/common/message} 固定传 {@code 0}。
	 */
	CommonMessageVO getMessageList(Long adminId, String uid, Long entId, Integer page, Integer limit, String cateId,
			String title, Integer isReadFilter);

	void updateMessageRead(Long adminId, String uid, Long messageId, Integer isRead);

	/**
	 * 未读站内信按 {@code cate_id} 聚合条数（供消息分类角标）。
	 */
	List<Map<String, Object>> countUnreadByCate(long entId, long adminId, String uuid);

	/**
	 * 消息中心抽屉 {@code GET ent/company/message}：当前用户，{@code is_read} 空串不按已读过滤。
	 */
	CommonMessageVO getCompanyMessage(Long adminId, String uid, Long entId, Integer page, Integer limit, String cateId,
			String title, String isReadRaw);

	/**
	 * 企业消息「全部」{@code GET ent/company/message/list}：不按接收人过滤（对齐 PHP {@code NoticeRecordController::list}）。
	 */
	CommonMessageVO getCompanyMessageAll(Long entId, Integer page, Integer limit, String cateId, String title,
			String isReadRaw);

	/**
	 * 批量已读/未读（对齐 PHP {@code PUT ent/company/message/batch/{isRead}}）。
	 */
	void batchUpdateCompanyMessageRead(long adminId, String uid, long entId, int isRead, Long cateId, List<Long> ids);

	/**
	 * 批量删除（对齐 PHP {@code DELETE ent/company/message/batch}）。
	 */
	void batchDeleteCompanyMessages(long adminId, String uid, long entId, List<Long> ids);

}
