package com.bubblecloud.biz.oa.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.AssessScoreService;
import com.bubblecloud.biz.oa.service.OaAssessConfigService;
import com.bubblecloud.biz.oa.service.SystemConfigService;
import com.bubblecloud.oa.api.dto.hr.AssessScoreConfigSaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.AssessScore;
import com.bubblecloud.oa.api.vo.hr.AdminBriefVO;
import com.bubblecloud.oa.api.vo.hr.AssessScoreConfigVO;
import com.bubblecloud.oa.api.vo.hr.AssessVerifyConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 绩效系统配置实现。
 *
 * @author qinlei
 * @date 2026/4/7 13:00
 */
@Service
@RequiredArgsConstructor
public class OaAssessConfigServiceImpl implements OaAssessConfigService {

	private static final String KEY_SCORE_MARK = "assess_score_mark";

	private static final String KEY_COMPUTE_MODE = "assess_compute_mode";

	private static final String KEY_VERIFY_SUPERIOR = "assess_verify_superior";

	private static final String KEY_VERIFY_APPOINT = "assess_verify_appoint";

	private static final String KEY_VERIFY_STAFF = "assess_verify_staff";

	private final SystemConfigService systemConfigService;

	private final AssessScoreService assessScoreService;

	private final AdminService adminService;

	@Override
	public AssessScoreConfigVO getScoreConfig(long entid) {
		AssessScoreConfigVO vo = new AssessScoreConfigVO();
		vo.setScoreMark(systemConfigService.getConfigRawValue(KEY_SCORE_MARK));
		String mode = systemConfigService.getConfigRawValue(KEY_COMPUTE_MODE);
		vo.setComputeMode(StrUtil.isBlank(mode) ? 1 : Integer.parseInt(mode.trim()));
		vo.setScoreList(assessScoreService.listByEntidOrderByLevel(entid));
		return vo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveScoreConfig(long entid, AssessScoreConfigSaveDTO dto) {
		if (ObjectUtil.isNull(dto)) {
			return;
		}
		systemConfigService.upsertConfigValue(KEY_SCORE_MARK, StrUtil.nullToEmpty(dto.getScoreMark()));
		systemConfigService.upsertConfigValue(KEY_COMPUTE_MODE,
				String.valueOf(ObjectUtil.defaultIfNull(dto.getComputeMode(), 1)));
		if (CollUtil.isEmpty(dto.getScoreList())) {
			assessScoreService.remove(Wrappers.lambdaQuery(AssessScore.class).eq(AssessScore::getEntid, entid));
			return;
		}
		List<AssessScore> sorted = dto.getScoreList()
			.stream()
			.sorted(Comparator.comparingInt(s -> ObjectUtil.defaultIfNull(s.getMax(), 0)))
			.collect(Collectors.toList());
		for (int k = 0; k < sorted.size(); k++) {
			AssessScore v = sorted.get(k);
			if (k > 0) {
				AssessScore prev = sorted.get(k - 1);
				if (ObjectUtil.defaultIfNull(v.getMax(), 0) < ObjectUtil.defaultIfNull(prev.getMax(), 0)
						|| ObjectUtil.defaultIfNull(v.getMin(), 0) < ObjectUtil.defaultIfNull(prev.getMin(), 0)
						|| ObjectUtil.defaultIfNull(v.getMin(), 0) < ObjectUtil.defaultIfNull(prev.getMax(), 0)) {
					throw new IllegalArgumentException("保存失败，等级分数存在冲突！");
				}
			}
			if (ObjectUtil.defaultIfNull(v.getMax(), 0) <= ObjectUtil.defaultIfNull(v.getMin(), 0)) {
				throw new IllegalArgumentException("保存失败，等级分数区间错误！");
			}
			v.setLevel(k + 1);
			v.setEntid(entid);
		}
		assessScoreService.replaceAllByEntid(entid, sorted);
	}

	@Override
	public AssessVerifyConfigVO getVerifyConfig() {
		AssessVerifyConfigVO vo = new AssessVerifyConfigVO();
		vo.setIsSuperior(StrUtil.blankToDefault(systemConfigService.getConfigRawValue(KEY_VERIFY_SUPERIOR), ""));
		vo.setIsAppoint(StrUtil.blankToDefault(systemConfigService.getConfigRawValue(KEY_VERIFY_APPOINT), ""));
		String raw = systemConfigService.getConfigRawValue(KEY_VERIFY_STAFF);
		List<AdminBriefVO> staff = new ArrayList<>();
		if (StrUtil.isNotBlank(raw)) {
			for (String s : StrUtil.splitTrim(raw, ',')) {
				if (!NumberUtil.isLong(s)) {
					continue;
				}
				Admin a = adminService.getById(Long.parseLong(s));
				if (ObjectUtil.isNotNull(a)) {
					AdminBriefVO b = new AdminBriefVO();
					b.setId(a.getId());
					b.setName(a.getName());
					b.setAvatar(a.getAvatar());
					staff.add(b);
				}
			}
		}
		vo.setStaff(staff);
		return vo;
	}

}
