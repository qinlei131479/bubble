package com.bubblecloud.common.log.event;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.bubblecloud.backed.api.entity.SysLog;
import com.bubblecloud.backed.api.feign.RemoteLogService;
import com.pig4cloud.pig.common.core.jackson.PigJavaTimeModule;
import com.bubblecloud.common.log.config.PigLogProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.Objects;

/**
 * @author lengleng 异步监听日志事件
 */
@Slf4j
@RequiredArgsConstructor
public class SysLogListener implements InitializingBean {

    // new 一个 避免日志脱敏策略影响全局ObjectMapper
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final RemoteLogService remoteLogService;

    private final PigLogProperties logProperties;

    @SneakyThrows
    @Async
    @Order
    @EventListener(SysLogEvent.class)
    public void saveSysLog(SysLogEvent event) {
        SysLogEventSource source = (SysLogEventSource) event.getSource();
        SysLog sysLog = new SysLog();
        BeanUtils.copyProperties(source, sysLog);

        // json 格式刷参数放在异步中处理，提升性能
        if (Objects.nonNull(source.getBody())) {
            String params = objectMapper.writeValueAsString(source.getBody());
            sysLog.setParams(StrUtil.subPre(params, logProperties.getMaxLength()));
        }

        remoteLogService.saveLog(sysLog);
    }

    @Override
    public void afterPropertiesSet() {
        objectMapper.addMixIn(Object.class, PropertyFilterMixIn.class);
        String[] ignorableFieldNames = logProperties.getExcludeFields().toArray(new String[0]);

        FilterProvider filters = new SimpleFilterProvider().addFilter("filter properties by name",
                SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames));
        objectMapper.setFilterProvider(filters);
        objectMapper.registerModule(new PigJavaTimeModule());
    }

    @JsonFilter("filter properties by name")
    class PropertyFilterMixIn {

    }

}
