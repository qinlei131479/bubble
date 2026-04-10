package com.bubblecloud.common.datasource.config;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.bubblecloud.common.datasource.support.DataSourceConstants;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 主数据源提供者，用于保证原有配置有效性并扩展其他数据源，和原有spring.datasource配置兼容。
 *
 * @author lengleng
 * @date 2025/07/14
 */
public class MasterDataSourceProvider extends AbstractDataSourceProvider {

	private final DataSourceProperties properties;

	private final DefaultDataSourceCreator defaultDataSourceCreator;

	public MasterDataSourceProvider(DefaultDataSourceCreator defaultDataSourceCreator,
			DataSourceProperties properties) {
		super(defaultDataSourceCreator);
		this.properties = properties;
		this.defaultDataSourceCreator = defaultDataSourceCreator;
	}

	/**
	 * 加载所有数据源
	 * @return 所有数据源，key为数据源名称
	 */
	@Override
	public Map<String, DataSource> loadDataSources() {
		Map<String, DataSource> map = new HashMap<>();
		// 添加默认主数据源
		DataSourceProperty property = new DataSourceProperty();
		property.setUsername(properties.getUsername());
		property.setPassword(properties.getPassword());
		property.setUrl(properties.getUrl());
		map.put(DataSourceConstants.DS_MASTER, defaultDataSourceCreator.createDataSource(property));
		return map;
	}

}
