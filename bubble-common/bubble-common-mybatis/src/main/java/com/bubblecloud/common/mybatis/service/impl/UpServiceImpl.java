package com.bubblecloud.common.mybatis.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubblecloud.common.core.entity.Req;
import com.bubblecloud.common.core.util.HuToolUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.common.mybatis.service.UpService;

import java.util.List;

/**
 * Mapper接口
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
public class UpServiceImpl<M extends UpMapper<T>, T extends Req> extends ServiceImpl<M, T> implements UpService<T> {

	/**
	 * 检查属性重复 -- 创建模式
	 *
	 * @param req
	 * @param fieldAndNames：字段名称对，使用冒号分割
	 * @return
	 */
	@Override
	public R checkFieldRepeatCreate(T req, String... fieldAndNames) {
		return checkFieldRepeat(req, true, null, fieldAndNames);
	}

	/**
	 * 检查属性重复 -- 创建模式
	 *
	 * @param req：请求参数
	 * @param fieldAndNames：字段名称对，使用冒号分割
	 * @return
	 */
	@Override
	public R checkFieldRepeatCreate(T req, List<String> eqNameList, String... fieldAndNames) {
		return checkFieldRepeat(req, true, eqNameList, fieldAndNames);
	}

	/**
	 * 检查属性重复 -- 修改模式
	 *
	 * @param req：请求参数
	 * @param fieldAndNames：字段名称对，使用冒号分割
	 * @return
	 */
	@Override
	public R checkFieldRepeatUpdate(T req, String... fieldAndNames) {
		return checkFieldRepeat(req, false, null, fieldAndNames);
	}

	/**
	 * 检查属性重复 -- 修改模式
	 *
	 * @param req：请求参数
	 * @param fieldAndNames：字段名称对，使用冒号分割
	 * @return
	 */
	@Override
	public R checkFieldRepeatUpdate(T req, List<String> eqNameList, String... fieldAndNames) {
		return checkFieldRepeat(req, false, eqNameList, fieldAndNames);
	}

	/**
	 * 检查字段重复（包含eq相等字段）
	 *
	 * @param req
	 * @param eqNameList：使用冒号分割
	 * @param fieldAndNames：字段名称对，使用冒号分割
	 * @return
	 */
	@Override
	public R checkFieldRepeat(T req, boolean isCreate, List<String> eqNameList, String... fieldAndNames) {
		for (String fieldAndName : fieldAndNames) {
			String[] fn = fieldAndName.split(":");
			String field = fn[0];
			String name = fn[1];
			QueryWrapper<T> queryWrapper = new QueryWrapper<>();
			Object object = HuToolUtil.getFieldValueIfExist(req, field);
			if (object != null && StrUtil.isNotBlank(object.toString())) {
				queryWrapper.eq(field, object);
				if (CollUtil.isNotEmpty(eqNameList)) {
					eqNameList.forEach(item -> {
						queryWrapper.eq(item, HuToolUtil.getFieldValueIfExist(req, item));
					});
				}
				Object id = null ;// req.calId();
				// 非创建情况下，增加id条件
				if (id != null && isCreate == false) {
					queryWrapper.ne("id", id);
				}
				long repeatCount = this.baseMapper.selectCount(queryWrapper);
				if (repeatCount > 0) {
//					throw R.failed(name).;
				}
			}
		}
		return null;
	}

	@Override
	public Page<T> findPg(Page page, T req) {
		return baseMapper.findPg(page, req);
	}

	@Override
	public List<T> findList(T req) {
		return baseMapper.findList(req);
	}

	@Override
	public List<T> findList(Page page, T req) {
		if (page != null) {
			// 数量不限制
			page.setSize(Integer.MAX_VALUE);
			// 不查询总数
			page.setSearchCount(false);
		}
		return baseMapper.findList(page, req);
	}

	@Override
	public R update(T req) {
		baseMapper.updateCustom(req);
		return R.ok();
	}

	@Override
	public R update(UpMapper mapper, Req req) {
		mapper.updateCustom(req);
		return  R.ok();
	}

	@Override
	public R create(T req) {
		return this.create(this.baseMapper, req);
	}

	@Override
	public R create(UpMapper mapper, Req req) {
		this.createVoid(mapper, req);
		return  R.ok();
	}

	/**
	 * 直接写入（不自动生成id）
	 * 
	 * @param req
	 */
	@Override
	public R insert(T req) {
		this.baseMapper.insert(req);
		return  R.ok();
	}

	@Override
	public void createVoid(UpMapper mapper, Req req) {
		mapper.insert(req);
	}

	@Override
	public R delete(T req) {
		return this.deleteById(null);
	}

	@Override
	public R deleteById(Long id) {
		baseMapper.deleteById(id);
		return R.ok();
	}
}
