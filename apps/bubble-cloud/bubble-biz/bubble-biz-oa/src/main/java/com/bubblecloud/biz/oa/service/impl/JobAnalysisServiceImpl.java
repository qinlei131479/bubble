package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.FrameAssistMapper;
import com.bubblecloud.biz.oa.mapper.FrameMapper;
import com.bubblecloud.biz.oa.mapper.RankJobMapper;
import com.bubblecloud.biz.oa.service.EnterpriseUserJobAnalysisService;
import com.bubblecloud.biz.oa.service.JobAnalysisService;
import com.bubblecloud.oa.api.dto.frame.FrameAssistCardBatchRow;
import com.bubblecloud.oa.api.dto.jobanalysis.JobAnalysisListSqlParam;
import com.bubblecloud.oa.api.dto.jobanalysis.JobAnalysisUpdateDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.EnterpriseUserJobAnalysis;
import com.bubblecloud.oa.api.entity.FrameAssist;
import com.bubblecloud.oa.api.entity.RankJob;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.jobanalysis.JobAnalysisCardVO;
import com.bubblecloud.oa.api.vo.jobanalysis.JobAnalysisDetailVO;
import com.bubblecloud.oa.api.vo.jobanalysis.JobAnalysisListItemVO;
import com.bubblecloud.oa.api.vo.jobanalysis.JobAnalysisMineVO;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 工作分析业务实现。
 *
 * @author qinlei
 * @date 2026/4/5 15:30
 */
@Service
@RequiredArgsConstructor
public class JobAnalysisServiceImpl implements JobAnalysisService {

	private final AdminMapper adminMapper;

	private final FrameMapper frameMapper;

	private final FrameAssistMapper frameAssistMapper;

	private final RankJobMapper rankJobMapper;

	private final EnterpriseUserJobAnalysisService enterpriseUserJobAnalysisService;

