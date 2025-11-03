package com.bubblecloud.api.backend.feign;

import com.bubblecloud.api.backend.dto.UserDTO;
import com.bubblecloud.api.backend.dto.UserInfo;
import com.bubblecloud.common.core.constant.ServiceNameConstants;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author lengleng
 * @date 2018/6/22
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.BACKEND_SERVICE)
public interface RemoteUserService {

    /**
     * (未登录状态调用，需要加 @NoToken)
     * 通过用户名查询用户、角色信息
     *
     * @param user 用户查询对象
     * @return R
     */
    @NoToken
    @GetMapping("/user/info/query")
    R<UserInfo> info(@SpringQueryMap UserDTO user);

}
