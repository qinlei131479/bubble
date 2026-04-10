package com.bubblecloud.biz.oa.constant.form;

import java.util.Set;

/**
 * 将 PHP 业务 customType 映射为 eb_form_cate.types（1 客户 2 合同 3 联系人）。
 *
 * @author qinlei
 * @date 2026/3/30
 */
public final class SalesmanCustomTypeResolver {

	private static final Set<Integer> CUSTOMER = Set.of(1, 2, 3, 4);

	private static final Set<Integer> CONTRACT = Set.of(5, 6, 115, 125, 135);

	private static final Set<Integer> LIAISON = Set.of(7, 117, 127, 137, 147);

	private SalesmanCustomTypeResolver() {
	}

	/**
	 * @return 表单 types，无法识别时返回 0
	 */
	public static int toFormTypes(int customType) {
		if (CUSTOMER.contains(customType)) {
			return 1;
		}
		if (CONTRACT.contains(customType)) {
			return 2;
		}
		if (LIAISON.contains(customType)) {
			return 3;
		}
		return 0;
	}

}
