package com.bubblecloud.biz.agi.service;

/**
 * 向量化服务：将文本转换为向量（pgvector）。
 *
 * @author qinlei
 */
public interface EmbeddingService {

	/**
	 * 将输入文本向量化。
	 *
	 * @param text 输入文本（建议已做拼接与清洗）
	 * @return 向量（动态维度）
	 */
	float[] embed(String text);
}

