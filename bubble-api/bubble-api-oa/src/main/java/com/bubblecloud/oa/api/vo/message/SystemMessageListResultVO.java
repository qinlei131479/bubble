package com.bubblecloud.oa.api.vo.message;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * @author qinlei
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SystemMessageListResultVO {

	private List<SystemMessageListItemVO> list = Collections.emptyList();

	private long count;

}
