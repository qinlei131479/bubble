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
 * 备忘录分类，对应 eb_user_memorial_category 表。
 *
 * @author qinlei
 * @date 2026/4/2 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "备忘录分类")
@TableName("eb_user_memorial_category")
public class UserMemorialCategory extends Req<UserMemorialCategory> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户UID")
	private String uid;

	@Schema(description = "路径")
	private String path;

	@Schema(description = "分类名称")
	private String name;

	@Schema(description = "上级分类ID")
	private Integer pid;

	@Schema(description = "类型：0 默认文件夹；1 用户自建")
	private Integer types;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
