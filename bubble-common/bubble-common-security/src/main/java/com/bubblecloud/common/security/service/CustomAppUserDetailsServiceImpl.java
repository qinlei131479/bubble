package com.bubblecloud.common.security.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UserDetails;

import com.bubblecloud.backend.api.dto.UserDTO;
import com.bubblecloud.backend.api.dto.UserInfo;
import com.bubblecloud.backend.api.feign.RemoteUserService;
import com.bubblecloud.common.core.constant.CacheConstants;
import com.bubblecloud.common.core.constant.SecurityConstants;
import com.bubblecloud.common.core.util.R;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * 用户详细信息服务实现类，提供基于手机号的用户信息加载功能
 *
 * @author lengleng hccake
 * @date 2025/05/31
 */
@RequiredArgsConstructor
public class CustomAppUserDetailsServiceImpl implements CustomUserDetailsService {

	private final RemoteUserService remoteUserService;

	private final CacheManager cacheManager;

	/**
	 * 根据手机号加载用户信息
	 * @param phone 用户手机号
	 * @return 用户详细信息
	 * @throws Exception 获取用户信息过程中可能抛出的异常
	 */
	@Override
	@SneakyThrows
	public UserDetails loadUserByUsername(String phone) {
		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		if (cache != null && cache.get(phone) != null) {
			return (CustomUser) cache.get(phone).get();
		}

		UserDTO userDTO = new UserDTO();
		userDTO.setPhone(phone);
		R<UserInfo> result = remoteUserService.info(userDTO);

		UserDetails userDetails = getUserDetails(result);
		if (cache != null) {
			cache.put(phone, userDetails);
		}
		return userDetails;
	}

	/**
	 * 根据用户信息加载用户详情
	 * @param customUser 用户信息对象
	 * @return 用户详情
	 */
	@Override
	public UserDetails loadUserByUser(CustomUser customUser) {
		return this.loadUserByUsername(customUser.getPhone());
	}

	/**
	 * 是否支持此客户端校验
	 * @param clientId 目标客户端
	 * @return true/false
	 */
	@Override
	public boolean support(String clientId, String grantType) {
		return SecurityConstants.MOBILE.equals(grantType);
	}

}
