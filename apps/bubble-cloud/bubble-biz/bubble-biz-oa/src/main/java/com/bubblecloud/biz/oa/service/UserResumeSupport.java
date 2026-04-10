package com.bubblecloud.biz.oa.service;

/**
 * 解析/确保当前登录用户关联的 {@link com.bubblecloud.oa.api.entity.UserResume}。
 *
 * @author qinlei
 * @date 2026/4/5
 */
public interface UserResumeSupport {

	/**
	 * 按员工主键解析 UID，并返回简历主键（不存在则插入空简历）。
	 */
	Long getOrCreateResumeId(Long adminId);

	/**
	 * 解析员工 UID。
	 */
	String requireUid(Long adminId);

}
