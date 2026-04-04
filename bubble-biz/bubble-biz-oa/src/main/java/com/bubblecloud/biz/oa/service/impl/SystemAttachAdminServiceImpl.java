package com.bubblecloud.biz.oa.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.CategoryMapper;
import com.bubblecloud.biz.oa.mapper.SystemAttachMapper;
import com.bubblecloud.biz.oa.service.SystemAttachAdminService;
import com.bubblecloud.biz.oa.service.SystemStorageService;
import com.bubblecloud.biz.oa.util.OaLocalUploadSupport;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.AttachImageListCondition;
import com.bubblecloud.oa.api.dto.AttachSaveDTO;
import com.bubblecloud.oa.api.entity.Category;
import com.bubblecloud.oa.api.entity.SystemAttach;
import com.bubblecloud.oa.api.vo.AttachFileSrcVO;
import com.bubblecloud.oa.api.vo.AttachInfoVO;
import com.bubblecloud.oa.api.vo.AttachUploadResultVO;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ObjectUtil;

/**
 * 系统附件实现（本地上传与 {@link SystemStorageService#getUploadType} 对齐）。
 *
 * @author qinlei
 * @date 2026/3/31
 */
@Service
public class SystemAttachAdminServiceImpl extends UpServiceImpl<SystemAttachMapper, SystemAttach>
		implements SystemAttachAdminService {

	private static final String TYPE_ATTACH = "systemAttach";

	private static final List<String> DEFAULT_IMAGE_EXT = Arrays.asList("jpg", "png", "gif", "jpeg", "webp");

	private final CategoryMapper categoryMapper;

	private final SystemStorageService systemStorageService;

	public SystemAttachAdminServiceImpl(CategoryMapper categoryMapper, SystemStorageService systemStorageService) {
		this.categoryMapper = categoryMapper;
		this.systemStorageService = systemStorageService;
	}

	@Override
	public ListCountVO<SystemAttach> imageList(int entid, int way, Integer cid, Integer pid, Integer upType,
			String realName, List<String> fileExt, int page, int limit) {
		int p = Math.max(page, 1);
		int sz = limit > 0 ? limit : 20;
		Integer effectiveCid = null;
		if (ObjectUtil.isNotNull(cid) && cid > 0) {
			effectiveCid = cid;
		}
		else if (ObjectUtil.isNotNull(pid) && pid > 0) {
			effectiveCid = pid;
		}
		List<String> exts = (fileExt != null && !fileExt.isEmpty()) ? fileExt : DEFAULT_IMAGE_EXT;
		AttachImageListCondition c = new AttachImageListCondition();
		c.setEntid(entid);
		c.setWay(way);
		c.setCid(effectiveCid);
		c.setUpType(upType);
		c.setNameLike(StrUtil.isBlank(realName) ? null : realName);
		c.setFileExt(exts);
		c.setOffset((long) (p - 1) * sz);
		c.setLimit(sz);
		long total = baseMapper.countImageList(c);
		List<SystemAttach> list = total == 0 ? Collections.emptyList() : baseMapper.selectImageList(c);
		return ListCountVO.of(list, total);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public AttachUploadResultVO saveRecord(AttachSaveDTO dto, int entid, String uid) {
		if (ObjectUtil.isNull(dto) || ObjectUtil.isNull(dto.getFile()) || !dto.getFile().isObject()) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		JsonNode f = dto.getFile();
		String name = text(f, "name");
		String url = text(f, "url");
		if (StrUtil.isBlank(name) || StrUtil.isBlank(url)) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		String ext = FileUtil.extName(name);
		SystemAttach a = new SystemAttach();
		a.setName(name);
		a.setRealName(name);
		a.setAttDir(url);
		a.setThumbDir(url);
		a.setAttSize(text(f, "size"));
		a.setAttType(text(f, "type"));
		a.setFileExt(ext);
		a.setUpType(systemStorageService.getUploadType());
		a.setCid(ObjectUtil.defaultIfNull(dto.getCid(), 0));
		a.setUid(StrUtil.blankToDefault(uid, ""));
		a.setWay(ObjectUtil.defaultIfNull(dto.getWay(), 2));
		a.setEntid(entid);
		int rel = relationTypeFromString(dto.getRelationType());
		a.setRelationType(rel);
		int rid = ObjectUtil.defaultIfNull(dto.getRelationId(), 0);
		if (rid < 1 && rel == 4 && StrUtil.isNotBlank(dto.getEid())) {
			try {
				rid = Integer.parseInt(dto.getEid().trim());
			}
			catch (NumberFormatException ignored) {
				rid = 0;
			}
		}
		a.setRelationId(rid);
		a.setCreatedAt(LocalDateTime.now());
		a.setUpdatedAt(LocalDateTime.now());
		baseMapper.insert(a);
		return AttachUploadResultVO.of(a.getAttDir(), a.getAttDir(), a.getId(), a.getAttSize(), a.getRealName());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteByIdsBody(JsonNode body, int entid) {
		List<Integer> ids = parseIds(body);
		if (ids.isEmpty()) {
			throw new IllegalArgumentException("请选择要删除的图片");
		}
		for (Integer id : ids) {
			SystemAttach row = baseMapper.selectById(id);
			if (row == null || row.getEntid() == null || row.getEntid() != entid) {
				continue;
			}
			if (ObjectUtil.defaultIfNull(row.getUpType(), 1) == 1) {
				String dir = row.getAttDir();
				if (StrUtil.isNotBlank(dir)) {
					OaLocalUploadSupport.deleteRelativeFileIfExists(dir.startsWith("/") ? dir.substring(1) : dir);
				}
			}
			baseMapper.deleteById(id);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void moveToCategory(int entid, int cid, String images) {
		List<Integer> ids = parseIdsFromString(images);
		if (ids.isEmpty()) {
			throw new IllegalArgumentException("移动失败或不能重复移动到同一分类下");
		}
		int n = baseMapper.updateCidByIds(entid, cid, ids);
		if (n <= 0) {
			throw new IllegalArgumentException("移动失败或不能重复移动到同一分类下");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRealNameLegacy(int attachId, int entid, String realName) {
		if (StrUtil.isBlank(realName)) {
			throw new IllegalArgumentException("文件名称不能为空");
		}
		SystemAttach row = baseMapper.selectById(attachId);
		if (row == null || row.getEntid() == null || row.getEntid() != entid) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		row.setRealName(realName);
		row.setUpdatedAt(LocalDateTime.now());
		baseMapper.updateById(row);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void realNamePut(int id, int entid, String realName) {
		if (id <= 0) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		updateRealNameLegacy(id, entid, realName);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public AttachFileSrcVO fileUpload(MultipartFile file, int attachType) throws IOException {
		int up = attachType == 1 ? systemStorageService.getUploadType() : attachType;
		if (ObjectUtil.defaultIfNull(up, 1) != 1) {
			throw new IllegalStateException("当前仅支持本地上传，请在存储配置中选择本地并保存");
		}
		String rel = OaLocalUploadSupport.saveMultipartAsSourceYm(file);
		return new AttachFileSrcVO("/" + rel);
	}

	@Override
	public ListCountVO<SystemAttach> assessCoverList(int entid, int page, int limit) {
		Category cate = categoryMapper.selectOne(Wrappers.lambdaQuery(Category.class)
			.eq(Category::getType, TYPE_ATTACH)
			.eq(Category::getKeyword, "assessCover")
			.eq(Category::getIsShow, 1)
			.last("LIMIT 1"));
		if (cate == null || cate.getId() == null) {
			return ListCountVO.of(Collections.emptyList(), 0);
		}
		int cid = cate.getId().intValue();
		return imageList(entid, 2, cid, null, null, null, null, page, limit);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Object uploadCover(int entid, String uid, int attachType, int type, MultipartFile file, Integer chunkIndex,
			Integer chunkTotal, String md5) throws IOException {
		Category cate = categoryMapper.selectOne(Wrappers.lambdaQuery(Category.class)
			.eq(Category::getType, TYPE_ATTACH)
			.eq(Category::getKeyword, "assessCover")
			.eq(Category::getIsShow, 1)
			.last("LIMIT 1"));
		int pid = cate != null && cate.getId() != null ? cate.getId().intValue() : 0;
		Optional<AttachUploadResultVO> done = doUploadMultipart(entid, pid, 2, "", 0, "", uid, attachType, type, file,
				chunkIndex, chunkTotal, md5);
		if (done.isEmpty()) {
			return Collections.emptyList();
		}
		return new AttachFileSrcVO(done.get().getSrc());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteCoverBody(JsonNode body, int entid, String uid) {
		List<Integer> ids = parseIds(body);
		if (ids.isEmpty()) {
			throw new IllegalArgumentException("请选择要删除的图片");
		}
		for (Integer id : ids) {
			SystemAttach row = baseMapper.selectById(id);
			if (row == null || row.getEntid() == null || row.getEntid() != entid) {
				continue;
			}
			if (!StrUtil.equals(uid, StrUtil.blankToDefault(row.getUid(), ""))) {
				continue;
			}
			if (ObjectUtil.defaultIfNull(row.getUpType(), 1) == 1) {
				String dir = row.getAttDir();
				if (StrUtil.isNotBlank(dir)) {
					String path = dir.startsWith("/") ? dir.substring(1) : dir;
					OaLocalUploadSupport.deleteRelativeFileIfExists(path);
				}
			}
			else if (StrUtil.isNotBlank(row.getName())) {
				OaLocalUploadSupport.deleteRelativeFileIfExists(row.getName());
			}
			baseMapper.deleteById(id);
		}
	}

	@Override
	public AttachInfoVO getAttachInfo(int id) {
		if (id <= 0) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		SystemAttach row = baseMapper.selectById(id);
		if (row == null) {
			throw new IllegalArgumentException("common.operation.noExists");
		}
		AttachInfoVO vo = new AttachInfoVO();
		vo.setId(row.getId());
		vo.setFileExt(row.getFileExt());
		vo.setFileName(row.getName());
		vo.setRealName(row.getRealName());
		vo.setFileSize(row.getAttSize());
		vo.setFileType(row.getAttType());
		String dir = StrUtil.blankToDefault(row.getAttDir(), "");
		vo.setFileUrl(dir.startsWith("/") ? dir : "/" + dir);
		return vo;
	}

	@Override
	public Object uploadMultipart(int entid, int cid, int way, String relationType, int relationId, String eidStr,
			String uid, int attachTypeParam, int type, MultipartFile file, Integer chunkIndex, Integer chunkTotal,
			String md5) throws IOException {
		Optional<AttachUploadResultVO> r = doUploadMultipart(entid, cid, way, relationType, relationId, eidStr, uid,
				attachTypeParam, type, file, chunkIndex, chunkTotal, md5);
		if (r.isPresent()) {
			return r.get();
		}
		return Collections.emptyList();
	}

	private Optional<AttachUploadResultVO> doUploadMultipart(int entid, int cid, int way, String relationType,
			int relationId, String eidStr, String uid, int attachTypeParam, int type, MultipartFile file,
			Integer chunkIndex, Integer chunkTotal, String md5) throws IOException {
		int ct = ObjectUtil.defaultIfNull(chunkTotal, 0);
		int ci = ObjectUtil.defaultIfNull(chunkIndex, 0);
		if (ct > 1 && ci < ct - 1) {
			return Optional.empty();
		}
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("common.empty.attrs");
		}
		int uploadType = attachTypeParam == 1 ? systemStorageService.getUploadType() : attachTypeParam;
		if (ObjectUtil.defaultIfNull(uploadType, 1) != 1) {
			throw new IllegalStateException("当前仅支持本地上传，请在存储配置中选择本地并保存");
		}
		String rel = OaLocalUploadSupport.saveMultipartAsAttachYm(file);
		String attPath = "/" + rel;
		if (type != 0) {
			return Optional.of(AttachUploadResultVO.of(attPath, attPath, 0, String.valueOf(file.getSize()),
					file.getOriginalFilename()));
		}
		SystemAttach a = new SystemAttach();
		String orig = file.getOriginalFilename();
		String ext = FileUtil.extName(orig);
		a.setName(orig);
		a.setRealName(orig);
		a.setAttDir(attPath);
		a.setThumbDir(attPath);
		a.setAttSize(String.valueOf(file.getSize()));
		a.setAttType(StrUtil.blankToDefault(file.getContentType(), "application/octet-stream"));
		a.setFileExt(ext);
		a.setUpType(uploadType);
		a.setCid(cid);
		a.setUid(StrUtil.blankToDefault(uid, ""));
		a.setWay(way);
		a.setEntid(entid);
		int relType = relationTypeFromString(relationType);
		a.setRelationType(relType);
		int rid = relationId;
		if (rid < 1 && relType == 4 && StrUtil.isNotBlank(eidStr)) {
			try {
				rid = Integer.parseInt(eidStr.trim());
			}
			catch (NumberFormatException ignored) {
				rid = 0;
			}
		}
		a.setRelationId(rid);
		a.setCreatedAt(LocalDateTime.now());
		a.setUpdatedAt(LocalDateTime.now());
		baseMapper.insert(a);
		return Optional
			.of(AttachUploadResultVO.of(a.getAttDir(), a.getAttDir(), a.getId(), a.getAttSize(), a.getRealName()));
	}

	private static int relationTypeFromString(String s) {
		if (StrUtil.isBlank(s)) {
			return 0;
		}
		return switch (s) {
			case "daily" -> 1;
			case "bill" -> 2;
			case "contract" -> 3;
			case "client" -> 4;
			case "follow" -> 5;
			case "invoice" -> 6;
			case "attendance_clock" -> 7;
			case "liaison" -> 8;
			case "program" -> 9;
			case "finance" -> 10;
			default -> 0;
		};
	}

	private static List<Integer> parseIds(JsonNode body) {
		if (body == null || !body.has("ids")) {
			return Collections.emptyList();
		}
		return parseIdsNode(body.get("ids"));
	}

	private static List<Integer> parseIdsNode(JsonNode n) {
		if (n == null || n.isNull()) {
			return Collections.emptyList();
		}
		if (n.isArray()) {
			List<Integer> out = new ArrayList<>();
			for (JsonNode x : n) {
				if (x.isInt()) {
					out.add(x.intValue());
				}
				else if (x.isTextual()) {
					out.addAll(parseIdsFromString(x.asText()));
				}
			}
			return out;
		}
		if (n.isTextual()) {
			return parseIdsFromString(n.asText());
		}
		if (n.isInt()) {
			return Collections.singletonList(n.intValue());
		}
		return Collections.emptyList();
	}

	private static List<Integer> parseIdsFromString(String raw) {
		if (StrUtil.isBlank(raw)) {
			return Collections.emptyList();
		}
		String[] parts = raw.split(",");
		List<Integer> out = new ArrayList<>();
		for (String p : parts) {
			try {
				out.add(Integer.parseInt(p.trim()));
			}
			catch (NumberFormatException ignored) {
			}
		}
		return out;
	}

	private static String text(JsonNode n, String field) {
		if (ObjectUtil.isNull(n) || !n.has(field) || n.get(field).isNull()) {
			return "";
		}
		return n.get(field).asText("");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(SystemAttach req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(SystemAttach req) {
		return super.update(req);
	}

}
