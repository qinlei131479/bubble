package com.bubblecloud.common.core.constant;

/**
 * @author lengleng
 * @date 2020年01月01日
 * <p>
 * 缓存的key 常量
 */
public interface CacheConstants {

	/**
	 * 每个项目定义一个顶级目录
	 */
	String TOP = "bubble-cloud::";
	/**
	 * oauth 缓存前缀
	 */
	String PROJECT_OAUTH_ACCESS = TOP + "token::access_token";

	/**
	 * 验证码前缀
	 */
	String DEFAULT_CODE_KEY = TOP + "DEFAULT_CODE_KEY:";

	/**
	 * 菜单信息缓存
	 */
	String MENU_DETAILS = TOP + "menu_details";

	/**
	 * 用户信息缓存
	 */
	String USER_DETAILS = TOP + "user_details";

	/**
	 * 字典信息缓存
	 */
	String DICT_DETAILS = TOP + "dict_details";

	/**
	 * 角色信息缓存
	 */
	String ROLE_DETAILS = TOP + "role_details";

	/**
	 * oauth 客户端信息
	 */
	String CLIENT_DETAILS_KEY = TOP + "client:details";

	/**
	 * 参数缓存
	 */
	String PARAMS_DETAILS = TOP + "params_details";

}
