package com.bubblecloud.biz.oa.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.form.FormProtectedKeys;
import com.bubblecloud.biz.oa.form.SalesmanCustomTypeResolver;
import com.bubblecloud.biz.oa.mapper.FormCategoryMapper;
import com.bubblecloud.biz.oa.mapper.FormDataMapper;
import com.bubblecloud.biz.oa.mapper.SalesmanCustomFieldMapper;
import com.bubblecloud.biz.oa.service.FormAdminService;
import com.bubblecloud.oa.api.entity.FormCategory;
import com.bubblecloud.oa.api.entity.FormData;
import com.bubblecloud.oa.api.entity.SalesmanCustomField;
import com.bubblecloud.oa.api.vo.form.FormCateListItemVO;
import com.bubblecloud.oa.api.vo.form.FormDataItemVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 自定义表单管理实现。
 *
 * @author qinlei
 * @date 2026/3/30
 */
@Service
@RequiredArgsConstructor
public class FormAdminServiceImpl implements FormAdminService {

	private static final String LIST_SELECT = "list_select";

	private static final String SEARCH_SELECT = "search_select";

	private final FormCategoryMapper formCategoryMapper;

	private final FormDataMapper formDataMapper;

	private final SalesmanCustomFieldMapper salesmanCustomFieldMapper;

	private final ObjectMapper objectMapper;

	private final SecureRandom secureRandom = new SecureRandom();

	@Override
	public List<FormCateListItemVO> listByTypes(int types) {
		List<FormCategory> cates = formCategoryMapper.selectList(Wrappers.lambdaQuery(FormCategory.class)
			.eq(FormCategory::getTypes, types)
			.orderByDesc(FormCategory::getSort)
			.orderByAsc(FormCategory::getId));
		Set<String> protectedKeys = new HashSet<>();
		for (String k : defaultProtectedKeys(types)) {
			protectedKeys.add(k);
		}
		List<FormCateListItemVO> out = new ArrayList<>();
		for (FormCategory c : cates) {
			FormCateListItemVO vo = new FormCateListItemVO();
			vo.setId(c.getId());
			vo.setTitle(c.getTitle());
			vo.setSort(c.getSort());
			vo.setTypes(c.getTypes());
			vo.setStatus(c.getStatus());
			List<FormData> rows = formDataMapper.selectList(Wrappers.lambdaQuery(FormData.class)
				.eq(FormData::getCateId, c.getId().intValue())
				.orderByDesc(FormData::getSort));
			List<FormDataItemVO> data = new ArrayList<>();
			for (FormData fd : rows) {
				FormDataItemVO item = toItemVo(fd);
				int enable = FormProtectedKeys.isProtected(types, fd.getFieldKey()) ? 0 : 1;
				item.setEnableDelete(enable);
				if (protectedKeys.contains(fd.getFieldKey())) {
					item.setEnableDelete(0);
				}
				data.add(item);
			}
			vo.setData(data);
			out.add(vo);
		}
		return out;
	}

	private static Set<String> defaultProtectedKeys(int types) {
		return switch (types) {
			case 1 -> Set.of("customer_status");
			case 2 -> Set.of("contract_status");
			default -> Set.of();
		};
	}

