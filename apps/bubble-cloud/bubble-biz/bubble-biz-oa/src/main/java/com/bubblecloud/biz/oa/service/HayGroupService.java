package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.HayGroup;
import com.bubblecloud.oa.api.entity.HayGroupData;

/**
 * 海氏评估组服务。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
public interface HayGroupService extends UpService<HayGroup> {

	List<HayGroupData> dataList(Long groupId);

	List<HayGroupData> historyList(Long groupId);

}
