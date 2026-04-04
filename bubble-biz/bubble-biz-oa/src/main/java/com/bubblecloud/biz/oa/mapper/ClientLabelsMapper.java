package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.ClientLabels;

/**
 * eb_client_labels Mapper。
 *
 * @author qinlei
 * @date 2026/4/3 10:00
 */
@Mapper
public interface ClientLabelsMapper extends UpMapper<ClientLabels> {

}
