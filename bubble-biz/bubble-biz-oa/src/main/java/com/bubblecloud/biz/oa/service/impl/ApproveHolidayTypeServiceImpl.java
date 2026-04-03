package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.AdminInfoMapper;
import com.bubblecloud.biz.oa.mapper.ApproveHolidayTypeMapper;
import com.bubblecloud.biz.oa.service.ApproveHolidayTypeService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.AdminInfo;
import com.bubblecloud.oa.api.entity.ApproveHolidayType;
import com.bubblecloud.oa.api.vo.approve.HolidayTypeSelectItemVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 审批假期类型实现。
 *
 * @author qinlei
 * @date 2026/4/2 15:00
 */
@Service
public class ApproveHolidayTypeServiceImpl extends UpServiceImpl<ApproveHolidayTypeMapper, ApproveHolidayType>
		implements ApproveHolidayTypeService {

	private static final ZoneId TZ = ZoneId.of("Asia/Shanghai");

	private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

	private final AdminInfoMapper adminInfoMapper;

	public ApproveHolidayTypeServiceImpl(AdminInfoMapper adminInfoMapper) {
		this.adminInfoMapper = adminInfoMapper;
	}

	private void checkNameUnique(String name, Long excludeId) {
		if (StrUtil.isBlank(name)) {
			throw new IllegalArgumentException("类型名称不能为空");
		}
		long cnt = count(Wrappers.lambdaQuery(ApproveHolidayType.class)
			.eq(ApproveHolidayType::getName, name)
			.ne(ObjectUtil.isNotNull(excludeId), ApproveHolidayType::getId, excludeId));
		if (cnt > 0) {
			throw new IllegalArgumentException("类型名称重复");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(ApproveHolidayType dto) {
		checkNameUnique(dto.getName(), null);
		dto.setNewEmployeeLimit(ObjectUtil.defaultIfNull(dto.getNewEmployeeLimit(), 0));
		dto.setNewEmployeeLimitMonth(ObjectUtil.defaultIfNull(dto.getNewEmployeeLimitMonth(), 1));
		dto.setDurationType(ObjectUtil.defaultIfNull(dto.getDurationType(), 0));
		dto.setDurationCalcType(ObjectUtil.defaultIfNull(dto.getDurationCalcType(), 1));
		dto.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
		LocalDateTime now = LocalDateTime.now();
		dto.setCreatedAt(now);
		dto.setUpdatedAt(now);
		return super.create(dto);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(ApproveHolidayType dto) {
		checkNameUnique(dto.getName(), dto.getId());
		ApproveHolidayType existing = baseMapper.selectById(dto.getId());
		if (ObjectUtil.isNull(existing)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		existing.setName(dto.getName());
		existing.setNewEmployeeLimit(ObjectUtil.defaultIfNull(dto.getNewEmployeeLimit(), 0));
		existing.setNewEmployeeLimitMonth(ObjectUtil.defaultIfNull(dto.getNewEmployeeLimitMonth(), 1));
		existing.setDurationType(ObjectUtil.defaultIfNull(dto.getDurationType(), 0));
		existing.setDurationCalcType(ObjectUtil.defaultIfNull(dto.getDurationCalcType(), 1));
		existing.setSort(ObjectUtil.defaultIfNull(dto.getSort(), 0));
		existing.setUpdatedAt(LocalDateTime.now());
		return super.update(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R deleteById(Long id) {
		if (ObjectUtil.isNull(getById(id))) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		return super.deleteById(id);
	}

	@Override
	public List<HolidayTypeSelectItemVO> getSelectList(Long adminId) {
		String workTime = "";
		if (ObjectUtil.isNotNull(adminId)) {
			AdminInfo info = adminInfoMapper.selectById(adminId);
			if (ObjectUtil.isNotNull(info) && StrUtil.isNotBlank(info.getWorkTime())) {
				workTime = info.getWorkTime();
			}
		}
		long monthNumber = Long.MAX_VALUE;
		if (StrUtil.isNotBlank(workTime)) {
			try {
				String s = workTime.length() >= 10 ? workTime.substring(0, 10) : workTime;
				LocalDate wd = LocalDate.parse(s, DAY_FMT);
				monthNumber = ChronoUnit.MONTHS.between(YearMonth.from(wd), YearMonth.now(TZ));
			}
			catch (Exception ignored) {
				monthNumber = 0;
			}
		}
		List<ApproveHolidayType> all = list(Wrappers.lambdaQuery(ApproveHolidayType.class)
			.orderByAsc(ApproveHolidayType::getSort)
			.orderByAsc(ApproveHolidayType::getId));
		List<HolidayTypeSelectItemVO> out = new ArrayList<>();
		for (ApproveHolidayType item : all) {
			if (ObjectUtil.defaultIfNull(item.getNewEmployeeLimit(), 0) != 0 && (StrUtil.isBlank(workTime)
					|| ObjectUtil.defaultIfNull(item.getNewEmployeeLimitMonth(), 1) > monthNumber)) {
				continue;
			}
			out.add(new HolidayTypeSelectItemVO(item.getId().intValue(), item.getName(), item.getDurationType()));
		}
		return out;
	}

}
