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
 * 系统附件，对应 eb_system_attach 表。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "系统附件")
@TableName("eb_system_attach")
public class SystemAttach extends Req<SystemAttach> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Integer id;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "上传用户 UID")
	private String uid;

	@Schema(description = "附件显示名")
	private String name;

	@Schema(description = "原始文件名")
	@TableField("real_name")
	private String realName;

	@Schema(description = "存储路径")
	@TableField("att_dir")
	private String attDir;

	@Schema(description = "缩略图路径")
	@TableField("thumb_dir")
	private String thumbDir;

	@Schema(description = "文件大小")
	@TableField("att_size")
	private String attSize;

	@Schema(description = "MIME 类型")
	@TableField("att_type")
	private String attType;

	@Schema(description = "扩展名")
	@TableField("file_ext")
	private String fileExt;

	@Schema(description = "分类ID")
	private Integer cid;

	@Schema(description = "上传来源类型")
	@TableField("up_type")
	private Integer upType;

	@Schema(description = "上传方式")
	private Integer way;

	@Schema(description = "关联业务类型")
	@TableField("relation_type")
	private Integer relationType;

	@Schema(description = "关联业务主键")
	@TableField("relation_id")
	private Integer relationId;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
