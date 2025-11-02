package com.bubblecloud.backed.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubblecloud.backed.api.entity.SysDict;
import com.bubblecloud.backed.api.entity.SysDictItem;
import com.bubblecloud.backed.mapper.SysDictItemMapper;
import com.bubblecloud.backed.service.SysDictItemService;
import com.bubblecloud.backed.service.SysDictService;
import com.bubblecloud.common.core.constant.CacheConstants;
import com.bubblecloud.common.core.constant.enums.DictTypeEnum;
import com.bubblecloud.common.core.exception.ErrorCodes;
import com.bubblecloud.common.core.util.MsgUtils;
import com.bubblecloud.common.core.util.R;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 字典项
 *
 * @author lengleng
 * @date 2019/03/19
 */
@Service
@AllArgsConstructor
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements SysDictItemService {

	private final SysDictService dictService;

	/**
	 * 删除字典项
	 * @param id 字典项ID
	 * @return
	 */
	@Override
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R removeDictItem(Long id) {
		// 根据ID查询字典ID
		SysDictItem dictItem = this.getById(id);
		SysDict dict = dictService.getById(dictItem.getDictId());
		// 系统内置
		if (DictTypeEnum.SYSTEM.getType().equals(dict.getSystemFlag())) {
			return R.failed(MsgUtils.getMessage(ErrorCodes.SYS_DICT_DELETE_SYSTEM));
		}
		return R.ok(this.removeById(id));
	}

	/**
	 * 更新字典项
	 * @param item 字典项
	 * @return
	 */
	@Override
	@CacheEvict(value = CacheConstants.DICT_DETAILS, key = "#item.dictType")
	public R updateDictItem(SysDictItem item) {
		// 查询字典
		SysDict dict = dictService.getById(item.getDictId());
		// 系统内置
		if (DictTypeEnum.SYSTEM.getType().equals(dict.getSystemFlag())) {
			return R.failed(MsgUtils.getMessage(ErrorCodes.SYS_DICT_UPDATE_SYSTEM));
		}
		return R.ok(this.updateById(item));
	}

}
