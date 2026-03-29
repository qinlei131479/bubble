package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工培训内容，对应 eb_employee_train 表。
 *
 * @author qinlei
 * @date 2026/3/29 下午6:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "员工培训")
@TableName("eb_employee_train")
public class EmployeeTrain extends Req<EmployeeTrain> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "培训类型")
	private String type;

	@Schema(description = "内容 HTML")
	private String content;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
