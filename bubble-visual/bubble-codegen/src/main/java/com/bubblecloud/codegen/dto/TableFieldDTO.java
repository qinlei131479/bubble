package com.bubblecloud.codegen.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表字段
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TableFieldDTO extends Model<TableFieldDTO> {

	@TableId(type = IdType.INPUT)
	private Long id;

	private String field;

	private String type;

	private String dbName;

	private String tableName;

	private String className;

	private String tableNameLike;
}
