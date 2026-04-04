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
 * 客户附件，对应 eb_client_file 表。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户附件")
@TableName("eb_client_file")
public class ClientFile extends Req<ClientFile> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "附件ID")
	private Long id;

	@Schema(description = "客户ID")
	private Integer eid;

	@Schema(description = "合同ID")
	private Integer cid;

	@Schema(description = "跟进记录ID")
	private Integer fid;

	@Schema(description = "发票申请ID")
	private Integer vid;

	@Schema(description = "上传用户ID")
	private Integer uid;

	@Schema(description = "附件名称")
	private String name;

	@Schema(description = "原始名称")
	private String realName;

	@Schema(description = "附件路径")
	private String attDir;

	@Schema(description = "压缩路径")
	private String thumbDir;

	@Schema(description = "附件大小")
	private String attSize;

	@Schema(description = "附件类型")
	private String attType;

	@Schema(description = "企业ID")
	private Integer entid;

	@Schema(description = "上传方式")
	private Integer upType;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
