package com.bubblecloud.biz.oa.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.AdminInfoMapper;
import com.bubblecloud.biz.oa.mapper.AdminMapper;
import com.bubblecloud.biz.oa.mapper.EnterpriseUserChangeMapper;
import com.bubblecloud.biz.oa.mapper.FrameAssistMapper;
import com.bubblecloud.biz.oa.mapper.FrameMapper;
import com.bubblecloud.biz.oa.mapper.RankJobMapper;
import com.bubblecloud.biz.oa.mapper.UserCardPerfectMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.CompanyCardService;
import com.bubblecloud.biz.oa.service.FrameAssistWriteService;
import com.bubblecloud.oa.api.dto.company.CompanyCardListQuery;
import com.bubblecloud.oa.api.dto.company.CompanyCardListRow;
import com.bubblecloud.oa.api.dto.frame.FrameAssistCardBatchRow;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.AdminInfo;
import com.bubblecloud.oa.api.entity.EnterpriseUserChange;
import com.bubblecloud.oa.api.entity.Frame;
import com.bubblecloud.oa.api.entity.FrameAssist;
import com.bubblecloud.oa.api.entity.RankJob;
import com.bubblecloud.oa.api.entity.UserCardPerfect;
import com.bubblecloud.oa.api.vo.company.CompanyCardFrameVO;
import com.bubblecloud.oa.api.vo.company.CompanyCardJobVO;
import com.bubblecloud.oa.api.vo.company.CompanyCardListDataVO;
import com.bubblecloud.oa.api.vo.company.CompanyCardListItemVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 员工档案业务实现。
 *
 * @author qinlei
 * @date 2026/4/5
 */
@Service
@RequiredArgsConstructor
public class CompanyCardServiceImpl implements CompanyCardService {

	private static final BCryptPasswordEncoder ENC = new BCryptPasswordEncoder();

	private static final java.util.regex.Pattern PHONE = java.util.regex.Pattern
		.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");

	private final AdminMapper adminMapper;

	private final AdminInfoMapper adminInfoMapper;

	private final AdminService adminService;

	private final FrameAssistMapper frameAssistMapper;

	private final FrameAssistWriteService frameAssistWriteService;

	private final FrameMapper frameMapper;

	private final RankJobMapper rankJobMapper;

	private final EnterpriseUserChangeMapper enterpriseUserChangeMapper;

	private final UserCardPerfectMapper userCardPerfectMapper;

	private final ObjectMapper objectMapper;

	@Override
	public CompanyCardListDataVO list(long entid, JsonNode body) {
		int page = body.path("page").asInt(1);
		int limit = body.path("limit").asInt(10);
		CompanyCardListQuery q = new CompanyCardListQuery();
		q.setEntid(entid);
		JsonNode typesNode = body.get("types");
		if (typesNode != null && typesNode.isArray()) {
			for (JsonNode n : typesNode) {
				q.getTypes().add(n.asInt());
			}
		}
		if (q.getTypes().size() == 1 && q.getTypes().get(0) == 4) {
			q.setSortMode("quit");
			q.setWorkTimeRange(false);
		}
		parseTimeToQuery(text(body, "time"), q);
		if (body.has("sex") && !body.get("sex").isNull() && StrUtil.isNotBlank(body.get("sex").asText())) {
			q.setSex(body.get("sex").asInt());
		}
		if (body.has("education") && !body.get("education").isNull()) {
			JsonNode e = body.get("education");
			q.setEducation(e.isTextual() ? e.asText() : String.valueOf(e.asInt()));
		}
		if (body.has("is_part") && !body.get("is_part").isNull() && StrUtil.isNotBlank(body.get("is_part").asText())) {
			q.setIsPart(body.get("is_part").asInt());
		}
		if (body.has("status") && !body.get("status").isNull() && StrUtil.isNotBlank(body.get("status").asText())) {
			q.setStatus(body.get("status").asInt());
		}
		if (body.has("type") && !body.get("type").isNull() && StrUtil.isNotBlank(body.get("type").asText())) {
			q.setSingleType(body.get("type").asInt());
		}
		q.setSearch(text(body, "search"));
		if (body.has("frame_id") && !body.get("frame_id").isNull()
				&& StrUtil.isNotBlank(body.get("frame_id").asText())) {
			q.setFrameId(body.get("frame_id").asInt());
		}
		q.setOffset(Math.max(0, (page - 1) * limit));
		q.setLimit(limit);
		long count = adminMapper.countCompanyCardList(q);
		List<CompanyCardListRow> rows = adminMapper.selectCompanyCardList(q);
		List<Long> userIds = rows.stream().map(CompanyCardListRow::getId).collect(Collectors.toList());
		Map<Long, List<FrameAssistCardBatchRow>> framesByUser = new HashMap<>();
		if (!userIds.isEmpty()) {
			for (FrameAssistCardBatchRow r : frameAssistMapper.selectFramesForCardBatch(entid, userIds)) {
				framesByUser.computeIfAbsent(r.getUserId(), k -> new ArrayList<>()).add(r);
			}
		}
		Set<Integer> jobIds = rows.stream()
			.map(CompanyCardListRow::getJob)
			.filter(Objects::nonNull)
			.filter(j -> j > 0)
			.collect(Collectors.toSet());
		Map<Integer, RankJob> jobMap = new HashMap<>();
		for (Integer jid : jobIds) {
			RankJob j = rankJobMapper.selectById(jid.longValue());
			if (ObjectUtil.isNotNull(j)) {
				jobMap.put(jid, j);
			}
		}
		List<CompanyCardListItemVO> list = new ArrayList<>();
		for (CompanyCardListRow row : rows) {
			CompanyCardListItemVO vo = new CompanyCardListItemVO();
			vo.setId(row.getId());
			vo.setUid(row.getUid());
			vo.setName(row.getName());
			vo.setPhone(row.getPhone());
			vo.setAvatar(row.getAvatar());
			vo.setStatus(row.getStatus());
			vo.setSex(row.getSex());
			vo.setEducation(row.getEducation());
			vo.setIsPart(row.getIsPart());
			vo.setType(row.getType());
			vo.setInterviewPosition(row.getInterviewPosition());
			vo.setWorkTime(row.getWorkTime());
			vo.setQuitTime(row.getQuitTime());
			vo.setFormalTime(row.getFormalTime());
			vo.setInterviewDate(row.getInterviewDate());
			if (row.getJob() != null && row.getJob() > 0) {
				RankJob j = jobMap.get(row.getJob());
				if (j != null) {
					CompanyCardJobVO jv = new CompanyCardJobVO();
					jv.setId(j.getId());
					jv.setName(j.getName());
					jv.setDescribe(j.getDescribe());
					vo.setJob(jv);
				}
			}
			List<FrameAssistCardBatchRow> fr = framesByUser.getOrDefault(row.getId(), List.of());
			for (FrameAssistCardBatchRow fa : fr) {
				CompanyCardFrameVO fv = new CompanyCardFrameVO();
				fv.setId(fa.getFrameId());
				fv.setName(fa.getFrameName());
				fv.setUserCount(fa.getUserCount());
				fv.setIsMastart(fa.getIsMastart());
				fv.setIsAdmin(fa.getIsAdmin());
				fv.setSuperiorUid(fa.getSuperiorUid());
				vo.getFrames().add(fv);
			}
			list.add(vo);
		}
		CompanyCardListDataVO data = new CompanyCardListDataVO();
		data.setList(list);
		data.setCount(count);
		return data;
	}

