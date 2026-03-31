package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.EnterpriseMessageNoticeMapper;
import com.bubblecloud.biz.oa.service.MessageService;
import com.bubblecloud.oa.api.entity.EnterpriseMessageNotice;
import com.bubblecloud.oa.api.vo.common.CommonMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 企业消息列表。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

	private final EnterpriseMessageNoticeMapper enterpriseMessageNoticeMapper;

	@Override
	public CommonMessageVO getMessageList(long adminId, String uid, int entid, int page, int limit, String cateId,
			String title) {
		LambdaQueryWrapper<EnterpriseMessageNotice> q = buildQuery(adminId, uid, entid, cateId, title);
		long total = enterpriseMessageNoticeMapper.selectCount(q);
		q.orderByDesc(EnterpriseMessageNotice::getId);
		Page<EnterpriseMessageNotice> p = enterpriseMessageNoticeMapper.selectPage(new Page<>(page, limit), q);
		CommonMessageVO vo = new CommonMessageVO();
		vo.setList(p.getRecords());
		vo.setMessageNum((int) Math.min(total, Integer.MAX_VALUE));
		return vo;
	}

	private static LambdaQueryWrapper<EnterpriseMessageNotice> buildQuery(long adminId, String uid, int entid,
			String cateId, String title) {
		LambdaQueryWrapper<EnterpriseMessageNotice> q = Wrappers.lambdaQuery(EnterpriseMessageNotice.class);
		q.and(w -> w.eq(EnterpriseMessageNotice::getEntid, (long) entid)
			.or()
			.eq(EnterpriseMessageNotice::getEntid, 0L));
		String idStr = String.valueOf(adminId);
		q.and(w -> w.eq(EnterpriseMessageNotice::getToUid, uid).or().eq(EnterpriseMessageNotice::getToUid, idStr));
		if (StringUtils.hasText(cateId) && !"0".equals(cateId)) {
			try {
				q.eq(EnterpriseMessageNotice::getCateId, Integer.parseInt(cateId.trim()));
			}
			catch (NumberFormatException ignored) {
				// 忽略非法 cate_id
			}
		}
		if (StringUtils.hasText(title)) {
			String t = "%" + title.trim() + "%";
			q.and(w -> w.like(EnterpriseMessageNotice::getTitle, t).or().like(EnterpriseMessageNotice::getMessage, t));
		}
		return q;
	}

	@Override
	public void updateMessageRead(long adminId, String uid, long messageId, int isRead) {
		EnterpriseMessageNotice row = enterpriseMessageNoticeMapper.selectById(messageId);
		if (row == null) {
			throw new IllegalArgumentException("消息不存在");
		}
		String idStr = String.valueOf(adminId);
		boolean ok = idStr.equals(row.getToUid()) || (StringUtils.hasText(uid) && uid.equals(row.getToUid()));
		if (!ok) {
			throw new IllegalArgumentException("无权操作该消息");
		}
		row.setIsRead(isRead);
		enterpriseMessageNoticeMapper.updateById(row);
	}

}