	@Override
	public ListCountVO<JobAnalysisListItemVO> list(long entid, long currentAdminId, Integer frameId, String name,
			List<Integer> types, Integer jobId, int page, int limit) {
		List<Integer> typeList = CollUtil.isEmpty(types) ? List.of(1, 2, 3) : types;
		JobAnalysisListSqlParam p = new JobAnalysisListSqlParam();
		p.setEntid(entid);
		p.setName(StrUtil.trimToNull(name));
		p.setTypes(typeList);
		p.setJobId(jobId);
		int pg = Math.max(page, 1);
		int sz = limit > 0 ? limit : 15;
		p.setLimit(sz);
		p.setOffset((long) (pg - 1) * sz);
		if (ObjectUtil.isNotNull(frameId) && frameId > 0) {
			p.setFilterByFrame(true);
			p.setFrameId(frameId);
			List<Integer> sub = frameMapper.selectSubtreeFrameIds(entid, frameId);
			p.setSubtreeFrameIds(sub == null ? List.of() : sub);
			p.setFilterByScopeIds(false);
		}
		else {
			p.setFilterByFrame(false);
			p.setFilterByScopeIds(true);
			List<Long> scope = directSubordinateAdminIds(entid, currentAdminId);
			p.setScopeAdminIds(scope);
		}
		long total = adminMapper.countJobAnalysisAdmins(p);
		if (total == 0) {
			return ListCountVO.of(Collections.emptyList(), 0L);
		}
		List<Long> ids = adminMapper.selectJobAnalysisAdminIds(p);
		if (CollUtil.isEmpty(ids)) {
			return ListCountVO.of(Collections.emptyList(), total);
		}
		List<Admin> admins = adminMapper.selectList(Wrappers.lambdaQuery(Admin.class).in(Admin::getId, ids));
		Map<Long, Admin> adminById = admins.stream().collect(Collectors.toMap(Admin::getId, a -> a, (a, b) -> a));
		List<FrameAssistCardBatchRow> frameRows = frameAssistMapper.selectFramesForCardBatch(entid, ids);
		Map<Long, List<FrameAssistCardBatchRow>> framesByUser = frameRows.stream()
			.filter(r -> ObjectUtil.isNotNull(r.getUserId()))
			.collect(Collectors.groupingBy(FrameAssistCardBatchRow::getUserId));
		List<JobAnalysisListItemVO> records = new ArrayList<>(ids.size());
		for (Long id : ids) {
			Admin a = adminById.get(id);
			if (ObjectUtil.isNull(a)) {
				continue;
			}
			JobAnalysisListItemVO row = new JobAnalysisListItemVO();
			row.setId(a.getId());
			row.setName(a.getName());
			row.setUpdatedAt(a.getUpdatedAt());
			row.setOperate(Boolean.TRUE);
			if (ObjectUtil.isNotNull(a.getJob()) && a.getJob() > 0) {
				RankJob job = rankJobMapper.selectOne(Wrappers.lambdaQuery(RankJob.class)
					.eq(RankJob::getId, a.getJob().longValue())
					.eq(RankJob::getEntid, entid));
				if (ObjectUtil.isNotNull(job)) {
					JobAnalysisListItemVO.JobRefVO j = new JobAnalysisListItemVO.JobRefVO();
					j.setId(a.getJob());
					j.setName(job.getName());
					row.setJob(j);
				}
			}
			List<FrameAssistCardBatchRow> frs = framesByUser.getOrDefault(id, List.of());
			List<JobAnalysisListItemVO.FrameRowVO> frameList = new ArrayList<>();
			for (FrameAssistCardBatchRow fr : frs) {
				JobAnalysisListItemVO.FrameRowVO fv = new JobAnalysisListItemVO.FrameRowVO();
				fv.setId(fr.getFrameId());
				fv.setName(fr.getFrameName());
				fv.setUserCount(fr.getUserCount());
				fv.setIsMastart(fr.getIsMastart());
				fv.setIsAdmin(fr.getIsAdmin());
				fv.setSuperiorUid(fr.getSuperiorUid());
				frameList.add(fv);
			}
			row.setFrames(frameList);
			frs.stream().filter(f -> ObjectUtil.equal(f.getIsMastart(), 1)).findFirst().ifPresent(fr -> {
				JobAnalysisListItemVO.FrameRefVO master = new JobAnalysisListItemVO.FrameRefVO();
				master.setId(fr.getFrameId());
				master.setName(fr.getFrameName());
				master.setUserCount(fr.getUserCount());
				master.setIsMastart(fr.getIsMastart());
				master.setIsAdmin(fr.getIsAdmin());
				master.setSuperiorUid(fr.getSuperiorUid());
				row.setFrame(master);
			});
			records.add(row);
		}
		return ListCountVO.of(records, total);
	}

	@Override
	public Object info(long entid, long targetAdminId) {
		Admin user = adminMapper.selectById(targetAdminId);
		if (ObjectUtil.isNull(user) || ObjectUtil.isNotNull(user.getDeletedAt())) {
			throw new IllegalArgumentException("用户信息不存在");
		}
		EnterpriseUserJobAnalysis ja = enterpriseUserJobAnalysisService
			.getOne(Wrappers.lambdaQuery(EnterpriseUserJobAnalysis.class)
				.eq(EnterpriseUserJobAnalysis::getEntid, entid)
				.eq(EnterpriseUserJobAnalysis::getUserId, targetAdminId));
		if (ObjectUtil.isNull(ja)) {
			return Collections.emptyList();
		}
		JobAnalysisDetailVO vo = new JobAnalysisDetailVO();
		vo.setUid(targetAdminId);
		vo.setData(HtmlUtils.htmlUnescape(StrUtil.nullToEmpty(ja.getData())));
		vo.setCreatedAt(ja.getCreatedAt());
		vo.setUpdatedAt(ja.getUpdatedAt());
		JobAnalysisCardVO card = new JobAnalysisCardVO();
		card.setId(user.getId());
		card.setName(user.getName());
		card.setAvatar(user.getAvatar());
		card.setUid(user.getUid());
		card.setPhone(user.getPhone());
		vo.setCard(card);
		return vo;
	}

