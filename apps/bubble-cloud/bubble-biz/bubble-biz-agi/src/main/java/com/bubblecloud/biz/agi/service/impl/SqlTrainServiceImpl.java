package com.bubblecloud.biz.agi.service.impl;

import cn.hutool.core.util.StrUtil;
import com.bubblecloud.agi.api.entity.Datasource;
import com.bubblecloud.biz.agi.mapper.DatasourceMapper;
import com.bubblecloud.biz.agi.service.EmbeddingService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.SqlTrain;
import com.bubblecloud.biz.agi.mapper.SqlTrainMapper;
import com.bubblecloud.biz.agi.service.SqlTrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * SQL训练示例
 *
 * @author Rampart
 * @date 2026-02-12 18:37:03
 */
@RequiredArgsConstructor
@Service
public class SqlTrainServiceImpl extends UpServiceImpl<SqlTrainMapper, SqlTrain> implements SqlTrainService {

	private final DatasourceMapper datasourceMapper;
	private final EmbeddingService embeddingService;

	@Override
	public R create(SqlTrain req) {
		Datasource datasource = datasourceMapper.selectById(req.getDsId());
		if (Objects.isNull(datasource)) {
			return R.failed("数据源不存在");
		}
		try {
			req.setEmbedding(embeddingService.embed(buildEmbedText(req)));
		} catch (Exception e) {
			return R.failed(e.getMessage());
		}
		return super.create(req);
	}

	@Override
	public R update(SqlTrain req) {
		Datasource datasource = datasourceMapper.selectById(req.getDsId());
		if (Objects.isNull(datasource)) {
			return R.failed("数据源不存在");
		}
		try {
			req.setEmbedding(embeddingService.embed(buildEmbedText(req)));
		} catch (Exception e) {
			return R.failed(e.getMessage());
		}
		return super.update(req);
	}

	private static String buildEmbedText(SqlTrain req) {
		String q = Objects.toString(req.getQuestion(), "");
		String sql = Objects.toString(req.getDescription(), "");
		return StrUtil.trim(StrUtil.join(" | ", q, sql));
	}
}