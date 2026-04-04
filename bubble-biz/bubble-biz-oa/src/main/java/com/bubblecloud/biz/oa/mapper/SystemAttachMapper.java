package com.bubblecloud.biz.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.entity.SystemAttach;

/**
 * eb_system_attach Mapper。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Mapper
public interface SystemAttachMapper extends UpMapper<SystemAttach> {

	/**
	 * 客户文件列表（对齐 PHP AttachService#getRelationList：relation_type 2~6，可按客户 eid 过滤）。
	 */
	Page<SystemAttach> selectClientRelationPage(Page<SystemAttach> page, @Param("entid") int entid,
			@Param("eid") Integer eid);

}
