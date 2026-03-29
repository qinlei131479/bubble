package com.bubblecloud.oa.api.vo.assess;

import java.util.Collections;
import java.util.List;

import lombok.Data;

/**
 * GET /ent/assess/index 工作台考核列表占位。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Data
public class AssessWorkbenchIndexVO {

	private List<AssessWorkbenchItemVO> list = Collections.emptyList();

}
