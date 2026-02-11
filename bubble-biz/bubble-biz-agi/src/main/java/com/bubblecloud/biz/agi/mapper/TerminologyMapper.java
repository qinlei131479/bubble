package com.bubblecloud.biz.agi.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.Terminology;

/**
 * Mapper接口：术语表
 *
 * @author Rampart Qin
 * @date   2026/02/11 22:35
 */
@Mapper
public interface TerminologyMapper extends UpMapper<Terminology> {
}
