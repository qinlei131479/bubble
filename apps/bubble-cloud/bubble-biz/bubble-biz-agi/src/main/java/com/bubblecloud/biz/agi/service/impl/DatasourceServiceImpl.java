package com.bubblecloud.biz.agi.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.agi.api.entity.Datasource;
import com.bubblecloud.agi.api.entity.DatasourceTable;
import com.bubblecloud.agi.api.entity.DatasourceTableField;
import com.bubblecloud.agi.api.dto.DatasourceTestDTO;
import com.bubblecloud.agi.api.vo.DatasourceTestResultVO;
import com.bubblecloud.agi.api.vo.TableInfoVO;
import com.bubblecloud.biz.agi.mapper.DatasourceMapper;
import com.bubblecloud.biz.agi.mapper.DatasourceTableMapper;
import com.bubblecloud.biz.agi.mapper.DatasourceTableFieldMapper;
import com.bubblecloud.biz.agi.service.DatasourceService;
import com.bubblecloud.biz.agi.util.JdbcUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据源
 *
 * @author Rampart
 * @date 2026-02-13 16:45:56
 */
@Slf4j
@Service
public class DatasourceServiceImpl extends UpServiceImpl<DatasourceMapper, Datasource> implements DatasourceService {

	@Autowired
	private DatasourceTableMapper datasourceTableMapper;

	@Autowired
	private DatasourceTableFieldMapper datasourceTableFieldMapper;

	@Override
	public DatasourceTestResultVO testConnection(DatasourceTestDTO dto) {
		DatasourceTestResultVO result = JdbcUtils.testConnection(dto);

		// 如果有数据源ID，则更新数据库中的状态
		if (Objects.nonNull(dto.getId())) {
			Datasource datasource = new Datasource();
			datasource.setId(dto.getId());
			datasource.setStatus(result.getConnected() ? "Success" : "Failed");
			this.updateById(datasource);
			log.info("数据源[{}]连接状态已更新为: {}", dto.getId(), datasource.getStatus());
		}

		return result;
	}

	@Override
	public List<TableInfoVO> getTableInfo(DatasourceTestDTO dto) {
		return JdbcUtils.getTableInfo(dto);
	}

	/**
	 * 同步数据源表结构到本地数据库
	 *
	 * @param dsId       数据源ID
	 * @param tableNames 选中的表名列表（为空则同步所有表）
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void syncTables(Long dsId, List<String> tableNames) {
		// 1. 查询数据源信息
		Datasource datasource = this.getById(dsId);
		if (datasource == null) {
			throw new RuntimeException("数据源不存在");
		}

		// 2. 构建测试DTO
		DatasourceTestDTO dto = new DatasourceTestDTO();
		dto.setDsType(datasource.getDsType());
		dto.setHost(datasource.getHost());
		dto.setPort(datasource.getPort());
		dto.setUsername(datasource.getUsername());
		dto.setPassword(datasource.getPassword());
		dto.setDsName(datasource.getDsName());
		dto.setDbSchema(datasource.getInstance());

		// 3. 获取表信息
		List<TableInfoVO> allTableInfoList = JdbcUtils.getTableInfo(dto);
		if (CollUtil.isEmpty(allTableInfoList)) {
			log.warn("数据源[{}]未获取到任何表", dsId);
			return;
		}

		// 4. 过滤要同步的表
		List<TableInfoVO> tableInfoList;
		if (CollUtil.isNotEmpty(tableNames)) {
			tableInfoList = allTableInfoList.stream()
					.filter(t -> tableNames.contains(t.getTableName()))
					.collect(Collectors.toList());
		} else {
			tableInfoList = allTableInfoList;
		}

		if (CollUtil.isEmpty(tableInfoList)) {
			log.warn("数据源[{}]没有需要同步的表", dsId);
			return;
		}

		// 5. 查询该数据源下已存在的表ID列表（用于删除字段）
		List<DatasourceTable> existingTables = datasourceTableMapper.selectList(
				Wrappers.lambdaQuery(DatasourceTable.class).eq(DatasourceTable::getDsId, dsId)
		);
		List<Long> existingTableIds = existingTables.stream()
				.map(DatasourceTable::getId)
				.collect(Collectors.toList());

		// 6. 先删除旧的表字段数据
		if (!existingTableIds.isEmpty()) {
			datasourceTableFieldMapper.delete(
					Wrappers.lambdaQuery(DatasourceTableField.class)
							.eq(DatasourceTableField::getDsId, dsId)
							.in(DatasourceTableField::getTableId, existingTableIds)
			);
			log.info("数据源[{}]已删除{}条旧表字段数据", dsId, existingTableIds.size());
		}

		// 7. 再删除旧的表结构数据
		datasourceTableMapper.delete(
				Wrappers.lambdaQuery(DatasourceTable.class).eq(DatasourceTable::getDsId, dsId)
		);
		log.info("数据源[{}]已删除{}条旧表结构数据", dsId, existingTables.size());

		// 用于存储表名和表ID的映射
		Map<String, Long> tableIdMap = new HashMap<>();

		// 8. 新增表信息
		for (TableInfoVO tableInfo : tableInfoList) {
			DatasourceTable dsTable = new DatasourceTable();
			dsTable.setDsId(dsId);
			dsTable.setTableName(tableInfo.getTableName());
			dsTable.setTableComment(tableInfo.getTableComment());
			dsTable.setCustomComment(tableInfo.getTableComment());
			dsTable.setCheckedFlag(1);
			// 新增表
			datasourceTableMapper.insert(dsTable);
			tableIdMap.put(tableInfo.getTableName(), dsTable.getId());
		}

		// 9. 同步表字段信息
		for (TableInfoVO tableInfo : tableInfoList) {
			Long tableId = tableIdMap.get(tableInfo.getTableName());
			if (tableId == null) {
				continue;
			}
			// 获取表的字段信息
			List<DatasourceTableField> fieldList = JdbcUtils.getTableFields(dto, tableInfo.getTableName());
			// 保存字段信息
			for (DatasourceTableField field : fieldList) {
				field.setDsId(dsId);
				field.setTableId(tableId);
				field.setCustomComment(field.getFieldComment());
				// 新增字段
				datasourceTableFieldMapper.insert(field);
			}
			log.info("数据源[{}]表[{}]字段同步成功，共{}个字段", dsId, tableInfo.getTableName(), fieldList.size());
		}

		// 10. 更新数据源状态为已同步
		Datasource updateDatasource = new Datasource();
		updateDatasource.setId(dsId);
		updateDatasource.setStatus("Success");
		this.updateById(updateDatasource);

		log.info("数据源[{}]表结构同步成功，共{}张表", dsId, tableInfoList.size());
	}
}