package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.hr.HayGroupSaveDTO;
import com.bubblecloud.oa.api.entity.HayGroup;
import com.bubblecloud.oa.api.entity.HayGroupData;
import com.bubblecloud.oa.api.vo.SimplePageVO;

/**
 * 海氏评估组服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface HayGroupService extends UpService<HayGroup> {

	SimplePageVO pageHayGroup(Pg<HayGroup> pg, HayGroup query);

	void createHayGroup(HayGroupSaveDTO dto);

	void updateHayGroup(Long id, HayGroupSaveDTO dto);

	void removeHayGroup(Long id);

	List<HayGroupData> dataList(Long groupId);

	List<HayGroupData> historyList(Long groupId);

}
