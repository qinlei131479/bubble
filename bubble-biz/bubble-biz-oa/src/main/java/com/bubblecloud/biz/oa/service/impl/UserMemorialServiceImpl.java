package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.constant.OaConstants;
import com.bubblecloud.biz.oa.mapper.UserMemorialCategoryMapper;
import com.bubblecloud.biz.oa.mapper.UserMemorialMapper;
import com.bubblecloud.biz.oa.service.AdminService;
import com.bubblecloud.biz.oa.service.UserMemorialService;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.UserMemorialSaveDTO;
import com.bubblecloud.oa.api.entity.Admin;
import com.bubblecloud.oa.api.entity.UserMemorial;
import com.bubblecloud.oa.api.entity.UserMemorialCategory;
import com.bubblecloud.oa.api.vo.ListCountVO;
import com.bubblecloud.oa.api.vo.memorial.UserMemorialGroupRowVO;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

/**
 * 备忘录业务实现。
 *
 * @author qinlei
 * @date 2026/4/2 10:00
 */
@Service
@RequiredArgsConstructor
public class UserMemorialServiceImpl extends UpServiceImpl<UserMemorialMapper, UserMemorial>
		implements UserMemorialService {

	private final UserMemorialCategoryMapper userMemorialCategoryMapper;

	private final AdminService adminService;

	private String requireUid(Long adminId) {
		if (ObjectUtil.isNull(adminId)) {
			throw new IllegalArgumentException("用户未登录");
		}
		Admin admin = adminService.getById(adminId);
		if (ObjectUtil.isNull(admin)) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		return admin.getUid();
	}

	@Override
	public ListCountVO<UserMemorial> listPage(Pg<UserMemorial> pg, Integer pid, String title, Long adminId) {
		String uid = requireUid(adminId);
		UserMemorial query = new UserMemorial();
		query.setUid(uid);
		resolvePidForList(query, pid, title);
		if (StrUtil.isNotBlank(title)) {
			query.setTitleSearch(title.trim());
		}
		Page<UserMemorial> res = baseMapper.findPg(pg, query);
		List<UserMemorial> records = res.getRecords();
		for (UserMemorial row : records) {
			row.setContent(HtmlUtils.htmlUnescape(row.getContent()));
		}
		return ListCountVO.of(records, res.getTotal());
	}

	private void resolvePidForList(UserMemorial query, Integer pid, String title) {
		if (StrUtil.isNotBlank(title)) {
			query.setPid(null);
			return;
		}
		if (ObjectUtil.isNull(pid) || pid == 0) {
			query.setPid(ensureDefaultCategoryId(query.getUid()).intValue());
		}
		else {
			query.setPid(pid);
		}
	}

	private Long ensureDefaultCategoryId(String uid) {
		UserMemorialCategory one = userMemorialCategoryMapper.selectOne(Wrappers.lambdaQuery(UserMemorialCategory.class)
			.eq(UserMemorialCategory::getUid, uid)
			.eq(UserMemorialCategory::getPid, 0)
			.eq(UserMemorialCategory::getTypes, 0)
			.last("LIMIT 1"));
		if (ObjectUtil.isNull(one)) {
			LocalDateTime now = LocalDateTime.now();
			UserMemorialCategory c = new UserMemorialCategory();
			c.setUid(uid);
			c.setPath("");
			c.setName("我的文件夹");
			c.setPid(0);
			c.setTypes(0);
			c.setCreatedAt(now);
			c.setUpdatedAt(now);
			userMemorialCategoryMapper.insert(c);
			return c.getId();
		}
		return one.getId();
	}

	private long resolveCategoryIdForSave(Integer pid, String uid) {
		if (ObjectUtil.isNull(pid) || pid == 0) {
			return ensureDefaultCategoryId(uid);
		}
		UserMemorialCategory c = userMemorialCategoryMapper.selectById(pid.longValue());
		if (ObjectUtil.isNull(c) || !uid.equals(c.getUid())) {
			throw new IllegalArgumentException("请选择正确的分类");
		}
		return pid.longValue();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createMemorial(UserMemorialSaveDTO dto, Long adminId) {
		String uid = requireUid(adminId);
		long pidVal = resolveCategoryIdForSave(dto.getPid(), uid);
		LocalDateTime now = LocalDateTime.now();
		UserMemorial row = new UserMemorial();
		row.setUid(uid);
		row.setTitle(StrUtil.nullToEmpty(dto.getTitle()));
		row.setContent(HtmlUtils.htmlEscape(StrUtil.nullToEmpty(dto.getContent())));
		row.setPid((int) pidVal);
		row.setCreatedAt(now);
		row.setUpdatedAt(now);
		baseMapper.insert(row);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMemorial(Long id, UserMemorialSaveDTO dto, Long adminId) {
		String uid = requireUid(adminId);
		if (ObjectUtil.isNotNull(dto.getPid()) && dto.getPid() != 0) {
			UserMemorialCategory c = userMemorialCategoryMapper.selectById(dto.getPid().longValue());
			if (ObjectUtil.isNull(c) || !uid.equals(c.getUid())) {
				throw new IllegalArgumentException("请选择正确的文件夹");
			}
		}
		UserMemorial existing = baseMapper.selectById(id);
		if (ObjectUtil.isNull(existing) || !uid.equals(existing.getUid())) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		UserMemorial row = new UserMemorial();
		row.setId(id);
		row.setUid(uid);
		row.setTitle(StrUtil.nullToEmpty(dto.getTitle()));
		row.setContent(HtmlUtils.htmlEscape(StrUtil.nullToEmpty(dto.getContent())));
		if (ObjectUtil.isNotNull(dto.getPid()) && dto.getPid() != 0) {
			row.setPid(dto.getPid());
		}
		else {
			row.setPid(existing.getPid());
		}
		String oldTitle = existing.getTitle();
		String oldContent = existing.getContent();
		String newTitle = row.getTitle();
		String newContent = row.getContent();
		if (!StrUtil.equals(oldTitle, newTitle) || !StrUtil.equals(oldContent, newContent)) {
			row.setUpdatedAt(LocalDateTime.now());
		}
		else {
			row.setUpdatedAt(existing.getUpdatedAt());
		}
		baseMapper.updateById(row);
	}

	@Override
	public ListCountVO<UserMemorialGroupRowVO> groupPage(Pg<?> pg, Integer pid, String title, Long adminId) {
		String uid = requireUid(adminId);
		Integer pidFilter = null;
		String titleSearch = StrUtil.isBlank(title) ? null : title.trim();
		if (ObjectUtil.isNotNull(pid) && pid > 0) {
			pidFilter = pid;
		}
		long total = baseMapper.countDistinctMonth(uid, pidFilter, titleSearch);
		long offset = (pg.getCurrent() - 1) * pg.getSize();
		List<String> months = baseMapper.selectMonthsPage(uid, pidFilter, titleSearch, offset, (int) pg.getSize());
		YearMonth nowYm = YearMonth.now();
		String currentMonth = nowYm.format(DateTimeFormatter.ofPattern("yyyy-MM"));
		String lastMonth = nowYm.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
		DateTimeFormatter zhFmt = DateTimeFormatter.ofPattern("yyyy年MM月");
		List<UserMemorialGroupRowVO> rows = new ArrayList<>();
		for (String ym : months) {
			List<UserMemorial> list = baseMapper.selectByUidAndMonth(uid, ym, pidFilter, titleSearch);
			for (UserMemorial m : list) {
				String plain = HtmlUtil.cleanHtmlTag(HtmlUtils.htmlUnescape(StrUtil.nullToEmpty(m.getContent())));
				m.setContent(StrUtil.sub(plain, 0, 60));
			}
			String label;
			if (currentMonth.equals(ym)) {
				label = "本月";
			}
			else if (lastMonth.equals(ym)) {
				label = "上个月";
			}
			else {
				label = YearMonth.parse(ym).format(zhFmt);
			}
			rows.add(new UserMemorialGroupRowVO(label, list));
		}
		return ListCountVO.of(rows, total);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeMemorial(Long id, Long adminId) {
		String uid = requireUid(adminId);
		UserMemorial existing = baseMapper.selectById(id);
		if (ObjectUtil.isNull(existing) || !uid.equals(existing.getUid())) {
			throw new IllegalArgumentException(OaConstants.NOT_EXISTS);
		}
		baseMapper.deleteById(id);
	}

}
