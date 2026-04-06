package com.bubblecloud.biz.oa.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.RankJobMapper;
import com.bubblecloud.biz.oa.service.RankJobService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.JobSubordinateUpdateDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.RankJob;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseUserListItemVO;
import com.bubblecloud.oa.api.vo.hr.JobSubordinateDetailVO;
import com.bubblecloud.oa.api.vo.hr.JobSubordinateRowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 岗位服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class RankJobServiceImpl extends UpServiceImpl<RankJobMapper, RankJob> implements RankJobService {

	@Autowired
	private AdminMapper adminMapper;

	@Override
	public List<RankJob> listSelect(Long entid) {
		return baseMapper.selectList(Wrappers.lambdaQuery(RankJob.class)
			.eq(ObjectUtil.isNotNull(entid), RankJob::getEntid, entid)
			.eq(RankJob::getStatus, 1)
			.orderByAsc(RankJob::getId)
			.select(RankJob::getId, RankJob::getName, RankJob::getEntid, RankJob::getStatus));
	}

	@Override
	public SimplePageVO subordinatePage(Pg pg, Long entid, String name) {
		if (ObjectUtil.isNull(entid)) {
			return SimplePageVO.of((int) pg.getCurrent(), (int) pg.getSize(), 0, List.of());
		}
		Page<EnterpriseUserListItemVO> page = new Page<>(pg.getCurrent(), pg.getSize());
		Page<EnterpriseUserListItemVO> res = adminMapper.selectEntUserList(page, entid, StrUtil.nullToEmpty(name), 1);
		List<JobSubordinateRowVO> rows = res.getRecords().stream().map(this::toSubRow).collect(Collectors.toList());
		return SimplePageVO.of((int) res.getCurrent(), (int) res.getSize(), res.getTotal(), rows);
	}

	private JobSubordinateRowVO toSubRow(EnterpriseUserListItemVO s) {
		JobSubordinateRowVO v = new JobSubordinateRowVO();
		v.setId(s.getId());
		v.setName(s.getName());
		v.setJob(s.getJob());
		v.setPhone(s.getPhone());
		v.setJobName(s.getJobName());
		v.setFrameNames(s.getFrameNames());
		v.setOperate(Boolean.FALSE);
		return v;
	}

	@Override
	public JobSubordinateDetailVO subordinateDetail(Long userId) {
		Admin admin = adminMapper.selectById(userId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		Integer jobId = admin.getJob();
		if (ObjectUtil.isNull(jobId) || jobId == 0) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		RankJob job = getById(Long.valueOf(jobId));
		if (ObjectUtil.isNull(job)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		JobSubordinateDetailVO vo = new JobSubordinateDetailVO();
		vo.setId(admin.getId());
		vo.setName(admin.getName());
		vo.setDuty(StrUtil.nullToEmpty(job.getDuty()));
		return vo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateJobStatus(Long id, Integer status) {
		RankJob existing = getById(id);
		assertExists(existing);
		existing.setStatus(status);
		baseMapper.updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateSubordinate(Long userId, JobSubordinateUpdateDTO dto) {
		Admin admin = adminMapper.selectById(userId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		Integer jobId = admin.getJob();
		if (ObjectUtil.isNull(jobId) || jobId == 0) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		RankJob job = getById(Long.valueOf(jobId));
		assertExists(job);
		job.setDuty(StrUtil.nullToEmpty(dto == null ? null : dto.getDuty()));
		baseMapper.updateById(job);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(RankJob req) {
		req.setStatus(ObjectUtil.defaultIfNull(req.getStatus(), 1));
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(RankJob req) {
		return super.update(req);
	}

	private void assertExists(RankJob entity) {
		if (ObjectUtil.isNull(entity)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
	}

}
