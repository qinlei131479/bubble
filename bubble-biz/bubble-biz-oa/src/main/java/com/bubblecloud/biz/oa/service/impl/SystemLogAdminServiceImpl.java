package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.EnterpriseLogMapper;
import com.bubblecloud.biz.oa.service.SystemLogAdminService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.EnterpriseLog;
import org.springframework.stereotype.Service;

/**
 * 系统操作日志实现。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
public class SystemLogAdminServiceImpl extends UpServiceImpl<EnterpriseLogMapper, EnterpriseLog>
		implements SystemLogAdminService {

}
