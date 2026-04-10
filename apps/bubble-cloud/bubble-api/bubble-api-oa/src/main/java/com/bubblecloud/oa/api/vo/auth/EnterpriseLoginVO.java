package com.bubblecloud.oa.api.vo.auth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 登录后企业信息（对齐 PHP loginInfo.enterprise）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EnterpriseLoginVO {

	private String title;

	private String enterpriseName;

	private String enterpriseNameEn;

	private Long entid;

	private String logo;

	private String uniqued;

	private Integer maxScore;

	private String culture;

	private Integer computeMode;

}
