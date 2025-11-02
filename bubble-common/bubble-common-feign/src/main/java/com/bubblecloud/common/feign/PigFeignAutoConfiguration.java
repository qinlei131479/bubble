package com.bubblecloud.common.feign;

import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bubblecloud.common.feign.core.PigFeignInnerRequestInterceptor;
import com.bubblecloud.common.feign.core.PigFeignRequestCloseInterceptor;
import com.bubblecloud.common.feign.sentinel.ext.PigSentinelFeign;
import com.bubblecloud.common.feign.sentinel.handle.PigUrlBlockHandler;
import com.bubblecloud.common.feign.sentinel.parser.PigHeaderRequestOriginParser;
import feign.Feign;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.PigFeignClientsRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

/**
 * sentinel 配置
 *
 * @author lengleng
 * @date 2020-02-12
 */
@Configuration(proxyBeanMethods = false)
@Import(PigFeignClientsRegistrar.class)
@AutoConfigureBefore(SentinelFeignAutoConfiguration.class)
public class PigFeignAutoConfiguration {

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "feign.sentinel.enabled")
    public Feign.Builder feignSentinelBuilder() {
        return PigSentinelFeign.builder();
    }

    @Bean
    @ConditionalOnMissingBean
    public BlockExceptionHandler blockExceptionHandler(ObjectMapper objectMapper) {
        return new PigUrlBlockHandler(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestOriginParser requestOriginParser() {
        return new PigHeaderRequestOriginParser();
    }

    /**
     * set connection close header
     *
     * @return RequestInterceptor
     */
    @Bean
    public RequestInterceptor pigFeignRequestCloseInterceptor() {
        return new PigFeignRequestCloseInterceptor();
    }

    /**
     * pig feign 内部请求拦截器
     *
     * @return {@link RequestInterceptor }
     */
    @Bean
    public RequestInterceptor pigFeignInnerRequestInterceptor() {
        return new PigFeignInnerRequestInterceptor();
    }
}
