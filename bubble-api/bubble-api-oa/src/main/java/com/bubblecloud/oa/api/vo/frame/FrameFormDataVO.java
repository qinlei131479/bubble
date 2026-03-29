package com.bubblecloud.oa.api.vo.frame;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门新增/编辑表单数据（frameInfo + tree）。
 *
 * @author qinlei
 * @date 2026/3/29 下午4:00
 */
@Data
@Schema(description = "部门表单数据")
public class FrameFormDataVO {

	@Schema(description = "当前部门信息，新建时可为空对象")
	private FrameDetailVO frameInfo;

	@Schema(description = "可选上级部门树")
	private List<FrameSelectTreeNodeVO> tree;

}
