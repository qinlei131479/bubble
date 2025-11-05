package com.pig4cloud.pig.codegen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.codegen.entity.GenDatasourceConf;

/**
 * 数据源配置服务接口 提供数据源的增删改查及校验等功能
 *
 * @author qinlei
 * @date 2025/05/31
 */
public interface GenDatasourceConfService extends IService<GenDatasourceConf> {

	/**
	 * 保存数据源并加密
	 * @param genDatasourceConf 数据源配置信息
	 * @return 保存是否成功
	 */
	Boolean saveDsByEnc(GenDatasourceConf genDatasourceConf);

	/**
	 * 更新数据源
	 * @param genDatasourceConf 数据源配置信息
	 * @return 更新是否成功
	 */
	Boolean updateDsByEnc(GenDatasourceConf genDatasourceConf);

	/**
	 * 添加动态数据源
	 * @param datasourceConf 数据源配置信息
	 */
	void addDynamicDataSource(GenDatasourceConf datasourceConf);

	/**
	 * 校验数据源配置是否有效
	 * @param datasourceConf 数据源配置信息
	 * @return true表示有效，false表示无效
	 */
	Boolean checkDataSource(GenDatasourceConf datasourceConf);

	/**
	 * 通过数据源ID删除数据源
	 * @param dsIds 数据源ID数组
	 * @return 删除是否成功
	 */
	Boolean removeByDsId(Long[] dsIds);

}
