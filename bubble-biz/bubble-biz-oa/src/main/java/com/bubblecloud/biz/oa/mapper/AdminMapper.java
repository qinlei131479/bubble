package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.vo.enterprise.EnterpriseUserListItemVO;

/**
 * eb_admin 表 Mapper。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Mapper
public interface AdminMapper extends UpMapper<Admin> {

	Page<EnterpriseUserListItemVO> selectEntUserList(Page<EnterpriseUserListItemVO> page, @Param("entid") Long entid,
			@Param("name") String name, @Param("status") Integer status);

}
