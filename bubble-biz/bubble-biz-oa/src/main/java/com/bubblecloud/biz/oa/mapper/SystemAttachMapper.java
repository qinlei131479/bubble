package com.bubblecloud.biz.oa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.common.mybatis.mapper.UpMapper;
import com.bubblecloud.oa.api.dto.AttachImageListCondition;
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

	long countImageList(@Param("c") AttachImageListCondition c);

	List<SystemAttach> selectImageList(@Param("c") AttachImageListCondition c);

	int updateCidByIds(@Param("entid") int entid, @Param("cid") int cid, @Param("ids") List<Integer> ids);

}
