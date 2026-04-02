package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.SystemQuick;

/**
 * 快捷入口（eb_system_quick）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface QuickAdminService extends UpService<SystemQuick> {

	Page<SystemQuick> page(Integer cid, String nameLike, Integer current, Integer size);

	SystemQuick getQuick(Integer id);

	void saveQuick(SystemQuick row);

	void updateQuick(SystemQuick row);

	void deleteQuick(Integer id);

	List<SystemQuick> listAll(Integer cid);

}
