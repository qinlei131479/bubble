package com.bubblecloud.biz.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bubblecloud.backend.api.entity.SysDictItem;
import com.bubblecloud.common.core.util.R;

/**
 * 字典项服务接口
 *
 * @author lengleng
 * @date 2025/05/30
 */
public interface SysDictItemService extends IService<SysDictItem> {

	/**
	 * 删除字典项
	 * @param id 字典项ID
	 * @return 操作结果
	 */
	R removeDictItem(Long id);

	/**
	 * 更新字典项
	 * @param item 需要更新的字典项
	 * @return 操作结果
	 */
	R updateDictItem(SysDictItem item);

}
