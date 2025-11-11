package com.bubblecloud.biz.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bubblecloud.backend.api.entity.SysPost;
import com.bubblecloud.backend.api.vo.PostExcelVO;
import com.bubblecloud.common.core.util.R;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * 岗位信息表
 *
 * @author fxz
 * @date 2022-03-26 12:50:43
 */
public interface SysPostService extends IService<SysPost> {

	/**
	 * 获取岗位列表用于导出Excel
	 * @return 岗位Excel数据列表
	 */
	List<PostExcelVO> listPosts();

	/**
	 * 导入岗位信息
	 * @param excelVOList 岗位Excel数据列表
	 * @param bindingResult 数据校验结果
	 * @return 导入结果(R对象)
	 */
	R importPost(List<PostExcelVO> excelVOList, BindingResult bindingResult);

}
