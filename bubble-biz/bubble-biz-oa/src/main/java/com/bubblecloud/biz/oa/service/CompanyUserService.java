package com.bubblecloud.biz.oa.service;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Admin;
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
public interface CompanyUserService extends UpService<Admin> {

	SimplePageVO listCompanyUsers(Integer entid, String pid, String name, Integer status, Integer current, Integer size);

	SimplePageVO addressBook(Integer entid, String name, Integer status, Integer current, Integer size);

	CompanyUserProfileVO userInfo(Long adminId, Integer entid);

	UserFrameBriefVO userFrame(Long adminId, Integer entid);

	List<FrameDepartmentTreeNodeVO> addressBookTree(Integer entid, String name);

	CompanyUserCardVO getCardEdit(Long targetAdminId, Integer entid);

	void updateCompanyUserCard(Long targetAdminId, Integer entid, EnterpriseUserCardUpdateDTO dto);

}
