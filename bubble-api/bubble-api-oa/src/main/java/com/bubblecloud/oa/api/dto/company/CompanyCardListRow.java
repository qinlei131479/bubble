package com.bubblecloud.oa.api.dto.company;

import lombok.Data;

/**
 * 员工档案列表行（Admin + AdminInfo 联表查询结果）。
 *
 * @author qinlei
 */
@Data
public class CompanyCardListRow {

	private Long id;

	private String uid;

	private String name;

	private String phone;

	private String avatar;

	private Integer status;

	private Integer job;

	private Integer sex;

	private String education;

	private Integer isPart;

	private Integer type;

	private String interviewPosition;

	private String workTime;

	private String quitTime;

	private String formalTime;

	private String interviewDate;

	private Integer sort;

	private java.time.LocalDateTime infoCreatedAt;

}
