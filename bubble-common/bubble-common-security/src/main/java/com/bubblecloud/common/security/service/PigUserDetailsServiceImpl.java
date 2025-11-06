package com.bubblecloud.common.security.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;

import com.bubblecloud.backend.api.dto.UserDTO;
import com.bubblecloud.backend.api.dto.UserInfo;
import com.bubblecloud.backend.api.feign.RemoteUserService;
import com.bubblecloud.common.core.constant.CacheConstants;
import com.bubblecloud.common.core.util.R;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * 用户详情服务实现类，提供基于用户名加载用户详情功能
 *
 * @author lengleng
 * @author hccake
 * @date 2025/05/31
 */
@Primary
@RequiredArgsConstructor
public class PigUserDetailsServiceImpl implements PigUserDetailsService {

	private final RemoteUserService remoteUserService;

	private final CacheManager cacheManager;

	/**
	 * 根据用户名加载用户详情
	 * @param username 用户名
	 * @return 用户详情信息
	 * @throws Exception 获取用户信息过程中可能抛出的异常
	 */
	@Override
	@SneakyThrows
	public UserDetails loadUserByUsername(String username) {
		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		if (cache != null && cache.get(username) != null) {
			return (PigUser) cache.get(username).get();
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
