package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.UserEnterpriseApply;

/**
 * eb_user_enterprise_apply 表 Mapper（SQL 见 classpath:/mapper/UserEnterpriseApplyMapper.xml）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
@Mapper
public interface UserEnterpriseApplyMapper extends UpMapper<UserEnterpriseApply> {

	long countInviterReview(@Param("entid") int entid);

}