	@Override
	public JobAnalysisMineVO mine(long entid, long currentAdminId) {
		EnterpriseUserJobAnalysis ja = enterpriseUserJobAnalysisService
			.getOne(Wrappers.lambdaQuery(EnterpriseUserJobAnalysis.class)
				.eq(EnterpriseUserJobAnalysis::getEntid, entid)
				.eq(EnterpriseUserJobAnalysis::getUserId, currentAdminId));
		if (ObjectUtil.isNull(ja)) {
			return null;
		}
		JobAnalysisMineVO vo = new JobAnalysisMineVO();
		vo.setUid(currentAdminId);
		vo.setData(HtmlUtils.htmlUnescape(StrUtil.nullToEmpty(ja.getData())));
		vo.setCreatedAt(ja.getCreatedAt());
		vo.setUpdatedAt(ja.getUpdatedAt());
		return vo;
	}

	@Override
	public void update(long entid, long targetAdminId, JobAnalysisUpdateDTO dto) {
		Admin user = adminMapper.selectById(targetAdminId);
		if (ObjectUtil.isNull(user) || ObjectUtil.isNotNull(user.getDeletedAt())) {
			throw new IllegalArgumentException("用户信息不存在");
		}
		enterpriseUserJobAnalysisService.upsertByEntAndUser(entid, targetAdminId, dto.getData());
	}

	private List<Long> directSubordinateAdminIds(long entid, long currentAdminId) {
		List<FrameAssist> manages = frameAssistMapper.selectList(Wrappers.lambdaQuery(FrameAssist.class)
			.eq(FrameAssist::getEntid, entid)
			.eq(FrameAssist::getUserId, currentAdminId)
			.eq(FrameAssist::getIsAdmin, 1)
			.isNull(FrameAssist::getDeletedAt));
		List<Integer> manageFrameIds = manages.stream()
			.map(FrameAssist::getFrameId)
			.filter(Objects::nonNull)
			.distinct()
			.toList();
		Set<Long> sub = new LinkedHashSet<>();
		if (CollUtil.isNotEmpty(manageFrameIds)) {
			List<Long> u1 = frameAssistMapper
				.selectList(Wrappers.lambdaQuery(FrameAssist.class)
					.eq(FrameAssist::getEntid, entid)
					.in(FrameAssist::getFrameId, manageFrameIds)
					.eq(FrameAssist::getIsMastart, 1)
					.eq(FrameAssist::getIsAdmin, 0)
					.isNull(FrameAssist::getDeletedAt))
				.stream()
				.map(FrameAssist::getUserId)
				.filter(Objects::nonNull)
				.toList();
			List<Long> notUid = frameAssistMapper
				.selectList(Wrappers.lambdaQuery(FrameAssist.class)
					.eq(FrameAssist::getEntid, entid)
					.in(FrameAssist::getFrameId, manageFrameIds)
					.eq(FrameAssist::getIsMastart, 1)
					.eq(FrameAssist::getIsAdmin, 1)
					.isNull(FrameAssist::getDeletedAt)
					.apply("superior_uid IS NOT NULL AND superior_uid <> {0}", currentAdminId))
				.stream()
				.map(FrameAssist::getUserId)
				.filter(Objects::nonNull)
				.toList();
			Set<Long> exclude = new LinkedHashSet<>(notUid);
			for (Long uid : u1) {
				if (!exclude.contains(uid)) {
					sub.add(uid);
				}
			}
		}
		List<Long> u2 = frameAssistMapper
			.selectList(Wrappers.lambdaQuery(FrameAssist.class)
				.eq(FrameAssist::getEntid, entid)
				.eq(FrameAssist::getSuperiorUid, currentAdminId)
				.isNull(FrameAssist::getDeletedAt))
			.stream()
			.map(FrameAssist::getUserId)
			.filter(Objects::nonNull)
			.toList();
		sub.addAll(u2);
		if (sub.isEmpty()) {
			return List.of();
		}
		return adminMapper
			.selectList(Wrappers.lambdaQuery(Admin.class)
				.in(Admin::getId, sub)
				.eq(Admin::getStatus, 1)
				.isNull(Admin::getDeletedAt))
			.stream()
			.map(Admin::getId)
			.toList();
	}

}
