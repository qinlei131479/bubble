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
 * 邀请完善档案记录，对应 eb_user_card_perfect。
 *
 * @author qinlei
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "邀请完善档案")
@TableName("eb_user_card_perfect")
public class UserCardPerfect extends Req<UserCardPerfect> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private Integer creator;

	private Integer userId;

	private Long entid;

	private String uid;

	private Integer cardId;

	private String uniqued;

	private Integer total;

	private Integer used;

	private Integer status;

	private Integer types;

	private LocalDateTime failTime;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
