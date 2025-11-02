package com.pig4cloud.pig.daemon.quartz.task;

import com.pig4cloud.pig.daemon.quartz.constants.PigQuartzEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author 郑健楠
 */
@Slf4j
@Component("demo")
public class SpringBeanTaskDemo {

	/**
	 * 测试Spring Bean的演示方法
	 */
	@SneakyThrows
	public String demoMethod(String para) {
		log.info("测试于:{}，输入参数{}", LocalDateTime.now(), para);
		return PigQuartzEnum.JOB_LOG_STATUS_SUCCESS.getType();
	}

}
