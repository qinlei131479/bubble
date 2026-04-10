package com.bubblecloud.codegen.service;

import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成服务接口
 *
 * @author qinlei
 * @date 2025/05/31
 */
public interface GeneratorService {

	/**
	 * 生成代码zip写出
	 * @param tableId 表
	 * @param zip 输出流
	 */
	void downloadCode(Long tableId, ZipOutputStream zip);

	/**
	 * 预览代码
	 * @param tableId 表
	 * @return [{模板名称:渲染结果}]
	 */
	List<Map<String, String>> preview(Long tableId);

	/**
	 * 目标目录写入渲染结果
	 * @param tableId 表
	 */
	void generatorCode(Long tableId);

}
