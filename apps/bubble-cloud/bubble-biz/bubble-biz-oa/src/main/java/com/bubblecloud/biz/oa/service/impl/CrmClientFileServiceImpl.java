package com.bubblecloud.biz.oa.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.SystemAttachMapper;
import com.bubblecloud.biz.oa.service.CrmClientFileService;
import com.bubblecloud.biz.oa.util.OaLocalUploadSupport;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.entity.SystemAttach;
import com.bubblecloud.oa.api.vo.AttachUploadResultVO;
import com.bubblecloud.oa.api.vo.ListCountVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 客户附件实现：本地落盘 + eb_system_attach，relation_type=4（客户）。
 *
 * @author qinlei
 * @date 2026/4/3 16:00
 */
@Service
public class CrmClientFileServiceImpl implements CrmClientFileService {

	private static final int RELATION_TYPE_CLIENT = 4;

	private final SystemAttachMapper systemAttachMapper;

	public CrmClientFileServiceImpl(SystemAttachMapper systemAttachMapper) {
		this.systemAttachMapper = systemAttachMapper;
	}

	@Override
	public ListCountVO<SystemAttach> index(int entid, Integer eid, Pg<SystemAttach> pg) {
		Integer qeid = (eid != null && eid > 0) ? eid : null;
		Page<SystemAttach> page = systemAttachMapper.selectClientRelationPage(pg, entid, qeid);
		return ListCountVO.of(page.getRecords(), page.getTotal());
	}

	@Override
	public void deleteAttach(int entid, int attachId) {
		SystemAttach row = systemAttachMapper.selectById(attachId);
		if (row == null || row.getEntid() == null || row.getEntid() != entid) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		if (StrUtil.isNotBlank(row.getAttDir())) {
			String rel = row.getAttDir().replaceFirst("^/+", "");
			OaLocalUploadSupport.deleteRelativeFileIfExists(rel);
		}
		systemAttachMapper.deleteById(attachId);
	}

	@Override
	public AttachUploadResultVO upload(int entid, int cid, int eid, int fid, MultipartFile file, String uidStr)
			throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		String orig = file.getOriginalFilename();
		String ext = FileUtil.extName(orig);
		String sub = java.time.LocalDate.now().toString().replace("-", "/");
		String name = UUID.randomUUID().toString().replace("-", "") + (StrUtil.isBlank(ext) ? "" : "." + ext);
		Path dir = OaLocalUploadSupport.uploadRoot().resolve("attach").resolve(Path.of(sub));
		Files.createDirectories(dir);
		Path target = dir.resolve(name);
		file.transferTo(target.toFile());
		String relative = "attach/" + sub + "/" + name;

		SystemAttach a = new SystemAttach();
		a.setEntid(entid);
		a.setUid(StrUtil.blankToDefault(uidStr, ""));
		a.setName(orig);
		a.setRealName(orig);
		a.setAttDir("/" + relative);
		a.setThumbDir("/" + relative);
		a.setAttSize(String.valueOf(file.getSize()));
		a.setAttType(StrUtil.blankToDefault(file.getContentType(), "application/octet-stream"));
		a.setFileExt(ext);
		a.setCid(cid);
		a.setUpType(1);
		a.setWay(2);
		a.setRelationType(RELATION_TYPE_CLIENT);
		a.setRelationId(eid > 0 ? eid : 0);
		a.setCreatedAt(LocalDateTime.now());
		a.setUpdatedAt(LocalDateTime.now());
		systemAttachMapper.insert(a);

		return AttachUploadResultVO.of(a.getAttDir(), a.getAttDir(), a.getId(), a.getAttSize(), a.getRealName());
	}

	@Override
	public void setRealName(int entid, int id, String realName) {
		SystemAttach row = systemAttachMapper.selectById(id);
		if (row == null || row.getEntid() == null || row.getEntid() != entid) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		row.setRealName(realName == null ? "" : realName);
		row.setUpdatedAt(LocalDateTime.now());
		systemAttachMapper.updateById(row);
	}

}
