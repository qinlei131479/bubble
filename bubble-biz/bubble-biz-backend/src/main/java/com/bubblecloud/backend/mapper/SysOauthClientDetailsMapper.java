package com.bubblecloud.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.backend.api.entity.SysOauthClientDetails;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统OAuth客户端详情 Mapper接口
 *
 * @author qinlei
 * @date 2025/06/27
 */
@Mapper
public interface SysOauthClientDetailsMapper extends BaseMapper<SysOauthClientDetails> {

}
