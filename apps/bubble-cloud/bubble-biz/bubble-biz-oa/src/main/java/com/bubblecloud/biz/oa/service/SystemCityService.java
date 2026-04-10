package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.oa.api.vo.common.CityTreeNodeVO;

/**
 * 省市区树（对齐 PHP SystemCityService::cityTree）。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
public interface SystemCityService {

	/**
	 * 省市区三级树，节点字段 value/label/pid/children。
	 */
	List<CityTreeNodeVO> cityTree();

}
