package com.bubblecloud.biz.oa.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.SystemAttach;
import com.bubblecloud.oa.api.vo.ListCountVO;

/**
 * 客户附件（对齐 PHP {@code ent/client/file}，底层 {@code eb_system_attach}）。
 *
 * @author qinlei
 * @date 2026/4/3 16:00
 */
public interface CrmClientFileService {

	ListCountVO<SystemAttach> index(int entid, Integer eid, Pg<SystemAttach> pg);

	void deleteAttach(int entid, int attachId);

	Map<String, Object> upload(int entid, int cid, int eid, int fid, MultipartFile file, String uidStr) throws IOException;

	void setRealName(int entid, int id, String realName);

}
