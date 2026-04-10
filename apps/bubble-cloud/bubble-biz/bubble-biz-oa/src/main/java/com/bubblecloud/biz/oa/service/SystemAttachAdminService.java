package com.bubblecloud.biz.oa.service;

import java.io.IOException;
import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.AttachSaveDTO;
import com.bubblecloud.oa.api.entity.SystemAttach;
import com.bubblecloud.oa.api.vo.AttachFileSrcVO;
import com.bubblecloud.oa.api.vo.AttachInfoVO;
import com.bubblecloud.oa.api.vo.AttachUploadResultVO;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统附件（eb_system_attach），对齐 PHP {@code ent/system/attach}。
 *
 * @author qinlei
 * @date 2026/3/31
 */
public interface SystemAttachAdminService extends UpService<SystemAttach> {

	ListCountVO<SystemAttach> imageList(int entid, int way, Integer cid, Integer pid, Integer upType, String realName,
			List<String> fileExt, int page, int limit);

	AttachUploadResultVO saveRecord(AttachSaveDTO dto, int entid, String uid);

	void deleteByIdsBody(JsonNode body, int entid);

	void moveToCategory(int entid, int cid, String images);

	void updateRealNameLegacy(int attachId, int entid, String realName);

	void realNamePut(int id, int entid, String realName);

	AttachFileSrcVO fileUpload(MultipartFile file, int attachType) throws IOException;

	ListCountVO<SystemAttach> assessCoverList(int entid, int page, int limit);

	Object uploadCover(int entid, String uid, int attachType, int type, MultipartFile file, Integer chunkIndex,
			Integer chunkTotal, String md5) throws IOException;

	void deleteCoverBody(JsonNode body, int entid, String uid);

	AttachInfoVO getAttachInfo(int id);

	Object uploadMultipart(int entid, int cid, int way, String relationType, int relationId, String eidStr, String uid,
			int attachTypeParam, int type, MultipartFile file, Integer chunkIndex, Integer chunkTotal, String md5)
			throws IOException;

}
