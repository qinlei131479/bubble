package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.oa.api.entity.SystemQuick;

/**
 * 快捷入口（eb_system_quick）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public interface QuickAdminService {

	Page<SystemQuick> page(Integer cid, String nameLike, int current, int size);

	SystemQuick get(int id);

	void save(SystemQuick row);

	void update(SystemQuick row);

	void delete(int id);

	List<SystemQuick> listAll(Integer cid);

}
