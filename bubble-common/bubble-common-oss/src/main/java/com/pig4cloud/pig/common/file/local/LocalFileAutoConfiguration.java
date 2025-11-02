package com.pig4cloud.pig.common.file.local;

import com.pig4cloud.pig.common.file.core.FileProperties;
import com.pig4cloud.pig.common.file.core.FileTemplate;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * aws 自动配置类
 *
 * @author lengleng
 * @author 858695266
 */
@AllArgsConstructor
public class LocalFileAutoConfiguration {

	private final FileProperties properties;

	@Bean
	@ConditionalOnMissingBean(LocalFileTemplate.class)
	@ConditionalOnProperty(name = "file.local.enable", havingValue = "true", matchIfMissing = true)
	public FileTemplate localFileTemplate() {
		return new LocalFileTemplate(properties);
	}

}