	private static void parseTimeToQuery(String timeRaw, CompanyCardListQuery q) {
		if (StrUtil.isBlank(timeRaw)) {
			return;
		}
		String t = timeRaw.replace('/', '-').trim();
		if (t.contains(" - ")) {
			String[] p = t.split("\\s+-\\s+", 2);
			q.setTimeStart(p[0].trim());
			if (p.length > 1) {
				q.setTimeEnd(p[1].trim());
			}
		}
		else {
			q.setTimeStart(t);
			q.setTimeEnd(t);
		}
	}

	private static String text(JsonNode body, String field) {
		JsonNode n = body.get(field);
		if (n == null || n.isNull()) {
			return "";
		}
		return n.asText("");
	}

	@Override
	public JsonNode cardDetail(long entid, long id) {
		assertUserInEnt(entid, id);
		Admin admin = adminService.getById(id);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("企业用户信息不存在");
		}
		AdminInfo info = adminInfoMapper.selectById(id);
		ObjectNode root = objectMapper.createObjectNode();
		putAdminFlat(root, admin);
		putInfoFlat(root, info);
		RankJob job = null;
		if (ObjectUtil.isNotNull(admin.getJob()) && admin.getJob() > 0) {
			job = rankJobMapper.selectById(admin.getJob().longValue());
		}
		ObjectNode jobNode = objectMapper.createObjectNode();
		if (job != null) {
			jobNode.put("id", job.getId());
			jobNode.put("name", StrUtil.nullToEmpty(job.getName()));
			jobNode.put("describe", StrUtil.nullToEmpty(job.getDescribe()));
		}
		root.set("job", jobNode);
		List<FrameAssistCardBatchRow> frRows = frameAssistMapper.selectFramesForCardBatch(entid, List.of(id));
		ArrayNode frames = objectMapper.createArrayNode();
		ArrayNode manageFrames = objectMapper.createArrayNode();
		Long superiorId = null;
		for (FrameAssistCardBatchRow fa : frRows) {
			ObjectNode fn = objectMapper.createObjectNode();
			fn.put("id", fa.getFrameId());
			fn.put("name", StrUtil.nullToEmpty(fa.getFrameName()));
			if (fa.getUserCount() != null) {
				fn.put("user_count", fa.getUserCount());
			}
			fn.put("is_mastart", fa.getIsMastart() != null ? fa.getIsMastart() : 0);
			fn.put("is_admin", fa.getIsAdmin() != null ? fa.getIsAdmin() : 0);
			if (fa.getSuperiorUid() != null) {
				fn.put("superior_uid", fa.getSuperiorUid());
			}
			frames.add(fn);
			if (fa.getIsMastart() != null && fa.getIsMastart() == 1 && fa.getSuperiorUid() != null
					&& fa.getSuperiorUid() > 0) {
				superiorId = fa.getSuperiorUid();
			}
			if (fa.getIsAdmin() != null && fa.getIsAdmin() == 1) {
				manageFrames.add(fn.deepCopy());
			}
		}
		root.set("frames", frames);
		root.set("manage_frames", manageFrames);
		ArrayNode manageFrameIds = objectMapper.createArrayNode();
		for (FrameAssistCardBatchRow fa : frRows) {
			if (fa.getIsAdmin() != null && fa.getIsAdmin() == 1 && fa.getFrameId() != null) {
				manageFrameIds.add(fa.getFrameId());
			}
		}
		root.put("is_admin", manageFrameIds.size() > 0 ? 1 : 0);
		root.set("manage_frame", manageFrameIds);
		if (superiorId != null) {
			Admin sup = adminService.getById(superiorId);
			ObjectNode sn = objectMapper.createObjectNode();
			if (sup != null) {
				sn.put("id", sup.getId());
				sn.put("name", StrUtil.nullToEmpty(sup.getName()));
				sn.put("avatar", StrUtil.nullToEmpty(sup.getAvatar()));
				sn.put("uid", StrUtil.nullToEmpty(sup.getUid()));
			}
			root.set("superior", sn);
			root.set("super", sn);
		}
		else {
			root.set("superior", objectMapper.createObjectNode());
			root.set("super", objectMapper.createObjectNode());
		}
		root.set("scope", objectMapper.createArrayNode());
		root.set("works", objectMapper.createArrayNode());
		root.set("educations", objectMapper.createArrayNode());
		return root;
	}

	private void putAdminFlat(ObjectNode root, Admin admin) {
		root.put("id", admin.getId());
		root.put("uid", StrUtil.nullToEmpty(admin.getUid()));
		root.put("name", StrUtil.nullToEmpty(admin.getName()));
		root.put("phone", StrUtil.nullToEmpty(admin.getPhone()));
		root.put("avatar", StrUtil.nullToEmpty(admin.getAvatar()));
		root.put("status", admin.getStatus() != null ? admin.getStatus() : 0);
		root.put("is_admin", admin.getIsAdmin() != null ? admin.getIsAdmin() : 0);
	}

	private void putInfoFlat(ObjectNode root, AdminInfo info) {
		if (info == null) {
			root.put("photo", "");
			root.put("type", 0);
			return;
		}
		root.put("letter", StrUtil.nullToEmpty(info.getLetter()));
		root.put("city", StrUtil.nullToEmpty(info.getCity()));
		root.put("area", StrUtil.nullToEmpty(info.getArea()));
		root.put("card_id", StrUtil.nullToEmpty(info.getCardId()));
		root.put("province", StrUtil.nullToEmpty(info.getProvince()));
		root.put("birthday", StrUtil.nullToEmpty(info.getBirthday()));
		root.put("nation", StrUtil.nullToEmpty(info.getNation()));
		root.put("politic", StrUtil.nullToEmpty(info.getPolitic()));
		putEducationJson(root, info.getEducation());
		root.put("education_image", StrUtil.nullToEmpty(info.getEducationImage()));
		root.put("acad", StrUtil.nullToEmpty(info.getAcad()));
		root.put("acad_image", StrUtil.nullToEmpty(info.getAcadImage()));
		root.put("native", StrUtil.nullToEmpty(info.getNativePlace()));
		root.put("address", StrUtil.nullToEmpty(info.getAddress()));
		putIntOrEmpty(root, "sex", info.getSex());
		putIntOrEmpty(root, "age", info.getAge());
		putIntOrEmpty(root, "marriage", info.getMarriage());
		putIntOrEmpty(root, "type", info.getType());
		putIntOrEmpty(root, "work_years", info.getWorkYears());
		root.put("spare_name", StrUtil.nullToEmpty(info.getSpareName()));
		root.put("spare_tel", StrUtil.nullToEmpty(info.getSpareTel()));
		root.put("email", StrUtil.nullToEmpty(info.getEmail()));
		root.put("social_num", StrUtil.nullToEmpty(info.getSocialNum()));
		root.put("fund_num", StrUtil.nullToEmpty(info.getFundNum()));
		root.put("bank_num", StrUtil.nullToEmpty(info.getBankNum()));
		root.put("bank_name", StrUtil.nullToEmpty(info.getBankName()));
		root.put("graduate_name", StrUtil.nullToEmpty(info.getGraduateName()));
		root.put("graduate_date", StrUtil.nullToEmpty(info.getGraduateDate()));
		root.put("interview_date", StrUtil.nullToEmpty(info.getInterviewDate()));
		root.put("interview_position", StrUtil.nullToEmpty(info.getInterviewPosition()));
		putIntOrEmpty(root, "is_part", info.getIsPart());
		root.put("photo", StrUtil.nullToEmpty(info.getPhoto()));
		root.put("card_front", StrUtil.nullToEmpty(info.getCardFront()));
		root.put("card_both", StrUtil.nullToEmpty(info.getCardBoth()));
		root.put("work_time", StrUtil.nullToEmpty(info.getWorkTime()));
		root.put("trial_time", StrUtil.nullToEmpty(info.getTrialTime()));
		root.put("formal_time", StrUtil.nullToEmpty(info.getFormalTime()));
		root.put("treaty_time", StrUtil.nullToEmpty(info.getTreatyTime()));
		root.put("quit_time", StrUtil.nullToEmpty(info.getQuitTime()));
		root.put("sort", info.getSort() != null ? info.getSort() : 0);
	}

	private static void putIntOrEmpty(ObjectNode root, String key, Integer v) {
		if (v == null) {
			root.putNull(key);
		}
		else {
			root.put(key, v);
		}
	}

	private static void putEducationJson(ObjectNode root, String education) {
		if (StrUtil.isBlank(education)) {
			root.putNull("education");
			return;
		}
		try {
			root.put("education", Integer.parseInt(education.trim()));
		}
		catch (NumberFormatException e) {
			root.put("education", education);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveCard(long entid, int saveType, JsonNode body) {
		String phone = text(body, "phone");
		if (StrUtil.isNotBlank(phone) && !PHONE.matcher(phone).matches()) {
			throw new IllegalArgumentException("请检查手机号是否正确");
		}
		if (StrUtil.isNotBlank(phone) && adminService.countByPhone(phone) > 0) {
			throw new IllegalArgumentException("企业存在相同记录，请勿重复操作");
		}
		List<Integer> frameIds = readIntList(body, "frame_id");
		int isAdminFlag = intOr0(body, "is_admin");
		int mainId = intOr0(body, "main_id");
		long superiorUid = longOr0(body, "superior_uid");
		List<Integer> manageFrames = readIntList(body, "manage_frames");
		if (!frameIds.isEmpty() && mainId == 0) {
			throw new IllegalArgumentException("必须选择一个主部门");
		}
		if (isAdminFlag == 1 && manageFrames.isEmpty()) {
			throw new IllegalArgumentException("必须选择一个负责部门");
		}
		if (frameIds.isEmpty()) {
			Long top = frameMapper.selectMinIdByEntid(entid);
			if (top != null) {
				frameIds = List.of(top.intValue());
			}
			manageFrames = List.of();
		}
		String rolesJson = buildRolesJson(entid, frameIds);
		Admin admin = new Admin();
		String uid = java.util.UUID.randomUUID().toString().replace("-", "");
		admin.setUid(uid);
		admin.setAccount(phone);
		admin.setPhone(phone);
		admin.setName(text(body, "name"));
		admin.setPassword(ENC.encode("888888"));
		String photo = text(body, "photo");
		admin.setAvatar(StrUtil.isNotBlank(photo) ? photo
				: String.format(OaConstants.AVATAR_BASE, 1 + (int) (Math.random() * 10)));
		admin.setRoles(rolesJson);
		admin.setJob(intOr0(body, "position"));
		admin.setStatus(saveType == 1 ? 1 : 0);
		admin.setIsAdmin(0);
		admin.setIsInit(1);
		adminService.save(admin);
		AdminInfo info = new AdminInfo();
		info.setId(admin.getId());
		info.setUid(admin.getUid());
		fillAdminInfoFromBody(info, body, saveType);
		adminInfoMapper.insert(info);
		frameAssistWriteService.setUserFrames(entid, admin.getId(), frameIds, mainId == 0 ? frameIds.get(0) : mainId,
				isAdminFlag == 1, superiorUid == 0 ? null : superiorUid, manageFrames);
		Integer t = info.getType();
		if (t != null && (t == 1 || t == 2)) {
			EnterpriseUserChange ch = new EnterpriseUserChange();
			ch.setUid(admin.getId());
			ch.setEntid(entid);
			ch.setCardId(admin.getId());
			ch.setTypes(0);
			ch.setDate(LocalDate.now());
			ch.setNewFrame(mainId == 0 ? frameIds.get(0) : mainId);
			ch.setNewPosition(admin.getJob());
			ch.setCreatedAt(LocalDateTime.now());
			ch.setUpdatedAt(LocalDateTime.now());
			enterpriseUserChangeMapper.insert(ch);
		}
	}

	private String buildRolesJson(long entid, List<Integer> frameIds) {
		if (frameIds.isEmpty()) {
			return "[]";
		}
		List<Frame> fs = frameMapper.selectList(Wrappers.lambdaQuery(Frame.class)
			.in(Frame::getId, frameIds)
			.eq(Frame::getEntid, entid)
			.isNull(Frame::getDeletedAt));
		Set<Long> roleIds = new LinkedHashSet<>();
		for (Frame f : fs) {
			if (f.getRoleId() != null && f.getRoleId() > 0) {
				roleIds.add(f.getRoleId());
			}
		}
		try {
			return objectMapper.writeValueAsString(new ArrayList<>(roleIds));
		}
		catch (Exception e) {
			return "[]";
		}
	}

	private void fillAdminInfoFromBody(AdminInfo info, JsonNode body, int saveType) {
		info.setLetter(nameLetter(text(body, "name")));
		info.setInterviewDate(text(body, "interview_date"));
		info.setInterviewPosition(text(body, "interview_position"));
		info.setCardId(text(body, "card_id"));
		info.setSex(intOrNull(body, "sex"));
		info.setBirthday(text(body, "birthday"));
		info.setAge(intOrNull(body, "age"));
		info.setNation(text(body, "nation"));
		info.setPolitic(text(body, "politic"));
		info.setWorkYears(intOrNull(body, "work_years"));
		info.setNativePlace(text(body, "native"));
		info.setAddress(text(body, "address"));
		info.setMarriage(intOrNull(body, "marriage"));
		info.setEmail(text(body, "email"));
		info.setEducation(jsonTextOrNumber(body, "education"));
		info.setAcad(text(body, "acad"));
		info.setGraduateDate(text(body, "graduate_date"));
		info.setGraduateName(text(body, "graduate_name"));
		info.setBankNum(text(body, "bank_num"));
		info.setBankName(text(body, "bank_name"));
		info.setSocialNum(text(body, "social_num"));
		info.setFundNum(text(body, "fund_num"));
		info.setSpareName(text(body, "spare_name"));
		info.setSpareTel(text(body, "spare_tel"));
		info.setCardFront(text(body, "card_front"));
		info.setCardBoth(text(body, "card_both"));
		info.setEducationImage(text(body, "education_image"));
		info.setAcadImage(text(body, "acad_image"));
		info.setWorkTime(text(body, "work_time"));
		info.setTrialTime(text(body, "trial_time"));
		info.setFormalTime(text(body, "formal_time"));
		info.setTreatyTime(text(body, "treaty_time"));
		info.setQuitTime(text(body, "quit_time"));
		info.setPhoto(text(body, "photo"));
		info.setIsPart(intOrNull(body, "is_part"));
		info.setSort(body.has("sort") ? body.get("sort").asInt(0) : 0);
		Integer type = intOrNull(body, "type");
		if (saveType == 1) {
			info.setType(type != null ? type : 2);
		}
		else if (saveType == 2) {
			info.setType(4);
		}
		else {
			info.setType(type != null ? type : 0);
		}
		LocalDateTime now = LocalDateTime.now();
		info.setCreatedAt(now);
		info.setUpdatedAt(now);
	}

	private static String nameLetter(String name) {
		if (StrUtil.isBlank(name)) {
			return "#";
		}
		char c = name.trim().charAt(0);
		if (c >= 'a' && c <= 'z') {
			return String.valueOf(Character.toUpperCase(c));
		}
		if (c >= 'A' && c <= 'Z') {
			return String.valueOf(c);
		}
		return "#";
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCard(long entid, long id, JsonNode body) {
		assertUserInEnt(entid, id);
		Admin admin = adminService.getById(id);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException("未找到相关员工信息");
		}
		String editType = text(body, "edit_type");
		if (StrUtil.isBlank(editType)) {
			editType = "all";
		}
		List<Integer> frameIds = readIntList(body, "frame_id");
		int mainId = intOr0(body, "main_id");
		int isAdminFlag = intOr0(body, "is_admin");
		long superiorUid = longOr0(body, "superior_uid");
		List<Integer> manageFrames = readIntList(body, "manage_frames");
		if (!frameIds.isEmpty() && mainId == 0) {
			throw new IllegalArgumentException("必须选择一个主部门");
		}
		if (isAdminFlag == 1 && superiorUid == 0) {
			throw new IllegalArgumentException("必须选择一个上级主管");
		}
		if (isAdminFlag == 1 && manageFrames.isEmpty()) {
			throw new IllegalArgumentException("必须选择一个负责部门");
		}
		if ("basic".equals(editType)) {
			String phone = text(body, "phone");
			if (StrUtil.isNotBlank(phone) && !phone.equals(admin.getPhone()) && adminService.countByPhone(phone) > 0) {
				throw new IllegalArgumentException("手机号码已存在");
			}
			if (StrUtil.isNotBlank(phone) && admin.getStatus() != null && admin.getStatus() == 1
					&& !phone.equals(admin.getPhone())) {
				throw new IllegalArgumentException("手机号绑定的帐号已激活，无法修改手机号!");
			}
			admin.setName(text(body, "name"));
			if (StrUtil.isNotBlank(phone)) {
				admin.setPhone(phone);
			}
			admin.setJob(intOr0(body, "position"));
			admin.setUpdatedAt(LocalDateTime.now());
			adminService.updateById(admin);
		}
		if (!"basic".equals(editType)) {
			AdminInfo info = adminInfoMapper.selectById(id);
			if (info == null) {
				info = new AdminInfo();
				info.setId(id);
				info.setUid(admin.getUid());
				fillAdminInfoFromBody(info, body, 0);
				adminInfoMapper.insert(info);
			}
			else {
				mergeAdminInfo(info, body, editType, admin);
				info.setUpdatedAt(LocalDateTime.now());
				adminInfoMapper.updateById(info);
			}
			admin.setUpdatedAt(LocalDateTime.now());
			adminService.updateById(admin);
		}
		if (!frameIds.isEmpty()) {
			frameAssistWriteService.setUserFrames(entid, id, frameIds, mainId == 0 ? frameIds.get(0) : mainId,
					isAdminFlag == 1, superiorUid == 0 ? null : superiorUid, manageFrames);
		}
	}

	private void mergeAdminInfo(AdminInfo info, JsonNode body, String editType, Admin admin) {
		switch (editType) {
			case "staff" -> {
				info.setIsPart(intOrNull(body, "is_part"));
				info.setWorkTime(text(body, "work_time"));
				info.setTrialTime(text(body, "trial_time"));
				info.setFormalTime(text(body, "formal_time"));
				info.setTreatyTime(text(body, "treaty_time"));
				info.setInterviewDate(text(body, "interview_date"));
				info.setInterviewPosition(text(body, "interview_position"));
				info.setQuitTime(text(body, "quit_time"));
				info.setType(intOrNull(body, "type"));
			}
			case "user" -> {
				info.setSex(intOrNull(body, "sex"));
				info.setBirthday(text(body, "birthday"));
				info.setAge(intOrNull(body, "age"));
				info.setNation(text(body, "nation"));
				info.setPolitic(text(body, "politic"));
				info.setWorkYears(intOrNull(body, "work_years"));
				info.setNativePlace(text(body, "native"));
				info.setAddress(text(body, "address"));
				info.setMarriage(intOrNull(body, "marriage"));
				info.setEmail(text(body, "email"));
				info.setCardId(text(body, "card_id"));
			}
			case "education" -> {
				info.setEducation(jsonTextOrNumber(body, "education"));
				info.setAcad(text(body, "acad"));
				info.setGraduateDate(text(body, "graduate_date"));
				info.setGraduateName(text(body, "graduate_name"));
			}
			case "bank" -> {
				info.setBankNum(text(body, "bank_num"));
				info.setBankName(text(body, "bank_name"));
			}
			case "social" -> {
				info.setSocialNum(text(body, "social_num"));
				info.setFundNum(text(body, "fund_num"));
			}
			case "spare" -> {
				info.setSpareName(text(body, "spare_name"));
				info.setSpareTel(text(body, "spare_tel"));
			}
			case "card" -> {
				info.setCardFront(text(body, "card_front"));
				info.setCardBoth(text(body, "card_both"));
				info.setEducationImage(text(body, "education_image"));
				info.setAcadImage(text(body, "acad_image"));
			}
			default -> {
				info.setLetter(nameLetter(text(body, "name")));
				info.setWorkTime(text(body, "work_time"));
				info.setTrialTime(text(body, "trial_time"));
				info.setFormalTime(text(body, "formal_time"));
				info.setTreatyTime(text(body, "treaty_time"));
				info.setInterviewPosition(text(body, "position"));
				info.setCardId(text(body, "card_id"));
				info.setSex(intOrNull(body, "sex"));
				info.setBirthday(text(body, "birthday"));
				info.setAge(intOrNull(body, "age"));
				info.setNation(text(body, "nation"));
				info.setPolitic(text(body, "politic"));
				info.setWorkYears(intOrNull(body, "work_years"));
				info.setNativePlace(text(body, "native"));
				info.setAddress(text(body, "address"));
				info.setMarriage(intOrNull(body, "marriage"));
				info.setEmail(text(body, "email"));
				info.setEducation(jsonTextOrNumber(body, "education"));
				info.setAcad(text(body, "acad"));
				info.setGraduateDate(text(body, "graduate_date"));
				info.setGraduateName(text(body, "graduate_name"));
				info.setBankNum(text(body, "bank_num"));
				info.setBankName(text(body, "bank_name"));
				info.setSocialNum(text(body, "social_num"));
				info.setFundNum(text(body, "fund_num"));
				info.setSpareName(text(body, "spare_name"));
				info.setSpareTel(text(body, "spare_tel"));
				info.setCardFront(text(body, "card_front"));
				info.setCardBoth(text(body, "card_both"));
				info.setEducationImage(text(body, "education_image"));
				info.setAcadImage(text(body, "acad_image"));
				info.setInterviewDate(text(body, "interview_date"));
				info.setInterviewPosition(text(body, "interview_position"));
				info.setQuitTime(text(body, "quit_time"));
				info.setType(intOrNull(body, "type"));
				info.setIsPart(intOrNull(body, "is_part"));
				info.setPhoto(text(body, "photo"));
				if (body.has("sort")) {
					info.setSort(body.get("sort").asInt(0));
				}
				admin.setName(text(body, "name"));
				String ph = text(body, "phone");
				if (StrUtil.isNotBlank(ph)) {
					admin.setPhone(ph);
				}
				admin.setJob(intOr0(body, "position"));
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void entry(long entid, long id) {
		assertUserInEnt(entid, id);
		AdminInfo info = adminInfoMapper.selectById(id);
		if (info == null) {
			throw new IllegalArgumentException("修改的用户名片不存在");
		}
		if (info.getType() != null && info.getType() == 2) {
			throw new IllegalArgumentException("该员工已入职");
		}
		LocalDate today = LocalDate.now();
		info.setWorkTime(today.toString());
		if (info.getType() == null || info.getType() == 0 || info.getType() == 4) {
			info.setType(2);
		}
		info.setUpdatedAt(LocalDateTime.now());
		adminInfoMapper.updateById(info);
		Admin a = adminService.getById(id);
		if (a != null) {
			a.setStatus(1);
			a.setUpdatedAt(LocalDateTime.now());
			adminService.updateById(a);
		}
		EnterpriseUserChange ch = new EnterpriseUserChange();
		ch.setUid(id);
		ch.setEntid(entid);
		ch.setCardId(id);
		ch.setTypes(0);
		ch.setDate(today);
		ch.setNewPosition(a != null && a.getJob() != null ? a.getJob() : 0);
		Integer mf = masterFrameId(entid, id);
		ch.setNewFrame(mf != null ? mf : 0);
		ch.setCreatedAt(LocalDateTime.now());
		ch.setUpdatedAt(LocalDateTime.now());
		enterpriseUserChangeMapper.insert(ch);
		frameAssistMapper.update(null,
				Wrappers.lambdaUpdate(FrameAssist.class)
					.set(FrameAssist::getDeletedAt, null)
					.eq(FrameAssist::getUserId, id)
					.isNotNull(FrameAssist::getDeletedAt));
	}

	private Integer masterFrameId(long entid, long userId) {
		FrameAssist fa = frameAssistMapper.selectOne(Wrappers.lambdaQuery(FrameAssist.class)
			.eq(FrameAssist::getEntid, entid)
			.eq(FrameAssist::getUserId, userId)
			.eq(FrameAssist::getIsMastart, 1)
			.orderByDesc(FrameAssist::getId)
			.last("LIMIT 1"));
		return fa == null ? null : fa.getFrameId();
	}

	@Override
	public JsonNode formalForm(long entid, long id) {
		assertUserInEnt(entid, id);
		Admin admin = adminService.getById(id);
		if (admin == null) {
			throw new IllegalArgumentException("修改的用户名片不存在");
		}
		AdminInfo info = adminInfoMapper.selectById(id);
		String formalTime = info != null && StrUtil.isNotBlank(info.getFormalTime()) ? info.getFormalTime()
				: LocalDate.now().toString();
		ObjectNode root = objectMapper.createObjectNode();
		root.put("title", "办理转正");
		root.put("method", "PUT");
		root.put("action", "/ent/company/card/be_formal/" + id);
		ArrayNode rules = objectMapper.createArrayNode();
		rules.add(formRuleInput("name", "人员姓名", admin.getName(), true));
		rules.add(formRuleDate("formal_time", "转正时间", formalTime));
		rules.add(formRuleTextarea("mark", "转正备注", ""));
		root.set("rule", rules);
		ObjectNode config = objectMapper.createObjectNode();
		root.set("config", config);
		return root;
	}

	private ObjectNode formRuleInput(String field, String title, String value, boolean disabled) {
		ObjectNode r = objectMapper.createObjectNode();
		r.put("type", "input");
		r.put("field", field);
		r.put("title", title);
		r.put("value", StrUtil.nullToEmpty(value));
		ObjectNode props = objectMapper.createObjectNode();
		props.put("disabled", disabled);
		r.set("props", props);
		return r;
	}

	private ObjectNode formRuleDate(String field, String title, String value) {
		ObjectNode r = objectMapper.createObjectNode();
		r.put("type", "datePicker");
		r.put("field", field);
		r.put("title", title);
		r.put("value", value);
		ObjectNode props = objectMapper.createObjectNode();
		props.put("type", "date");
		props.put("valueFormat", "yyyy-MM-dd");
		r.set("props", props);
		return r;
	}

	private ObjectNode formRuleTextarea(String field, String title, String value) {
		ObjectNode r = objectMapper.createObjectNode();
		r.put("type", "input");
		r.put("field", field);
		r.put("title", title);
		r.put("value", value);
		ObjectNode props = objectMapper.createObjectNode();
		props.put("type", "textarea");
		props.put("rows", 3);
		r.set("props", props);
		return r;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void beFormal(long entid, long id, JsonNode body) {
		assertUserInEnt(entid, id);
		AdminInfo info = adminInfoMapper.selectById(id);
		if (info == null) {
			throw new IllegalArgumentException("修改的用户名片不存在");
		}
		if (info.getType() != null && info.getType() == 1) {
			throw new IllegalArgumentException("已转正，请勿重复操作");
		}
		String formalTime = text(body, "formal_time");
		if (StrUtil.isBlank(formalTime)) {
			throw new IllegalArgumentException("请选择转正时间");
		}
		info.setFormalTime(formalTime);
		info.setType(1);
		info.setUpdatedAt(LocalDateTime.now());
		adminInfoMapper.updateById(info);
		Admin a = adminService.getById(id);
		EnterpriseUserChange ch = new EnterpriseUserChange();
		ch.setUid(id);
		ch.setEntid(entid);
		ch.setCardId(id);
		ch.setTypes(1);
		ch.setDate(LocalDate.parse(formalTime));
		ch.setOldPosition(a != null && a.getJob() != null ? a.getJob() : 0);
		ch.setOldFrame(masterFrameId(entid, id) != null ? masterFrameId(entid, id) : 0);
		ch.setMark(text(body, "mark"));
		ch.setCreatedAt(LocalDateTime.now());
		ch.setUpdatedAt(LocalDateTime.now());
		enterpriseUserChangeMapper.insert(ch);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void quit(long entid, long id, JsonNode body) {
		assertUserInEnt(entid, id);
		Admin admin = adminService.getById(id);
		if (admin == null) {
			throw new IllegalArgumentException("未找到相关用户信息");
		}
		if (admin.getIsAdmin() != null && admin.getIsAdmin() == 1) {
			throw new IllegalArgumentException("企业创始人不能离职！");
		}
		long handover = longOr0(body, "user_id");
		if (handover <= 0) {
			throw new IllegalArgumentException("未找到交接人信息");
		}
		AdminInfo info = adminInfoMapper.selectById(id);
		if (info == null) {
			info = new AdminInfo();
			info.setId(id);
			info.setUid(admin.getUid());
			adminInfoMapper.insert(info);
		}
		String quitTime = text(body, "quit_time");
		if (StrUtil.isBlank(quitTime)) {
			throw new IllegalArgumentException("请选择离职时间");
		}
		info.setType(4);
		info.setQuitTime(quitTime);
		info.setUpdatedAt(LocalDateTime.now());
		adminInfoMapper.updateById(info);
		admin.setStatus(2);
		admin.setUpdatedAt(LocalDateTime.now());
		adminService.updateById(admin);
		EnterpriseUserChange ch = new EnterpriseUserChange();
		ch.setUid(id);
		ch.setEntid(entid);
		ch.setCardId(id);
		ch.setTypes(3);
		ch.setDate(LocalDate.parse(quitTime.substring(0, Math.min(10, quitTime.length()))));
		ch.setOldPosition(admin.getJob() != null ? admin.getJob() : 0);
		ch.setOldFrame(masterFrameId(entid, id) != null ? masterFrameId(entid, id) : 0);
		ch.setInfo(text(body, "info"));
		ch.setMark(text(body, "mark"));
		ch.setUserId((int) handover);
		ch.setCreatedAt(LocalDateTime.now());
		ch.setUpdatedAt(LocalDateTime.now());
		enterpriseUserChangeMapper.insert(ch);
		LocalDateTime now = LocalDateTime.now();
		frameAssistMapper.update(null,
				Wrappers.lambdaUpdate(FrameAssist.class)
					.set(FrameAssist::getIsAdmin, 0)
					.eq(FrameAssist::getUserId, id));
		frameAssistMapper.update(null,
				Wrappers.lambdaUpdate(FrameAssist.class)
					.set(FrameAssist::getDeletedAt, now)
					.eq(FrameAssist::getUserId, id));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteCard(long entid, long id) {
		assertUserInEnt(entid, id);
		Admin admin = adminService.getById(id);
		if (admin == null) {
			throw new IllegalArgumentException("删除的用户名片不存在");
		}
		if (admin.getStatus() != null && admin.getStatus() != 0) {
			throw new IllegalArgumentException("已激活的用户不可删除");
		}
		if (admin.getIsAdmin() != null && admin.getIsAdmin() == 1) {
			throw new IllegalArgumentException("不能删除企业的创始人");
		}
		LocalDateTime now = LocalDateTime.now();
		frameAssistMapper.update(null,
				Wrappers.lambdaUpdate(FrameAssist.class)
					.set(FrameAssist::getDeletedAt, now)
					.eq(FrameAssist::getUserId, id));
		adminInfoMapper.deleteById(id);
		admin.setDeletedAt(now);
		admin.setUpdatedAt(now);
		adminService.updateById(admin);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatch(long entid, List<Long> ids) {
		for (Long id : ids) {
			deleteCard(entid, id);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSetFrame(long entid, List<Integer> frameIds, List<Long> userIds, int mastartId) {
		LocalDateTime now = LocalDateTime.now();
		for (Long userId : userIds) {
			List<Integer> existing = frameAssistMapper
				.selectList(Wrappers.lambdaQuery(FrameAssist.class)
					.eq(FrameAssist::getEntid, entid)
					.eq(FrameAssist::getUserId, userId)
					.isNull(FrameAssist::getDeletedAt))
				.stream()
				.map(FrameAssist::getFrameId)
				.collect(Collectors.toList());
			for (Integer fid : frameIds) {
				if (!existing.contains(fid)) {
					if (mastartId == fid) {
						frameAssistMapper.update(null,
								Wrappers.lambdaUpdate(FrameAssist.class)
									.set(FrameAssist::getIsMastart, 0)
									.eq(FrameAssist::getEntid, entid)
									.eq(FrameAssist::getUserId, userId)
									.isNull(FrameAssist::getDeletedAt));
					}
					FrameAssist fa = new FrameAssist();
					fa.setEntid(entid);
					fa.setFrameId(fid);
					fa.setUserId(userId);
					fa.setIsMastart(mastartId == fid ? 1 : 0);
					fa.setIsAdmin(0);
					fa.setSuperiorUid(0L);
					fa.setCreatedAt(now);
					fa.setUpdatedAt(now);
					frameAssistMapper.insert(fa);
				}
			}
		}
	}

	@Override
	public Map<String, Object> importTemplate() {
		return Map.of("url", "/static/temp/card_import_temp.xlsx");
	}

	@Override
	public String importCards(long entid, JsonNode body) {
		JsonNode data = body.get("data");
		if (data == null || !data.isArray() || data.size() == 0) {
			throw new IllegalArgumentException("未获取到导入数据");
		}
		int succ = 0;
		int err = 0;
		int type = body.path("type").asInt(0);
		for (JsonNode row : data) {
			try {
				saveCard(entid, type, row);
				succ++;
			}
			catch (Exception e) {
				err++;
			}
		}
		return "员工导入结果，成功：" + succ + "条,失败：" + err + "条.";
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> sendPerfect(long entid, long creatorAdminId, long cardId) {
		Admin admin = adminService.getById(cardId);
		if (admin == null) {
			throw new IllegalArgumentException("未找到相关员工档案信息");
		}
		assertUserInEnt(entid, cardId);
		long todayCount = userCardPerfectMapper.selectCount(Wrappers.lambdaQuery(UserCardPerfect.class)
			.eq(UserCardPerfect::getUserId, (int) cardId)
			.apply("DATE(created_at) = CURDATE()"));
		if (todayCount > 0) {
			throw new IllegalArgumentException("每日仅可发送一次邀请！");
		}
		String uniqued = md5Hex("_perfect_" + cardId + System.currentTimeMillis());
		UserCardPerfect p = new UserCardPerfect();
		p.setCreator((int) creatorAdminId);
		p.setUserId((int) cardId);
		p.setEntid(entid);
		p.setUid(StrUtil.nullToEmpty(admin.getUid()));
		p.setCardId((int) cardId);
		p.setUniqued(uniqued);
		p.setTotal(1);
		p.setTypes(1);
		p.setStatus(0);
		p.setUsed(0);
		p.setFailTime(LocalDateTime.now().plusDays(8));
		p.setCreatedAt(LocalDateTime.now());
		p.setUpdatedAt(LocalDateTime.now());
		userCardPerfectMapper.insert(p);
		// 前端读取 result.data.url.url
		Map<String, Object> inner = new LinkedHashMap<>();
		inner.put("url", "");
		Map<String, Object> outer = new LinkedHashMap<>();
		outer.put("url", inner);
		outer.put("message", "邀请完善信息已发出，等待用户完善");
		return outer;
	}

	@Override
	public Map<String, Object> sendInterview(long entid) {
		Map<String, Object> inner = new LinkedHashMap<>();
		inner.put("url", "");
		Map<String, Object> outer = new LinkedHashMap<>();
		outer.put("url", inner);
		return outer;
	}

	@Override
	public List<Map<String, Object>> changeList(long entid, Long cardIdFilter) {
		var q = Wrappers.lambdaQuery(EnterpriseUserChange.class).eq(EnterpriseUserChange::getEntid, entid);
		if (cardIdFilter != null && cardIdFilter > 0) {
			q.eq(EnterpriseUserChange::getCardId, cardIdFilter);
		}
		q.orderByDesc(EnterpriseUserChange::getUpdatedAt);
		List<EnterpriseUserChange> rows = enterpriseUserChangeMapper.selectList(q);
		List<Map<String, Object>> out = new ArrayList<>();
		for (EnterpriseUserChange c : rows) {
			Map<String, Object> item = new LinkedHashMap<>();
			item.put("id", c.getId());
			item.put("types", c.getTypes());
			item.put("date", c.getDate() != null ? c.getDate().toString() : "");
			item.put("created_at", c.getCreatedAt());
			item.put("info", StrUtil.nullToEmpty(c.getInfo()));
			item.put("mark", StrUtil.nullToEmpty(c.getMark()));
			item.put("o_frame", frameNameNode(c.getOldFrame()));
			item.put("n_frame", frameNameNode(c.getNewFrame()));
			item.put("o_position", jobNameNode(c.getOldPosition()));
			item.put("n_position", jobNameNode(c.getNewPosition()));
			out.add(item);
		}
		return out;
	}

	private Map<String, String> frameNameNode(Integer frameId) {
		if (frameId == null || frameId == 0) {
			return Map.of("name", "");
		}
		Frame f = frameMapper.selectById(frameId.longValue());
		return Map.of("name", f != null ? StrUtil.nullToEmpty(f.getName()) : "");
	}

	private Map<String, String> jobNameNode(Integer jobId) {
		if (jobId == null || jobId == 0) {
			return Map.of("name", "");
		}
		RankJob j = rankJobMapper.selectById(jobId.longValue());
		return Map.of("name", j != null ? StrUtil.nullToEmpty(j.getName()) : "");
	}

	private void assertUserInEnt(long entid, long adminId) {
		long n = frameAssistMapper.selectCount(Wrappers.lambdaQuery(FrameAssist.class)
			.eq(FrameAssist::getEntid, entid)
			.eq(FrameAssist::getUserId, adminId)
			.isNull(FrameAssist::getDeletedAt));
		if (n == 0) {
			throw new IllegalArgumentException("用户不在当前企业");
		}
	}

	private static String md5Hex(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : d) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		}
		catch (Exception e) {
			return String.valueOf(s.hashCode());
		}
	}

	private static String jsonTextOrNumber(JsonNode body, String field) {
		JsonNode n = body.get(field);
		if (n == null || n.isNull()) {
			return "";
		}
		if (n.isNumber()) {
			return String.valueOf(n.asInt());
		}
		return n.asText("");
	}

	private static List<Integer> readIntList(JsonNode body, String key) {
		List<Integer> list = new ArrayList<>();
		JsonNode n = body.get(key);
		if (n == null || !n.isArray()) {
			return list;
		}
		for (JsonNode x : n) {
			if (x.isInt() || x.isLong()) {
				list.add(x.asInt());
			}
			else if (x.isTextual() && StrUtil.isNotBlank(x.asText())) {
				list.add(Integer.parseInt(x.asText().trim()));
			}
		}
		return list;
	}

	private static int intOr0(JsonNode body, String key) {
		JsonNode n = body.get(key);
		if (n == null || n.isNull()) {
			return 0;
		}
		if (n.isArray() && n.size() > 0) {
			return n.get(n.size() - 1).asInt(0);
		}
		if (n.isInt() || n.isLong()) {
			return n.asInt();
		}
		String t = n.asText("");
		if (StrUtil.isBlank(t)) {
			return 0;
		}
		try {
			return Integer.parseInt(t.trim());
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}

	private static long longOr0(JsonNode body, String key) {
		JsonNode n = body.get(key);
		if (n == null || n.isNull()) {
			return 0L;
		}
		if (n.isInt() || n.isLong()) {
			return n.asLong();
		}
		String t = n.asText("");
		if (StrUtil.isBlank(t)) {
			return 0L;
		}
		try {
			return Long.parseLong(t.trim());
		}
		catch (NumberFormatException e) {
			return 0L;
		}
	}

	private static Integer intOrNull(JsonNode body, String key) {
		JsonNode n = body.get(key);
		if (n == null || n.isNull()) {
			return null;
		}
		if (n.isInt() || n.isLong()) {
			return n.asInt();
		}
		String t = n.asText("");
		if (StrUtil.isBlank(t)) {
			return null;
		}
		try {
			return Integer.parseInt(t.trim());
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

}
