package com.bubblecloud.backend.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubblecloud.backend.api.entity.SysDict;
import com.bubblecloud.backend.api.entity.SysDictItem;
import com.bubblecloud.backend.mapper.SysDictItemMapper;
import com.bubblecloud.backend.mapper.SysDictMapper;
import com.bubblecloud.backend.service.SysDictService;
import com.bubblecloud.common.core.constant.CacheConstants;
import com.bubblecloud.common.core.constant.enums.DictTypeEnum;
import com.bubblecloud.common.core.exception.ErrorCodes;
import com.bubblecloud.common.core.util.MsgUtils;
import com.bubblecloud.common.core.util.R;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

/**
 * 系统字典服务实现类
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Service
@AllArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

	private final SysDictItemMapper dictItemMapper;

	/**
	 * 根据ID删除字典
	 * @param ids 字典ID数组
	 * @return 操作结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R removeDictByIds(Long[] ids) {

		List<Long> dictIdList = baseMapper.selectByIds(CollUtil.toList(ids))
			.stream()
			.filter(sysDict -> !sysDict.getSystemFlag().equals(DictTypeEnum.SYSTEM.getType()))// 系统内置类型不删除
			.map(SysDict::getId)
			.toList();

		baseMapper.deleteByIds(dictIdList);

		dictItemMapper.delete(Wrappers.<SysDictItem>lambdaQuery().in(SysDictItem::getDictId, dictIdList));
		return R.ok();
	}

	/**
	 * 更新字典数据
	 * @param dict 字典对象
	 * @return 操作结果
	 * @see R 返回结果封装类
	 */
	@Override
	@CacheEvict(value = CacheConstants.DICT_DETAILS, key = "#dict.dictType")
	public R updateDict(SysDict dict) {
		SysDict sysDict = this.getById(dict.getId());
		// 系统内置
		if (DictTypeEnum.SYSTEM.getType().equals(sysDict.getSystemFlag())) {
			return R.failed(MsgUtils.getMessage(ErrorCodes.SYS_DICT_UPDATE_SYSTEM));
		}
		this.updateById(dict);
		return R.ok(dict);
	}

	/**
	 * 同步字典缓存（清空缓存）
	 * @return 操作结果
	 */
	@Override
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R syncDictCache() {
		return R.ok();
	}

}
