package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.dto.CheckPwdDTO;
import com.bubblecloud.oa.api.dto.UserResumeSaveDTO;
import com.bubblecloud.oa.api.dto.UserSelfUpdateDTO;
import com.bubblecloud.oa.api.vo.user.UserResumeDetailVO;
import com.bubblecloud.oa.api.vo.user.UserSelfInfoVO;

/**
 * 当前登录用户资料与简历（对齐 PHP UserController）。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
public interface UserProfileService {

	UserSelfInfoVO getSelfInfo(long adminId);

	void updateSelf(long adminId, UserSelfUpdateDTO dto);

	void checkPwd(CheckPwdDTO dto);

	UserResumeDetailVO getResume(long adminId);

	void saveResume(long adminId, UserResumeSaveDTO dto);

}
