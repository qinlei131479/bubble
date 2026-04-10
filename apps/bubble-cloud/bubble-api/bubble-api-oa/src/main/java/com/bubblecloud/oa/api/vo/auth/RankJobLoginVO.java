package com.bubblecloud.oa.api.vo.auth;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 登录信息中的岗位（蛇形 JSON，对齐 PHP job 对象）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RankJobLoginVO {

	private Long id;

	private Long entid;

	private String name;

	private String describe;

	private String duty;

	private Integer status;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
