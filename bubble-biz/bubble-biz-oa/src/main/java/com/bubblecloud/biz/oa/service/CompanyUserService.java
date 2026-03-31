package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.vo.SimplePageVO;
import com.bubblecloud.oa.api.vo.company.CompanyUserCardVO;
import com.bubblecloud.oa.api.dto.EnterpriseUserCardUpdateDTO;
import com.bubblecloud.oa.api.vo.company.CompanyUserProfileVO;
import com.bubblecloud.oa.api.vo.company.UserFrameBriefVO;
import com.bubblecloud.oa.api.vo.frame.FrameDepartmentTreeNodeVO;

import java.util.List;

/**
 * 企业用户（对齐 PHP {@code CompanyUserController}）。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:30
 */
public interface CompanyUserService {

	SimplePageVO listCompanyUsers(int entid, String pid, String name, Integer status, int current, int size);

	SimplePageVO addressBook(int entid, String name, Integer status, int current, int size);

	CompanyUserProfileVO userInfo(long adminId, int entid);

	UserFrameBriefVO userFrame(long adminId, int entid);

	List<FrameDepartmentTreeNodeVO> addressBookTree(int entid, String name);

	CompanyUserCardVO getCardEdit(long targetAdminId, int entid);

	void updateCompanyUserCard(long targetAdminId, int entid, EnterpriseUserCardUpdateDTO dto);

}
