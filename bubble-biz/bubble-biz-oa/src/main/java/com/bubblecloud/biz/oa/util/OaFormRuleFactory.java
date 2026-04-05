package com.bubblecloud.biz.oa.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bubblecloud.oa.api.entity.EnterpriseUserEducation;
import com.bubblecloud.oa.api.entity.EnterpriseUserPosition;
import com.bubblecloud.oa.api.entity.EnterpriseUserWork;
import com.bubblecloud.oa.api.entity.UserEducationHistory;
import com.bubblecloud.oa.api.entity.UserWorkHistory;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 生成 form-create（element-ui）规则 JSON，对齐 PHP FormService 常用字段形态。
 */
public final class OaFormRuleFactory {

	private OaFormRuleFactory() {
	}

	public static ArrayNode userWorkHistoryRules(ObjectMapper om, long resumeId, UserWorkHistory row) {
		ArrayNode rules = om.createArrayNode();
		rules.add(hidden(om, "resume_id", resumeId));
		rules.add(datePicker(om, "start_time", "开始时间", dateVal(row == null ? null : row.getStartTime()), true));
		rules.add(datePicker(om, "end_time", "结束时间", dateVal(row == null ? null : row.getEndTime()), true));
		rules.add(input(om, "company", "所在公司", str(row == null ? null : row.getCompany()), true));
		rules.add(input(om, "position", "职位", str(row == null ? null : row.getPosition()), true));
		rules.add(textarea(om, "describe", "工作描述", str(row == null ? null : row.getDescribe()), true));
		rules.add(textarea(om, "quit_reason", "离职原因", str(row == null ? null : row.getQuitReason()), false));
		return rules;
	}

	public static ArrayNode userEducationHistoryRules(ObjectMapper om, long resumeId, UserEducationHistory row) {
		ArrayNode rules = om.createArrayNode();
		rules.add(hidden(om, "resume_id", resumeId));
		rules.add(datePicker(om, "start_time", "入学时间", dateVal(row == null ? null : row.getStartTime()), true));
		rules.add(datePicker(om, "end_time", "毕业时间", dateVal(row == null ? null : row.getEndTime()), true));
		rules.add(input(om, "school_name", "学校名称", str(row == null ? null : row.getSchoolName()), true));
		rules.add(input(om, "major", "所学专业", str(row == null ? null : row.getMajor()), true));
		rules.add(input(om, "education", "学历", str(row == null ? null : row.getEducation()), true));
		rules.add(input(om, "academic", "学位", str(row == null ? null : row.getAcademic()), false));
		rules.add(textarea(om, "remark", "备注", str(row == null ? null : row.getRemark()), false));
		return rules;
	}

	public static ArrayNode enterpriseUserWorkRules(ObjectMapper om, long userId, EnterpriseUserWork row) {
		ArrayNode rules = om.createArrayNode();
		rules.add(hidden(om, "user_id", userId));
		rules.add(datePicker(om, "start_time", "开始时间", dateVal(row == null ? null : row.getStartTime()), true));
		rules.add(datePicker(om, "end_time", "结束时间", dateVal(row == null ? null : row.getEndTime()), true));
		rules.add(input(om, "company", "所在公司", str(row == null ? null : row.getCompany()), true));
		rules.add(input(om, "position", "职位", str(row == null ? null : row.getPosition()), true));
		rules.add(textarea(om, "describe", "工作描述", str(row == null ? null : row.getDescribe()), true));
		rules.add(textarea(om, "quit_reason", "离职原因", str(row == null ? null : row.getQuitReason()), false));
		return rules;
	}

	public static ArrayNode enterpriseUserEducationRules(ObjectMapper om, long userId, EnterpriseUserEducation row) {
		ArrayNode rules = om.createArrayNode();
		rules.add(hidden(om, "user_id", userId));
		rules.add(datePicker(om, "start_time", "入学时间", dateVal(row == null ? null : row.getStartTime()), true));
		rules.add(datePicker(om, "end_time", "毕业时间", dateVal(row == null ? null : row.getEndTime()), true));
		rules.add(input(om, "school_name", "学校名称", str(row == null ? null : row.getSchoolName()), true));
		rules.add(input(om, "major", "所学专业", str(row == null ? null : row.getMajor()), true));
		rules.add(input(om, "education", "学历", str(row == null ? null : row.getEducation()), true));
		rules.add(input(om, "academic", "学位", str(row == null ? null : row.getAcademic()), false));
		rules.add(textarea(om, "remark", "备注", str(row == null ? null : row.getRemark()), false));
		return rules;
	}

