package com.bubblecloud.daemon.quartz.task;

import com.bubblecloud.daemon.quartz.constants.PigQuartzEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Spring Bean任务演示类
 *
 * @author qinlei
 * @author 郑健楠
 * @date 2025/05/31
 */
@Slf4j
@Component("demo")
public class SpringBeanTaskDemo {

	/**
	 * 演示方法，用于测试Spring Bean
	 * @param para 输入参数
	 * @return 返回任务日志状态成功类型
	 * @throws Exception 可能抛出的异常
	 */
	@SneakyThrows
	public String demoMethod(String para) {
		log.info("测试于:{}，输入参数{}", LocalDateTime.now(), para);
		return PigQuartzEnum.JOB_LOG_STATUS_SUCCESS.getType();
	}

}
