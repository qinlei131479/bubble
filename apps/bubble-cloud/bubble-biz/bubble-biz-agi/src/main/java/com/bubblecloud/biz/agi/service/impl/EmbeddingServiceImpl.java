package com.bubblecloud.biz.agi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.bubblecloud.agi.api.entity.SupplierModel;
import com.bubblecloud.biz.agi.service.EmbeddingService;
import com.bubblecloud.biz.agi.service.SupplierModelService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 使用 LangChain4j 的 OpenAI EmbeddingModel 向量化实现。
 *
 * @author qinlei
 */
@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

	private final SupplierModelService supplierModelService;

	@Override
	public float[] embed(String text) {
		if (StrUtil.isBlank(text)) {
			return null;
		}
		SupplierModel model = supplierModelService.getDefaultVectorEmbeddingModel();
		String baseUrl = normalizeBaseUrl(model.getApiDomain());
		EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
				.baseUrl(baseUrl)
				.apiKey(StrUtil.blankToDefault(model.getApiKey(), ""))
				.modelName(model.getBaseModel())
				.build();
		Response<Embedding> res = embeddingModel.embed(text);
		if (ObjectUtil.isNull(res) || ObjectUtil.isNull(res.content())) {
			throw new IllegalStateException("向量化失败：响应为空");
		}
		return res.content().vector();
	}

	private static String normalizeBaseUrl(String apiDomain) {
		if (StrUtil.isBlank(apiDomain)) {
			throw new IllegalStateException("供应商 API Domain 未配置");
		}
		String v = apiDomain.trim();
		v = StrUtil.removeSuffix(v, "/");
		if (StrUtil.endWithIgnoreCase(v, "/v1")) {
			return v;
		}
		return v + "/v1";
	}
}

