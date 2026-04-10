package com.bubblecloud.biz.oa.attendance;

import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 考勤打卡导入异步分块（对齐 PHP {@code AttendanceImportJob} +
 * {@code AttendanceClockService::saveRecord}）。
 *
 * @author qinlei
 * @date 2026/4/7 15:40
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceClockImportAsyncService {

	private static final int CHUNK = 10;

	private final AttendanceClockImportChunkExecutor chunkExecutor;

	public void scheduleExcelImport(List<Map<String, Object>> data) {
		if (CollUtil.isEmpty(data)) {
			return;
		}
		List<List<Map<String, Object>>> chunks = CollUtil.split(data, CHUNK);
		log.info("考勤 Excel 导入已投递异步任务：共 {} 条，分 {} 批", data.size(), chunks.size());
		for (List<Map<String, Object>> chunk : chunks) {
			chunkExecutor.runExcelChunk(chunk);
		}
	}

	/**
	 * 三方导入（钉钉/企微）占位：分块异步，具体字段映射后续与 PHP 对齐。
	 */
	public void scheduleThirdPartyImport(int type, List<Map<String, Object>> data) {
		if (CollUtil.isEmpty(data)) {
			return;
		}
		log.info("考勤三方导入已投递异步任务 type={} 条数={}（行级映射待补全）", type, data.size());
		List<List<Map<String, Object>>> chunks = CollUtil.split(data, CHUNK);
		for (List<Map<String, Object>> chunk : chunks) {
			chunkExecutor.runThirdPartyChunk(type, chunk);
		}
	}

}
