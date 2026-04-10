package com.bubblecloud.oa.api.vo.perfect;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邀请完善记录列表 VO（对齐 PHP list 字段）。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "邀请完善记录列表")
public class UserPerfectIndexVO {

	@Schema(description = "记录列表")
	private List<UserPerfectListRowVO> list = Collections.emptyList();

	public static UserPerfectIndexVO of(List<UserPerfectListRowVO> list) {
		return new UserPerfectIndexVO(list == null ? Collections.emptyList() : list);
	}

}
