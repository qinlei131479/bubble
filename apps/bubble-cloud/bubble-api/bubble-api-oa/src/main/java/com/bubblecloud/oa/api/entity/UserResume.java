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
 * 用户简历主表，对应 eb_user_resume 表。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户简历")
@TableName("eb_user_resume")
public class UserResume extends Req<UserResume> {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	@Schema(description = "主键ID")
	private Long id;

	@Schema(description = "用户UID")
	private String uid;

	@Schema(description = "照片")
	private String photo;

	@Schema(description = "员工姓名")
	private String name;

	@Schema(description = "手机号")
	private String phone;

	@Schema(description = "期望职位")
	private String position;

	@Schema(description = "生日")
	private String birthday;

	@Schema(description = "民族")
	private String nation;

	@Schema(description = "政治面貌")
	private String politic;

	@TableField("`native`")
	@Schema(description = "籍贯")
	private String nativePlace;

	@Schema(description = "居住地")
	private String address;

	@Schema(description = "性别：0=未知；1=男；2=女")
	private Integer sex;

	@Schema(description = "年龄")
	private Integer age;

	@Schema(description = "婚姻状况：0=未婚；1=已婚")
	private Integer marriage;

	@Schema(description = "是否兼职：1=是；0=否")
	private Integer isPart;

	@Schema(description = "工作年限")
	private Integer workYears;

	@Schema(description = "紧急联系人")
	private String spareName;

	@Schema(description = "紧急联系电话")
	private String spareTel;

	@Schema(description = "邮箱")
	private String email;

	@Schema(description = "入职时间")
	private String workTime;

	@Schema(description = "试用时间")
	private String trialTime;

	@Schema(description = "转正时间")
	private String formalTime;

	@Schema(description = "合同到期时间")
	private String treatyTime;

	@Schema(description = "社保账户")
	private String socialNum;

	@Schema(description = "公积金账户")
	private String fundNum;

	@Schema(description = "银行卡账户")
	private String bankNum;

	@Schema(description = "开户行")
	private String bankName;

	@Schema(description = "毕业院校")
	private String graduateName;

	@Schema(description = "毕业时间")
	private String graduateDate;

	@Schema(description = "身份证号")
	private String cardId;

	@Schema(description = "身份证正面")
	private String cardFront;

	@Schema(description = "身份证背面")
	private String cardBoth;

	@Schema(description = "学历")
	private String education;

	@Schema(description = "学历证书")
	private String educationImage;

	@Schema(description = "学位")
	private String acad;

	@Schema(description = "学位证书")
	private String acadImage;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

}
