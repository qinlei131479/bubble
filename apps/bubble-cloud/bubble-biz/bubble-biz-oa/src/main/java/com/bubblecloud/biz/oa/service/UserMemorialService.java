package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.UserMemorialSaveDTO;
import com.bubblecloud.oa.api.entity.UserMemorial;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.memorial.UserMemorialGroupRowVO;

/**
 * 备忘录（对齐 PHP NotepadService / eb_user_memorial）。
 *
 * @author qinlei
 * @date 2026/4/2 10:00
 */
public interface UserMemorialService extends UpService<UserMemorial> {

	/**
	 * 列表分页（PHP index / getList）。
	 * @param adminId 当前登录员工 eb_admin.id
	 */
	ListCountVO<UserMemorial> listPage(Pg<UserMemorial> pg, Integer pid, String title, Long adminId);

	/**
	 * 新增（PHP saveData）。
	 */
	void createMemorial(UserMemorialSaveDTO dto, Long adminId);

	/**
	 * 修改（PHP resourceUpdate）。
	 */
	void updateMemorial(Long id, UserMemorialSaveDTO dto, Long adminId);

	/**
	 * 按月份分组列表（PHP groupList）。
	 */
	ListCountVO<UserMemorialGroupRowVO> groupPage(Pg<?> pg, Integer pid, String title, Long adminId);

	/**
	 * 删除（校验归属用户）。
	 */
	void removeMemorial(Long id, Long adminId);

}
