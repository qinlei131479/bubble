package com.pig4cloud.pig.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.admin.api.entity.SysDict;
import com.pig4cloud.pig.common.core.util.R;

/**
 * 字典表
 *
 * @author lengleng
 * @date 2019/03/19
 */
public interface SysDictService extends IService<SysDict> {

	/**
	 * 根据ID 删除字典
	 * @param ids ID列表
	 * @return
	 */
	R removeDictByIds(Long[] ids);

	/**
	 * 更新字典
	 * @param sysDict 字典
	 * @return
	 */
	R updateDict(SysDict sysDict);

	/**
	 * 同步缓存 （清空缓存）
	 * @return R
	 */
	R syncDictCache();

}
