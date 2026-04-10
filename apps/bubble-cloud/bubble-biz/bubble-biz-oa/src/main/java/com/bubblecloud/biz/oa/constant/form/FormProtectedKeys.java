package com.bubblecloud.biz.oa.constant.form;

import java.util.Set;
import cn.hutool.core.util.ObjectUtil;

/**
 * 与 PHP CustomEnum 对齐的不可删除字段 key 集合（types：1 客户 2 合同 3 联系人）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public final class FormProtectedKeys {

	private static final Set<String> CUSTOMER = Set.of("customer_way", "customer_name", "customer_label",
			"customer_followed", "area_cascade", "customer_status", "b37a3f16");

	private static final Set<String> CONTRACT = Set.of("contract_status", "contract_category", "contract_price",
			"start_date", "end_date", "contract_followed", "contract_customer", "signing_status", "contract_name");

	private static final Set<String> LIAISON = Set.of("liaison_name", "liaison_tel");

	private FormProtectedKeys() {
	}

	public static boolean isProtected(int types, String key) {
		if (ObjectUtil.isNull(key)) {
			return false;
		}
		return switch (types) {
			case 1 -> CUSTOMER.contains(key);
			case 2 -> CONTRACT.contains(key);
			case 3 -> LIAISON.contains(key);
			default -> false;
		};
	}

}
