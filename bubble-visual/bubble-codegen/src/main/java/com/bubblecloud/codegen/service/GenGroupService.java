package com.bubblecloud.codegen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bubblecloud.codegen.entity.GenGroupEntity;
import com.bubblecloud.codegen.util.vo.GroupVO;
import com.bubblecloud.codegen.util.vo.TemplateGroupDTO;

/**
 * 模板分组服务接口
 *
 * @author qinlei
 * @date 2025/05/31
 */
public interface GenGroupService extends IService<GenGroupEntity> {

	/**
	 * 保存生成模板组
	 * @param genTemplateGroup 模板组DTO对象
	 */
	void saveGenGroup(TemplateGroupDTO genTemplateGroup);

	/**
	 * 删除分组极其关系
	 * @param ids
	 */
	void delGroupAndTemplate(Long[] ids);

	/**
	 * 查询group数据
	 * @param id
	 */
	GroupVO getGroupVoById(Long id);

	/**
	 * 更新group数据
	 * @param GroupVo
	 */
	void updateGroupAndTemplateById(GroupVO GroupVo);

}
