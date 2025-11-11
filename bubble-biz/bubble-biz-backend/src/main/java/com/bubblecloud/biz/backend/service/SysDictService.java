package com.bubblecloud.biz.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bubblecloud.backend.api.entity.SysDict;
import com.bubblecloud.common.core.util.R;

/**
 * 字典表服务接口 提供字典数据的增删改查及缓存同步功能
 *
 * @author lengleng
 * @date 2025/05/30
 */
public interface SysDictService extends IService<SysDict> {

	/**
	 * 根据ID列表删除字典
	 * @param ids 要删除的字典ID数组
	 * @return 操作结果
	 */
	R removeDictByIds(Long[] ids);

	/**
	 * 更新字典
	 * @param sysDict 要更新的字典对象
	 * @return 操作结果
	 */
	R updateDict(SysDict sysDict);

	/**
	 * 同步字典缓存（清空缓存）
	 * @return 操作结果
	 */
	R syncDictCache();

}
