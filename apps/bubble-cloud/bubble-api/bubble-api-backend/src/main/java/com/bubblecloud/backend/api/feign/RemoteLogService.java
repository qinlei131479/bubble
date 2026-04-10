package com.bubblecloud.backend.api.feign;

import com.bubblecloud.backend.api.entity.SysLog;
import com.bubblecloud.common.core.constant.ServiceNameConstants;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 远程日志服务接口
 *
 * @author lengleng
 * @date 2025/05/30
 */
@FeignClient(contextId = "remoteLogService", value = ServiceNameConstants.BACKEND_SERVICE)
public interface RemoteLogService {

	/**
	 * 保存日志 (异步多线程调用，无token)
	 * @param sysLog 日志实体
	 * @return succes、false
	 */
	@NoToken
	@PostMapping("/log/save")
	R<Boolean> saveLog(@RequestBody SysLog sysLog);

}
