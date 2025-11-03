package com.bubblecloud.common.security.service;

import com.bubblecloud.api.backend.dto.UserDTO;
import com.bubblecloud.api.backend.dto.UserInfo;
import com.bubblecloud.api.backend.feign.RemoteUserService;
import com.bubblecloud.common.core.constant.CacheConstants;
import com.bubblecloud.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户详细信息
 *
 * @author lengleng hccake
 */
@Slf4j
@Primary
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final RemoteUserService remoteUserService;

    private final CacheManager cacheManager;

    /**
     * 用户名密码登录
     *
     * @param username 用户名
     * @return
     */
    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername(String username) {
        Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
        if (cache != null && cache.get(username) != null) {
            return (CustomUser) cache.get(username).get();
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        R<UserInfo> result = remoteUserService.info(userDTO);
        UserDetails userDetails = getUserDetails(result);
        if (cache != null) {
            cache.put(username, userDetails);
        }
        return userDetails;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
