package com.bubblecloud.common.mybatis.base;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.core.enums.ActionStatusEnum;
import com.bubblecloud.common.core.enums.ListTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页参数对象的扩展
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
@Schema(name = "分页参数Dto")
@Data
@EqualsAndHashCode(callSuper = false)
public class Pg<T> extends Page<T> {
	private static final long serialVersionUID = 7148080350889774725L;

	/**
	 * 查询类型，见枚举 ListTypeEnum
	 */
	@Schema(description = "查询类型，不传默认分页查询，one表示单条查询")
	protected String listType;

	public boolean checkListTypeOne() {
		return ListTypeEnum.ONE.getCode().equals(this.listType);
	}

	public boolean checkListTypeArray() {
		return ListTypeEnum.ARRAY.getCode().equals(this.listType);
	}

	public boolean checkListTypePage() {
		return StrUtil.isBlank(listType) || ListTypeEnum.PAGE.getCode().equals(this.listType);
	}

	public boolean checkListType(String listType) {
		return StrUtil.isNotBlank(listType) && listType.equals(this.listType);
	}

	/**
	 * 操作状态，见枚举:ActionStatusEnum
	 */
	@Schema(description = "默认空，初始化值:init")
	protected String actionStatus;

	/**
	 * 判断操作状态，action状态
	 *
	 * @param actionStatusEnum
	 * @return
	 */
	public boolean checkActionStatus(ActionStatusEnum actionStatusEnum) {
		return (actionStatusEnum.getCode().equals(this.actionStatus));
	}

	public boolean checkActionStatusInit() {
		return checkActionStatus(ActionStatusEnum.INIT);
	}

	public boolean checkActionStatusCheck() {
		return checkActionStatus(ActionStatusEnum.CHECK);
	}

	/**
	 * 当排序为空的时候，增加默认排序
	 *
	 * @param item
	 */
	public void addOrderDefault(OrderItem... item) {
		if (CollUtil.isEmpty(this.orders())) {
			this.addOrder(item);
		}
	}
}
