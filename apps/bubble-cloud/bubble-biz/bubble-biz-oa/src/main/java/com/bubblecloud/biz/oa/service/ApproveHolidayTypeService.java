package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.ApproveHolidayType;
import com.bubblecloud.oa.api.vo.approve.HolidayTypeSelectItemVO;

/**
 * 审批假期类型（对齐 PHP ApproveHolidayTypeService）。
 *
 * @author qinlei
 * @date 2026/4/2 15:00
 */
public interface ApproveHolidayTypeService extends UpService<ApproveHolidayType> {

	/**
	 * 下拉列表（按新员工限制与入职时间过滤）。
	 * @param adminId 当前员工 id，可为 null（无入职时间则按无限制处理）
	 */
	List<HolidayTypeSelectItemVO> getSelectList(Long adminId);

}
