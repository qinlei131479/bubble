package com.bubblecloud.biz.agi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.DatasourceTable;
import com.bubblecloud.biz.agi.mapper.DatasourceTableMapper;
import com.bubblecloud.biz.agi.service.EmbeddingService;
import com.bubblecloud.biz.agi.service.DatasourceTableService;
import com.bubblecloud.common.core.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 数据源授权
 *
 * @author Rampart
 * @date 2026-02-13 16:47:36
 */
@Service
@RequiredArgsConstructor
public class DatasourceTableServiceImpl extends UpServiceImpl<DatasourceTableMapper, DatasourceTable> implements DatasourceTableService {

	private final EmbeddingService embeddingService;

	@Override
	public R create(DatasourceTable req) {
		try {
			req.setEmbedding(embeddingService.embed(buildEmbedText(req)));
		} catch (Exception e) {
			return R.failed(e.getMessage());
		}
		return super.create(req);
	}

	@Override
	public R update(DatasourceTable req) {
		try {
			req.setEmbedding(embeddingService.embed(buildEmbedText(req)));
		} catch (Exception e) {
			return R.failed(e.getMessage());
		}
		return super.update(req);
	}

	private static String buildEmbedText(DatasourceTable req) {
		String embed = ObjectUtil.defaultIfBlank(req.getCustomComment(), req.getTableComment());
		return StrUtil.trim(embed);
	}

}