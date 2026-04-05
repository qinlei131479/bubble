package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.vo.perfect.UserPerfectIndexVO;

public interface UserPerfectBizService {

	UserPerfectIndexVO listForCurrentUser(Long adminId, Integer status, Integer page, Integer limit);

	void agree(Long adminId, Long id);

	void refuse(Long id);

}
