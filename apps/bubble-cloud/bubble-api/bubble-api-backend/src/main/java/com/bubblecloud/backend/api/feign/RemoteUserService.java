package com.bubblecloud.backend.api.feign;

import com.bubblecloud.backend.api.dto.UserDTO;
import com.bubblecloud.backend.api.dto.UserInfo;
import com.bubblecloud.common.core.constant.ServiceNameConstants;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 远程用户服务接口：提供用户信息查询功能
 *
 * @author lengleng
 * @date 2025/05/30
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.BACKEND_SERVICE)
public interface RemoteUserService {

	/**
	 * (未登录状态调用，需要加 @NoToken) 通过用户名查询用户、角色信息
	 * @param user 用户查询对象
	 * @return R
	 */
	@NoToken
	@GetMapping("/user/info/query")
	R<UserInfo> info(@SpringQueryMap UserDTO user);

}
