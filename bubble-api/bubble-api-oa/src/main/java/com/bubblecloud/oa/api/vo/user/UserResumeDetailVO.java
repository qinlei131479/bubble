package com.bubblecloud.oa.api.vo.user;

import java.util.Collections;
import java.util.List;

import com.bubblecloud.oa.api.entity.UserResume;
import lombok.Data;

/**
 * GET /ent/user/resume：简历主表 + 工作经历/教育（后续可接子表）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Data
public class UserResumeDetailVO {

	private UserResume resume;

	private List<Object> works = Collections.emptyList();

	private List<Object> educations = Collections.emptyList();

}
