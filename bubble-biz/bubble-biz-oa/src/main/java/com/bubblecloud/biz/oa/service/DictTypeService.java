package com.bubblecloud.biz.oa.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.DictType;

/**
 * 字典类型服务。
 *
 * @author qinlei
 * @date 2026/3/29 下午12:00
 */
public interface DictTypeService extends UpService<DictType> {

	/**
	 * 分页列表（条件在 Service 内装配，供 Controller 调用）。
	 */
	Page<DictType> pageDictTypes(long current, long size, String name, String linkType, Integer status);

}
