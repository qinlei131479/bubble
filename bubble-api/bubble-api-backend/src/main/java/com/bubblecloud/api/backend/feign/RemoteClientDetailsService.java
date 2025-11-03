package com.bubblecloud.api.backend.feign;

import com.bubblecloud.api.backend.entity.SysOauthClientDetails;
import com.bubblecloud.common.core.constant.ServiceNameConstants;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author lengleng
 * @date 2020/12/05
 */
@FeignClient(contextId = "remoteClientDetailsService", value = ServiceNameConstants.BACKEND_SERVICE)
public interface RemoteClientDetailsService {

    /**
     * 通过clientId 查询客户端信息 (未登录，需要无token 内部调用)
     *
     * @param clientId 用户名
     * @return R
     */
    @NoToken
    @GetMapping("/client/getClientDetailsById/{clientId}")
    R<SysOauthClientDetails> getClientDetailsById(@PathVariable("clientId") String clientId);


}
