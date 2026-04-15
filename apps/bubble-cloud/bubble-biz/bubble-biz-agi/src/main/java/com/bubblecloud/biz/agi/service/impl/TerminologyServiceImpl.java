package com.bubblecloud.biz.agi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.agi.api.entity.Datasource;
import com.bubblecloud.biz.agi.mapper.DatasourceMapper;
import com.bubblecloud.common.core.util.HuToolUtil;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.biz.agi.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.Terminology;
import com.bubblecloud.biz.agi.mapper.TerminologyMapper;
import com.bubblecloud.biz.agi.service.TerminologyService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * service实现类：术语表
 *
 * @author Rampart Qin
 * @date 2026/02/11 22:35
 */
@RequiredArgsConstructor
@Service
public class TerminologyServiceImpl extends UpServiceImpl<TerminologyMapper, Terminology> implements TerminologyService {

	private final DatasourceMapper datasourceMapper;
	private final EmbeddingService embeddingService;

	@Override
	public Page<Terminology> findPg(Page page, Terminology req) {
		Page<Terminology> res = super.findPg(page, req);
		if (res.getTotal() > 0) {
			res.getRecords().forEach(item -> this.fill(item));
		}
		return res;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(Terminology req) {
		List<Long> dsIds = getDsIds(req.getDatasourceIds());
		List<Datasource> datasource = getDs(dsIds);
		if (dsIds.size() != datasource.size()) {
			return R.failed("设置数据源失败，部分数据源不存在");
		}
		try {
			req.setEmbedding(embeddingService.embed(buildEmbedText(req)));
		} catch (Exception e) {
			return R.failed(e.getMessage());
		}
		R res = super.create(req);
		updateWords(req, false);
		return res;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(Terminology req) {
		List<Long> dsIds = getDsIds(req.getDatasourceIds());
		List<Datasource> datasource = getDs(dsIds);
		if (dsIds.size() != datasource.size()) {
			return R.failed("设置数据源失败，部分数据源不存在");
		}
		try {
			req.setEmbedding(embeddingService.embed(buildEmbedText(req)));
		} catch (Exception e) {
			return R.failed(e.getMessage());
		}
		R res = super.update(req);
		updateWords(req, true);
		return res;
	}

	@Override
	public Terminology fill(Terminology item) {
		if (Objects.nonNull(item)) {
			List<Datasource> datasource = getDs(getDsIds(item.getDatasourceIds()));
			item.setDatasourceNames(datasource.stream().map(Datasource::getName).collect(Collectors.joining(StrUtil.COMMA)));
			Set<String> words = this.baseMapper.selectList(Wrappers.lambdaQuery(Terminology.class).eq(Terminology::getParentId, item.getId()))
					.stream().map(Terminology::getWord).collect(Collectors.toSet());
			item.setWords(words);
		}
		return item;
	}

	/**
	 * 更新术语（同义词）
	 *
	 * @param req
	 * @param isUpdate
	 */
	private void updateWords(Terminology req, boolean isUpdate) {
		if (isUpdate) {
			this.baseMapper.delete(Wrappers.lambdaQuery(Terminology.class).eq(Terminology::getParentId, req.getId()));
		}
		if (ObjectUtil.isNotEmpty(req.getWords())) {
			req.getWords().forEach(word -> {
				Terminology terminology = new Terminology();
				terminology.setParentId(req.getId());
				terminology.setWord(word);
				terminology.setSpecificDs(req.getSpecificDs());
				terminology.setDatasourceIds(req.getDatasourceIds());
				terminology.setEnabledFlag(req.getEnabledFlag());
				super.create(terminology);
			});
		}
	}

	/**
	 * 获取数据源ID列表
	 *
	 * @param datasourceIds
	 * @return
	 */
	private List<Long> getDsIds(String datasourceIds) {
		if (StrUtil.isEmpty(datasourceIds)) {
			return List.of();
		}
		return StrUtil.split(datasourceIds, StrUtil.C_COMMA).stream().map(Long::valueOf).toList();
	}

	/**
	 * 获取数据源列表
	 *
	 * @param dsIds
	 * @return
	 */
	private List<Datasource> getDs(List<Long> dsIds) {
		return CollUtil.isNotEmpty(dsIds) ? datasourceMapper.selectByIds(dsIds) : List.of();
	}

	private static String buildEmbedText(Terminology req) {
		if (CollUtil.isEmpty(req.getWords())) {
			req.setWords(Set.of());
		}
		req.getWords().add(StrUtil.trim(req.getWord()));
		return StrUtil.trim(StrUtil.join(" | ", req.getWords()));
	}

}
