package com.bubblecloud.biz.agi.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.agi.api.entity.SqlTrain;
import org.apache.ibatis.annotations.Mapper;

/**
 * SQL训练示例 ORM映射层
 *
 * @author Rampart
 * @date 2026-02-12 18:37:03
 */
@Mapper
public interface SqlTrainMapper extends UpMapper<SqlTrain> {

}