	private FormDataItemVO toItemVo(FormData fd) {
		FormDataItemVO v = new FormDataItemVO();
		v.setId(fd.getId());
		v.setKey(fd.getFieldKey());
		v.setKeyName(fd.getKeyName());
		v.setType(fd.getType());
		v.setInputType(fd.getInputType());
		v.setCateId(fd.getCateId());
		v.setParam(fd.getParam());
		v.setDictIdent(fd.getDictIdent());
		v.setValue(fd.getValue());
		v.setSort(fd.getSort());
		v.setStatus(fd.getStatus());
		v.setRequired(fd.getRequired());
		v.setMax(fd.getMax());
		v.setMin(fd.getMin());
		v.setUniqued(fd.getUniqued());
		v.setDecimalPlace(fd.getDecimalPlace());
		v.setUploadType(fd.getUploadType());
		v.setPlaceholder(fd.getPlaceholder());
		return v;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public long saveCate(int types, String title, Integer sort, int status) {
		FormCategory c = new FormCategory();
		c.setTitle(title == null ? "" : title);
		c.setSort(sort == null ? 0 : sort);
		c.setTypes(types);
		c.setStatus(status);
		c.setCreatedAt(LocalDateTime.now());
		c.setUpdatedAt(LocalDateTime.now());
		formCategoryMapper.insert(c);
		return c.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCate(long id, String title, Integer sort, int status) {
		FormCategory c = new FormCategory();
		c.setId(id);
		c.setTitle(title);
		c.setSort(sort);
		c.setStatus(status);
		c.setUpdatedAt(LocalDateTime.now());
		formCategoryMapper.updateById(c);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteCate(long id) {
		formCategoryMapper.deleteById(id);
		formDataMapper.delete(Wrappers.lambdaQuery(FormData.class).eq(FormData::getCateId, (int) id));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCateStatus(long id, int status) {
		FormCategory c = new FormCategory();
		c.setId(id);
		c.setStatus(status != 0 ? 1 : 0);
		c.setUpdatedAt(LocalDateTime.now());
		formCategoryMapper.updateById(c);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveFormData(int types, JsonNode body) {
		if (body == null || !body.isArray()) {
			throw new IllegalArgumentException("data 格式错误");
		}
		List<Integer> cateIds = formCategoryMapper
			.selectList(Wrappers.lambdaQuery(FormCategory.class).eq(FormCategory::getTypes, types))
			.stream()
			.map(c -> c.getId().intValue())
			.collect(Collectors.toList());
		Set<Integer> cateIdSet = new HashSet<>(cateIds);
		for (JsonNode group : body) {
			if (!group.hasNonNull("cate_id")) {
				continue;
			}
			int cateId = group.get("cate_id").asInt();
			if (!cateIdSet.contains(cateId)) {
				throw new IllegalArgumentException("分组数据异常");
			}
			JsonNode dataArr = group.get("data");
			if (dataArr == null || !dataArr.isArray()) {
				continue;
			}
			if (dataArr.isEmpty()) {
				formDataMapper.delete(Wrappers.lambdaQuery(FormData.class).eq(FormData::getCateId, cateId));
				continue;
			}
			Map<Long, Long> remaining = formDataMapper
				.selectList(Wrappers.lambdaQuery(FormData.class).eq(FormData::getCateId, cateId))
				.stream()
				.collect(Collectors.toMap(FormData::getId, FormData::getId));
			int num = dataArr.size();
			for (JsonNode form : dataArr) {
				if (form == null || form.isNull()) {
					continue;
				}
				FormData row = patchFormDataFromJson(form, cateId, num);
				long rawId = form.hasNonNull("id") ? form.get("id").asLong() : 0L;
				if (rawId > 0 && remaining.remove(rawId) != null) {
					row.setId(rawId);
					row.setUpdatedAt(LocalDateTime.now());
					formDataMapper.updateById(row);
				}
				else {
					row.setCreatedAt(LocalDateTime.now());
					row.setUpdatedAt(LocalDateTime.now());
					formDataMapper.insert(row);
				}
				num--;
			}
			if (!remaining.isEmpty()) {
				formDataMapper.delete(Wrappers.lambdaQuery(FormData.class)
					.eq(FormData::getCateId, cateId)
					.in(FormData::getId, remaining.keySet()));
			}
		}
	}

	private FormData patchFormDataFromJson(JsonNode form, int cateId, int sortNum) {
		FormData row = new FormData();
		row.setCateId(cateId);
		String key = text(form, "key");
		if (!StringUtils.hasText(key)) {
			key = randomFieldKey();
		}
		row.setFieldKey(key);
		row.setKeyName(text(form, "key_name"));
		row.setType(text(form, "type"));
		row.setInputType(StringUtils.hasText(text(form, "input_type")) ? text(form, "input_type") : "input");
		row.setParam(text(form, "param"));
		row.setDecimalPlace(intOrZero(form, "decimal_place"));
		row.setUploadType(intOrZero(form, "upload_type"));
		row.setRequired(intOrZero(form, "required"));
		row.setPlaceholder(text(form, "placeholder"));
		row.setDictIdent(text(form, "dict_ident"));
		row.setValue(stringifyValue(form.get("value")));
		row.setUniqued(intOrZero(form, "uniqued"));
		row.setFieldDesc(text(form, "desc"));
		row.setSort(form.hasNonNull("sort") ? form.get("sort").asInt() : sortNum);
		row.setStatus(form.hasNonNull("status") ? form.get("status").asInt() : 2);
		row.setMax(form.hasNonNull("max") ? form.get("max").asInt() : 0);
		row.setMin(form.hasNonNull("min") ? form.get("min").asInt() : 0);
		return row;
	}

	private static String text(JsonNode n, String field) {
		if (n == null || !n.has(field) || n.get(field).isNull()) {
			return "";
		}
		return n.get(field).asText("");
	}

	private static int intOrZero(JsonNode n, String field) {
		if (n == null || !n.has(field) || n.get(field).isNull()) {
			return 0;
		}
		return n.get(field).asInt();
	}

	private String stringifyValue(JsonNode v) {
		if (v == null || v.isNull()) {
			return "";
		}
		if (v.isArray() || v.isObject()) {
			return v.toString();
		}
		return v.asText("");
	}

	private String randomFieldKey() {
		for (int i = 0; i < 50; i++) {
			String k = randomKey8();
			Long c = formDataMapper.selectCount(Wrappers.lambdaQuery(FormData.class).eq(FormData::getFieldKey, k));
			if (c == null || c == 0) {
				return k;
			}
		}
		return "c" + System.nanoTime();
	}

	private String randomKey8() {
		String s = "c"
				+ md5Hex((System.nanoTime() + "_" + secureRandom.nextInt(99999)).getBytes(StandardCharsets.UTF_8));
		return s.length() <= 8 ? s : s.substring(0, 8);
	}

	private static String md5Hex(byte[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] d = md.digest(input);
			StringBuilder sb = new StringBuilder();
			for (byte b : d) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void moveFormData(int types, long formDataId, int targetCateId) {
		List<Integer> cateIds = formCategoryMapper
			.selectList(Wrappers.lambdaQuery(FormCategory.class).eq(FormCategory::getTypes, types))
			.stream()
			.map(c -> c.getId().intValue())
			.toList();
		if (!cateIds.contains(targetCateId)) {
			throw new IllegalArgumentException("分组数据异常");
		}
		FormData fd = formDataMapper.selectById(formDataId);
		if (fd == null || !cateIds.contains(fd.getCateId())) {
			throw new IllegalArgumentException("移动失败");
		}
		fd.setCateId(targetCateId);
		fd.setUpdatedAt(LocalDateTime.now());
		formDataMapper.updateById(fd);
	}

	@Override
	public JsonNode getSalesmanCustomFields(long adminId, int customType) {
		int formTypes = SalesmanCustomTypeResolver.toFormTypes(customType);
		ObjectNode root = objectMapper.createObjectNode();
		ArrayNode list = root.putArray("list");
		ArrayNode search = root.putArray("search");
		if (formTypes == 0) {
			root.set("list_select", objectMapper.createArrayNode());
			root.set("search_select", objectMapper.createArrayNode());
			return root;
		}
		for (FormCateListItemVO c : listByTypes(formTypes)) {
			if (c.getData() == null) {
				continue;
			}
			for (FormDataItemVO row : c.getData()) {
				ObjectNode o = objectMapper.createObjectNode();
				o.put("field", row.getKey());
				o.put("name", row.getKeyName());
				o.put("type", row.getType() == null ? "" : row.getType());
				o.put("input_type", row.getInputType() == null ? "" : row.getInputType());
				o.put("dict_ident", row.getDictIdent() == null ? "" : row.getDictIdent());
				list.add(o);
				search.add(o.deepCopy());
			}
		}
		String listKey = customType + "_" + LIST_SELECT;
		String searchKey = customType + "_" + SEARCH_SELECT;
		root.set("list_select", readFieldListArray(adminId, listKey));
		root.set("search_select", readFieldListArray(adminId, searchKey));
		return root;
	}

	private ArrayNode readFieldListArray(long adminId, String compositeType) {
		SalesmanCustomField row = salesmanCustomFieldMapper.selectOne(Wrappers.lambdaQuery(SalesmanCustomField.class)
			.eq(SalesmanCustomField::getUid, (int) adminId)
			.eq(SalesmanCustomField::getCustomType, compositeType));
		if (row == null || !StringUtils.hasText(row.getFieldList())) {
			return objectMapper.createArrayNode();
		}
		try {
			JsonNode parsed = objectMapper.readTree(row.getFieldList());
			if (parsed.isArray()) {
				return (ArrayNode) parsed;
			}
		}
		catch (Exception ignored) {
		}
		return objectMapper.createArrayNode();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveSalesmanCustomFields(long adminId, int customType, String selectType, JsonNode dataArray) {
		if (!LIST_SELECT.equals(selectType) && !SEARCH_SELECT.equals(selectType)) {
			throw new IllegalArgumentException("业务类型错误");
		}
		if (dataArray == null || !dataArray.isArray()) {
			throw new IllegalArgumentException("data 格式错误");
		}
		Set<String> uniq = new HashSet<>();
		for (JsonNode n : dataArray) {
			if (n != null && !n.isNull()) {
				uniq.add(n.asText());
			}
		}
		if (uniq.size() < 3) {
			throw new IllegalArgumentException("至少选中3个字段");
		}
		String composite = customType + "_" + selectType;
		ArrayNode arr = objectMapper.createArrayNode();
		uniq.forEach(arr::add);
		String json = arr.toString();
		SalesmanCustomField existing = salesmanCustomFieldMapper
			.selectOne(Wrappers.lambdaQuery(SalesmanCustomField.class)
				.eq(SalesmanCustomField::getUid, (int) adminId)
				.eq(SalesmanCustomField::getCustomType, composite));
		LocalDateTime now = LocalDateTime.now();
		if (existing != null) {
			existing.setFieldList(json);
			existing.setUpdatedAt(now);
			salesmanCustomFieldMapper.updateById(existing);
		}
		else {
			SalesmanCustomField n = new SalesmanCustomField();
			n.setUid((int) adminId);
			n.setCustomType(composite);
			n.setFieldList(json);
			n.setCreatedAt(now);
			n.setUpdatedAt(now);
			salesmanCustomFieldMapper.insert(n);
		}
	}

}
