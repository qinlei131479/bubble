package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.EmployeeTrainUpdateDTO;
import com.bubblecloud.oa.api.entity.EmployeeTrain;

/**
 * 员工培训.
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
public interface EmployeeTrainService extends UpService<EmployeeTrain> {

	EmployeeTrain getInfo(String type);

	/**
	 * 仅更新培训正文（对齐 PHP {@code postMore content}）。
	 */
	void updateTrainContent(String type, String content);

}
