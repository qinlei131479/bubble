package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

/**
 * PUT /ent/user/resume_save（字段与 PHP postMore 一致，未传则保持空串）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResumeSaveDTO {

	private String name;

	private String photo;

	private String position;

	private String cardId;

	private String birthday;

	private String age;

	private String education;

	private String educationImage;

	private String phone;

	private Integer sex;

	private String nation;

	private String acad;

	private String acadImage;

	private String politic;

	@JsonProperty("native")
	private String nativePlace;

	private String address;

	private Integer marriage;

	private String workYears;

	private String spareName;

	private String spareTel;

	private String email;

	private String workTime;

	private String trialTime;

	private String formalTime;

	private String treatyTime;

	private Integer isPart;

	private String socialNum;

	private String fundNum;

	private String bankNum;

	private String bankName;

	private String graduateName;

	private String graduateDate;

	private String cardFront;

	private String cardBoth;

}
