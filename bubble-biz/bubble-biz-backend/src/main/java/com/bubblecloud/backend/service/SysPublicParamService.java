package com.bubblecloud.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bubblecloud.backend.api.entity.SysPublicParam;
import com.pig4cloud.pig.common.core.util.R;

/**
 * 系统公共参数配置表 服务类
 *
 * @author lengleng
 * @date 2025/05/30
 */
public interface SysPublicParamService extends IService<SysPublicParam> {

	/**
	 * 根据公共参数key获取对应的value值
	 * @param publicKey 公共参数key
	 * @return 公共参数value，未找到时返回null
	 */
	String getParamValue(String publicKey);

	/**
	 * 更新系统公共参数
	 * @param sysPublicParam 系统公共参数对象
	 * @return 操作结果
	 */
	R updateParam(SysPublicParam sysPublicParam);

	/**
	 * 根据ID删除参数
	 * @param publicIds 参数ID数组
	 * @return 删除结果
	 */
	R removeParamByIds(Long[] publicIds);

	/**
	 * 同步参数缓存
	 * @return 操作结果
	 */
	R syncParamCache();

}
