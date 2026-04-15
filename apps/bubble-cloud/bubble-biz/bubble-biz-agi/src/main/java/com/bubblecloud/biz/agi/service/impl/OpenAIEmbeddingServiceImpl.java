package com.bubblecloud.biz.agi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.bubblecloud.agi.api.entity.Supplier;
import com.bubblecloud.agi.api.entity.SupplierModel;
import com.bubblecloud.biz.agi.mapper.SupplierMapper;
import com.bubblecloud.biz.agi.mapper.SupplierModelMapper;
import com.bubblecloud.biz.agi.service.EmbeddingService;
import com.bubblecloud.common.core.constant.enums.FlagEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * 使用 OpenAI 兼容 embeddings 协议的向量化实现（POST /v1/embeddings）。
 *
 * @author qinlei
 */
@Service
@RequiredArgsConstructor
public class OpenAIEmbeddingServiceImpl implements EmbeddingService {

	private static final String MODEL_TYPE_VECTOR = "3";

	private final SupplierModelMapper supplierModelMapper;
	private final SupplierMapper supplierMapper;
	private final ObjectMapper objectMapper;

	// 直接用 RestTemplate（Spring Web 已在项目中引入）
	private final RestTemplate restTemplate = new RestTemplate();

	@Override
	public float[] embed(String text) {
		if (StrUtil.isBlank(text)) {
			return null;
		}
		SupplierModel model = supplierModelMapper.selectOne(
				com.baomidou.mybatisplus.core.toolkit.Wrappers.<SupplierModel>lambdaQuery()
						.eq(SupplierModel::getModelType, MODEL_TYPE_VECTOR)
						.eq(SupplierModel::getDefaultFlag, FlagEnum.YES.getCode())
						.last("limit 1")
		);
		if (Objects.isNull(model)) {
			throw new IllegalStateException("未配置默认向量模型（supplier_model.model_type=3 且 default_flag=1）");
		}
		Supplier supplier = supplierMapper.selectById(model.getSupplierId());
		if (Objects.isNull(supplier)) {
			throw new IllegalStateException("向量模型所属供应商不存在");
		}
		if (StrUtil.isBlank(supplier.getApiDomain())) {
			throw new IllegalStateException("供应商 API Domain 未配置");
		}

		String baseUrl = supplier.getApiDomain().trim();
		String endpoint = baseUrl.endsWith("/") ? baseUrl + "v1/embeddings" : baseUrl + "/v1/embeddings";

		EmbeddingRequest req = new EmbeddingRequest();
		req.setModel(model.getBaseModel());
		req.setInput(text);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (StrUtil.isNotBlank(supplier.getApiKey())) {
			headers.setBearerAuth(supplier.getApiKey().trim());
		}

		try {
			ResponseEntity<String> res = restTemplate.exchange(
					URI.create(endpoint),
					HttpMethod.POST,
					new HttpEntity<>(objectMapper.writeValueAsString(req), headers),
					String.class
			);
			if (!res.getStatusCode().is2xxSuccessful() || StrUtil.isBlank(res.getBody())) {
				throw new IllegalStateException("向量化失败，HTTP=" + res.getStatusCode());
			}
			EmbeddingResponse body = objectMapper.readValue(res.getBody(), EmbeddingResponse.class);
			if (ObjectUtil.isNull(body) || CollUtil.isEmpty(body.getData())) {
				throw new IllegalStateException("向量化失败：响应 data 为空");
			}
			List<Double> vec = body.getData().get(0).getEmbedding();
			if (CollUtil.isEmpty(vec)) {
				throw new IllegalStateException("向量化失败：embedding 为空");
			}
			float[] out = new float[vec.size()];
			for (int i = 0; i < vec.size(); i++) {
				out[i] = vec.get(i).floatValue();
			}
			return out;
		} catch (RestClientException e) {
			throw new IllegalStateException("向量化调用失败：" + e.getMessage(), e);
		} catch (Exception e) {
			throw new IllegalStateException("向量化解析失败：" + e.getMessage(), e);
		}
	}

	@Data
	private static class EmbeddingRequest {
		private String model;
		private String input;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class EmbeddingResponse {
		private List<EmbeddingData> data;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class EmbeddingData {
		@JsonProperty("embedding")
		private List<Double> embedding;
	}
}

