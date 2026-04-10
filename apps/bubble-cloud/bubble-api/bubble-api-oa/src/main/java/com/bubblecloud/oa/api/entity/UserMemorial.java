package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 备忘录，对应 eb_user_memorial 表。
 *
 * @author qinlei
 * @date 2026/4/2 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "备忘录")
@TableName("eb_user_memorial")
public class UserMemorial extends Req<UserMemorial> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户UID")
	private String uid;

	@Schema(description = "标题")
	private String title;

	@Schema(description = "内容（入库为 HTML 转义）")
	private String content;

	@Schema(description = "所属分类ID")
	private Integer pid;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@TableField(exist = false)
	@Schema(description = "列表查询：标题/内容模糊（不入库）")
	private String titleSearch;

}
