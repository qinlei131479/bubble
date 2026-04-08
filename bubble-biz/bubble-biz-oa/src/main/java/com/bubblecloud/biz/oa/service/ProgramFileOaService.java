package com.bubblecloud.biz.oa.service;

import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.entity.SystemAttach;

/**
 * 项目附件（对齐 PHP ProgramFileController + AttachService#getRelationList）。
 *
 * @author qinlei
 * @date 2026/4/8 14:00
 */
public interface ProgramFileOaService {

	ListCountVO<SystemAttach> list(int entid, long programId, String nameLike, int page, int limit);

	void delete(int id, int entid);

	void rename(int id, int entid, String realName);

}
