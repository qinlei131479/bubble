package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.EnterpriseMessageNoticeMapper;
import com.bubblecloud.biz.oa.service.MessageService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.EnterpriseMessageNotice;
import com.bubblecloud.oa.api.vo.common.CommonMessageVO;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 企业消息列表。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Service
public class MessageServiceImpl extends UpServiceImpl<EnterpriseMessageNoticeMapper, EnterpriseMessageNotice>
		implements MessageService {

	@Override
	public CommonMessageVO getMessageList(long adminId, String uid, int entid, int page, int limit, String cateId,
			String title) {
		LambdaQueryWrapper<EnterpriseMessageNotice> q = buildQuery(adminId, uid, entid, cateId, title);
		long total = this.count(q);
		q.orderByDesc(EnterpriseMessageNotice::getId);
		Page<EnterpriseMessageNotice> p = this.page(new Page<>(page, limit), q);
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
		if (StrUtil.isNotBlank(cateId) && !"0".equals(cateId)) {
			try {
				q.eq(EnterpriseMessageNotice::getCateId, Integer.parseInt(cateId.trim()));
			}
			catch (NumberFormatException ignored) {
				// 忽略非法 cate_id
			}
		}
		if (StrUtil.isNotBlank(title)) {
			String t = "%" + title.trim() + "%";
			q.and(w -> w.like(EnterpriseMessageNotice::getTitle, t).or().like(EnterpriseMessageNotice::getMessage, t));
		}
		return q;
	}

	@Override
	public void updateMessageRead(long adminId, String uid, long messageId, int isRead) {
		EnterpriseMessageNotice row = this.getById(messageId);
		if (ObjectUtil.isNull(row)) {
			throw new IllegalArgumentException("消息不存在");
		}
		String idStr = String.valueOf(adminId);
		boolean ok = idStr.equals(row.getToUid()) || (StrUtil.isNotBlank(uid) && uid.equals(row.getToUid()));
		if (!ok) {
			throw new IllegalArgumentException("无权操作该消息");
		}
		row.setIsRead(isRead);
		this.updateById(row);
	}

}
