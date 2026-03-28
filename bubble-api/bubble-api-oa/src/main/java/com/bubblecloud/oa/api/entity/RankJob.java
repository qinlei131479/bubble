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
 * 职位表 eb_rank_job（与 PHP Position Job 对应）。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "职位")
@TableName("eb_rank_job")
public class RankJob extends Req<RankJob> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private Long entid;

	private String name;

	private String describe;

	private String duty;

	private Integer status;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
