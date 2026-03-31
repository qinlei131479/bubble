package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.SystemAttachMapper;
import com.bubblecloud.biz.oa.service.SystemAttachAdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.SystemAttach;
import org.springframework.stereotype.Service;

/**
 * 系统附件实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
public class SystemAttachAdminServiceImpl extends UpServiceImpl<SystemAttachMapper, SystemAttach>
		implements SystemAttachAdminService {

}
