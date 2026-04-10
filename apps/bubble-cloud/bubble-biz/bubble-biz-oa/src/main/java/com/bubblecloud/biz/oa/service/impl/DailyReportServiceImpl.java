package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.EnterpriseUserDailyMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseUserDailyReplyMapper;
import com.bubblecloud.biz.oa.service.DailyReportService;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.DailyReplyDTO;
import com.bubblecloud.oa.api.dto.DailySaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.EnterpriseUserDaily;
import com.bubblecloud.oa.api.entity.EnterpriseUserDailyReply;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 日报核心：列表/写库/回复：统计类接口后续对齐 PHP。
 *
 * @author qinlei
 * @date 2026/4/6 12:30
 */
@Service
@RequiredArgsConstructor
public class DailyReportServiceImpl implements DailyReportService {

	private final EnterpriseUserDailyMapper dailyMapper;

	private final EnterpriseUserDailyReplyMapper replyMapper;

	private final ObjectMapper objectMapper;

	@Override
	public ListCountVO<EnterpriseUserDaily> list(Pg<EnterpriseUserDaily> pg, Integer types, String time,
			Long filterUserId, Long entid) {
		var w = Wrappers.lambdaQuery(EnterpriseUserDaily.class).eq(EnterpriseUserDaily::getEntid, entid);
		if (ObjectUtil.isNotNull(types) && types > 0) {
			w.eq(EnterpriseUserDaily::getTypes, types);
		}
		if (StrUtil.isNotBlank(time)) {
			w.likeRight(EnterpriseUserDaily::getCreatedAt, time);
		}
		if (ObjectUtil.isNotNull(filterUserId)) {
			w.eq(EnterpriseUserDaily::getUserId, filterUserId);
		}
		w.orderByDesc(EnterpriseUserDaily::getDailyId);
		Page<EnterpriseUserDaily> res = dailyMapper.selectPage(pg, w);
		return ListCountVO.of(res.getRecords(), res.getTotal());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(Long operatorAdminId, String operatorUid, DailySaveDTO dto) {
		EnterpriseUserDaily d = new EnterpriseUserDaily();
		d.setEntid(ObjectUtil.defaultIfNull(dto.getEntid(), 1L));
		d.setUid(StrUtil.blankToDefault(operatorUid, ""));
		d.setUserId(operatorAdminId);
		d.setFinish(jsonList(dto.getFinish()));
		d.setPlan(jsonList(dto.getPlan()));
		d.setMark(StrUtil.nullToEmpty(dto.getMark()));
		d.setStatus(ObjectUtil.defaultIfNull(dto.getStatus(), 1));
		d.setTypes(ObjectUtil.defaultIfNull(dto.getTypes(), 1));
		d.setCreatedAt(LocalDateTime.now());
		d.setUpdatedAt(LocalDateTime.now());
		dailyMapper.insert(d);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(Long operatorAdminId, Long id, DailySaveDTO dto) {
		EnterpriseUserDaily d = dailyMapper.selectById(id);
		if (ObjectUtil.isNull(d)) {
			throw new IllegalArgumentException("汇报信息不存在");
		}
		d.setFinish(jsonList(dto.getFinish()));
		d.setPlan(jsonList(dto.getPlan()));
		d.setMark(StrUtil.nullToEmpty(dto.getMark()));
		if (ObjectUtil.isNotNull(dto.getStatus())) {
			d.setStatus(dto.getStatus());
		}
		if (ObjectUtil.isNotNull(dto.getTypes())) {
			d.setTypes(dto.getTypes());
		}
		d.setUpdatedAt(LocalDateTime.now());
		dailyMapper.updateById(d);
	}

	@Override
	public JsonNode editDetail(Long id, Long entid) {
		EnterpriseUserDaily d = dailyMapper.selectById(id);
		if (ObjectUtil.isNull(d) || !Objects.equals(entid, d.getEntid())) {
			throw new IllegalArgumentException("汇报信息不存在");
		}
		ObjectNode n = objectMapper.valueToTree(d);
		List<EnterpriseUserDailyReply> rs = replyMapper.selectList(Wrappers.lambdaQuery(EnterpriseUserDailyReply.class)
			.eq(EnterpriseUserDailyReply::getDailyId, id)
			.isNull(EnterpriseUserDailyReply::getDeletedAt)
			.orderByAsc(EnterpriseUserDailyReply::getId));
		n.set("replys", objectMapper.valueToTree(rs));
		n.set("attachs", objectMapper.createArrayNode());
		n.set("members", objectMapper.createArrayNode());
		return n;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteById(Long id) {
		dailyMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void replySave(String replierUid, DailyReplyDTO dto) {
		if (ObjectUtil.isNull(dto.getDailyId()) || dto.getDailyId() <= 0) {
			throw new IllegalArgumentException("参数错误");
		}
		EnterpriseUserDailyReply r = new EnterpriseUserDailyReply();
		r.setDailyId(dto.getDailyId());
		r.setPid(ObjectUtil.defaultIfNull(dto.getPid(), 0L));
		r.setUid(StrUtil.blankToDefault(replierUid, ""));
		r.setContent(StrUtil.nullToEmpty(dto.getContent()));
		r.setCreatedAt(LocalDateTime.now());
		r.setUpdatedAt(LocalDateTime.now());
		replyMapper.insert(r);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void replyDelete(Long replyId, Long dailyId, String operatorUid) {
		EnterpriseUserDailyReply r = replyMapper.selectById(replyId);
		if (ObjectUtil.isNull(r) || !dailyId.equals(r.getDailyId()) || !StrUtil.equals(operatorUid, r.getUid())) {
			throw new IllegalArgumentException("删除失败");
		}
		replyMapper.deleteById(replyId);
	}

	@Override
	public JsonNode reportMembers(Long adminId) {
		return objectMapper.createArrayNode();
	}

	private String jsonList(List<Object> list) {
		if (list == null) {
			return "";
		}
		try {
			return objectMapper.writeValueAsString(list);
		}
		catch (JsonProcessingException e) {
			return "[]";
		}
	}

}
