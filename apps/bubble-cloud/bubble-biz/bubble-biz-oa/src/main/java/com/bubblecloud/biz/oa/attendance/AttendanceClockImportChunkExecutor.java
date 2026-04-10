package com.bubblecloud.biz.oa.attendance;

import java.util.List;
import java.util.Map;

import com.bubblecloud.biz.oa.constant.config.OaAsyncConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 考勤导入异步执行器（独立 Bean，确保 {@link Async} 代理生效）。
 *
 * @author qinlei
 * @date 2026/4/7 16:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceClockImportChunkExecutor {

	private final AttendanceClockImportRowHandler rowHandler;

	@Async(OaAsyncConfig.ATTENDANCE_IMPORT_EXECUTOR)
	public void runExcelChunk(List<Map<String, Object>> chunk) {
		for (Map<String, Object> row : chunk) {
			try {
				rowHandler.importExcelRow(row);
			}
			catch (Exception e) {
				log.error("考勤导入单行失败: {}", e.getMessage(), e);
			}
		}
	}

	@Async(OaAsyncConfig.ATTENDANCE_IMPORT_EXECUTOR)
	public void runThirdPartyChunk(int type, List<Map<String, Object>> chunk) {
		log.debug("三方导入 chunk type={} size={}（行级映射待补全）", type, chunk.size());
	}

}
