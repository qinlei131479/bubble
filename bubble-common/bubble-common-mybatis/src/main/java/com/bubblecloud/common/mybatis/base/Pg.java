package com.bubblecloud.common.mybatis.base;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 分页参数对象的扩展
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
@Schema(name = "分页参数DTO")
@Data
@EqualsAndHashCode(callSuper = false)
public class Pg<T> extends Page<T> {

	private static final long serialVersionUID = 7148080350889774725L;

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