	public static ArrayNode enterpriseUserPositionRules(ObjectMapper om, long userId, EnterpriseUserPosition row) {
		ArrayNode rules = om.createArrayNode();
		rules.add(hidden(om, "user_id", userId));
		rules.add(datePicker(om, "start_time", "开始时间", dateTimeVal(row == null ? null : row.getStartTime()), true));
		rules.add(datePicker(om, "end_time", "结束时间", dateTimeVal(row == null ? null : row.getEndTime()), true));
		rules.add(input(om, "position", "职位", str(row == null ? null : row.getPosition()), true));
		rules.add(input(om, "department", "部门", str(row == null ? null : row.getDepartment()), true));
		rules.add(textarea(om, "remark", "备注", str(row == null ? null : row.getRemark()), false));
		ObjectNode isAdmin = om.createObjectNode();
		isAdmin.put("type", "radio");
		isAdmin.put("field", "is_admin");
		isAdmin.put("title", "身份");
		isAdmin.put("value", row == null ? 0 : nz(row.getIsAdmin()));
		isAdmin.put("$required", true);
		ArrayNode opts = om.createArrayNode();
		ObjectNode o0 = om.createObjectNode();
		o0.put("label", "普通员工");
		o0.put("value", 0);
		ObjectNode o1 = om.createObjectNode();
		o1.put("label", "主管");
		o1.put("value", 1);
		opts.add(o0);
		opts.add(o1);
		isAdmin.set("options", opts);
		rules.add(isAdmin);
		ObjectNode status = om.createObjectNode();
		status.put("type", "radio");
		status.put("field", "status");
		status.put("title", "任职状态");
		status.put("value", row == null ? 1 : nz(row.getStatus()));
		status.put("$required", true);
		ArrayNode st = om.createArrayNode();
		ObjectNode s0 = om.createObjectNode();
		s0.put("label", "离职");
		s0.put("value", 0);
		ObjectNode s1 = om.createObjectNode();
		s1.put("label", "任职");
		s1.put("value", 1);
		st.add(s0);
		st.add(s1);
		status.set("options", st);
		rules.add(status);
		return rules;
	}

	public static ArrayNode memorialCategoryRules(ObjectMapper om, ArrayNode cascaderOptions, ArrayNode pathValue,
			String name) {
		ArrayNode rules = om.createArrayNode();
		ObjectNode path = om.createObjectNode();
		path.put("type", "cascader");
		path.put("field", "path");
		path.put("title", "前置文件夹");
		path.set("value", pathValue == null ? om.createArrayNode() : pathValue);
		ObjectNode props = om.createObjectNode();
		props.set("options", cascaderOptions == null ? om.createArrayNode() : cascaderOptions);
		ObjectNode inner = om.createObjectNode();
		inner.put("checkStrictly", true);
		inner.put("emitPath", true);
		props.set("props", inner);
		path.set("props", props);
		path.put("placeholder", "请选择前置文件夹");
		rules.add(path);
		rules.add(input(om, "name", "文件夹名称", StrUtil.nullToEmpty(name), true));
		return rules;
	}

	private static int nz(Integer v) {
		return v == null ? 0 : v;
	}

	private static ObjectNode hidden(ObjectMapper om, String field, long value) {
		ObjectNode n = om.createObjectNode();
		n.put("type", "hidden");
		n.put("field", field);
		n.put("value", value);
		return n;
	}

	private static ObjectNode datePicker(ObjectMapper om, String field, String title, String value, boolean required) {
		ObjectNode n = om.createObjectNode();
		n.put("type", "datePicker");
		n.put("field", field);
		n.put("title", title);
		n.put("value", StrUtil.nullToEmpty(value));
		ObjectNode p = om.createObjectNode();
		p.put("type", "date");
		p.put("placeholder", "选择日期");
		n.set("props", p);
		if (required) {
			n.put("$required", true);
		}
		return n;
	}

	private static ObjectNode input(ObjectMapper om, String field, String title, String value, boolean required) {
		ObjectNode n = om.createObjectNode();
		n.put("type", "input");
		n.put("field", field);
		n.put("title", title);
		n.put("value", StrUtil.nullToEmpty(value));
		if (required) {
			n.put("$required", true);
		}
		return n;
	}

	private static ObjectNode textarea(ObjectMapper om, String field, String title, String value, boolean required) {
		ObjectNode n = om.createObjectNode();
		n.put("type", "input");
		n.put("field", field);
		n.put("title", title);
		n.put("value", StrUtil.nullToEmpty(value));
		ObjectNode p = om.createObjectNode();
		p.put("type", "textarea");
		p.put("rows", 4);
		n.set("props", p);
		if (required) {
			n.put("$required", true);
		}
		return n;
	}

	private static String str(String s) {
		return StrUtil.nullToEmpty(s);
	}

	private static String dateVal(java.time.LocalDate d) {
		if (ObjectUtil.isNull(d)) {
			return "";
		}
		return d.toString();
	}

	private static String dateTimeVal(java.time.LocalDateTime d) {
		if (ObjectUtil.isNull(d)) {
			return "";
		}
		return d.toLocalDate().toString();
	}

}
