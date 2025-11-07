package com.bubblecloud.codegen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubblecloud.codegen.entity.GenTemplateGroupEntity;
import com.bubblecloud.codegen.mapper.GenTemplateGroupMapper;
import com.bubblecloud.codegen.service.GenTemplateGroupService;
import org.springframework.stereotype.Service;

/**
 * 模板分组关联表
 *
 * @author qinlei
 * @date 2023-02-22 09:25:15
 */
@Service
public class GenTemplateGroupServiceImpl extends ServiceImpl<GenTemplateGroupMapper, GenTemplateGroupEntity>
		implements GenTemplateGroupService {

}
