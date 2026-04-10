package com.bubblecloud.oa.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bubblecloud.common.mybatis.base.Req;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工档案扩展，对应 eb_admin_info 表（主键与 eb_admin.id 一致）。
 *
 * @author qinlei
 * @date 2026/4/4 16:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "员工档案扩展")
@TableName("eb_admin_info")
public class AdminInfo extends Req<AdminInfo> {

	private static final long serialVersionUID = 1L;

	@TableId
	@Schema(description = "主键（eb_admin.id）")
	private Long id;

	@Schema(description = "用户UID")
	private String uid;

	@Schema(description = "姓氏首字母")
	private String letter;

	@Schema(description = "城市")
	private String city;

	@Schema(description = "区域")
	private String area;

	@Schema(description = "身份证号")
	@TableField("card_id")
	private String cardId;

	@Schema(description = "省份")
	private String province;

	@Schema(description = "生日")
	private String birthday;

	@Schema(description = "民族")
	private String nation;

	@Schema(description = "政治面貌")
	private String politic;

	@Schema(description = "学历")
	private String education;

	@Schema(description = "学历证书")
	@TableField("education_image")
	private String educationImage;

	@Schema(description = "学位")
	private String acad;

	@Schema(description = "学位证书")
	@TableField("acad_image")
	private String acadImage;

	@Schema(description = "籍贯")
	@TableField("`native`")
	private String nativePlace;

	@Schema(description = "居住地")
	private String address;

	@Schema(description = "性别")
	private Integer sex;

	@Schema(description = "年龄")
	private Integer age;

	@Schema(description = "婚姻")
	private Integer marriage;

	@Schema(description = "员工状态类型")
	private Integer type;

	@Schema(description = "工作经验年数")
	@TableField("work_years")
	private Integer workYears;

	@Schema(description = "紧急联系人")
	@TableField("spare_name")
	private String spareName;

	@Schema(description = "紧急联系电话")
	@TableField("spare_tel")
	private String spareTel;

	@Schema(description = "邮箱")
	private String email;

	@Schema(description = "社保账户")
	@TableField("social_num")
	private String socialNum;

	@Schema(description = "公积金账户")
	@TableField("fund_num")
	private String fundNum;

	@Schema(description = "银行卡号")
	@TableField("bank_num")
	private String bankNum;

	@Schema(description = "开户行")
	@TableField("bank_name")
	private String bankName;

	@Schema(description = "毕业院校")
	@TableField("graduate_name")
	private String graduateName;

	@Schema(description = "毕业时间")
	@TableField("graduate_date")
	private String graduateDate;

	@Schema(description = "面试时间")
	@TableField("interview_date")
	private String interviewDate;

	@Schema(description = "面试职位")
	@TableField("interview_position")
	private String interviewPosition;

	@Schema(description = "是否兼职")
	@TableField("is_part")
	private Integer isPart;

	@Schema(description = "员工照片")
	private String photo;

	@Schema(description = "身份证正面")
	@TableField("card_front")
	private String cardFront;

	@Schema(description = "身份证背面")
	@TableField("card_both")
	private String cardBoth;

	@Schema(description = "入职时间")
	@TableField("work_time")
	private String workTime;

	@Schema(description = "试用时间")
	@TableField("trial_time")
	private String trialTime;

	@Schema(description = "转正时间")
	@TableField("formal_time")
	private String formalTime;

	@Schema(description = "合同到期")
	@TableField("treaty_time")
	private String treatyTime;

	@Schema(description = "离职时间")
	@TableField("quit_time")
	private String quitTime;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "创建时间")
	private LocalDateTime createdAt;

	@Schema(description = "更新时间")
	private LocalDateTime updatedAt;

	@Schema(description = "删除时间")
	private LocalDateTime deletedAt;

}
