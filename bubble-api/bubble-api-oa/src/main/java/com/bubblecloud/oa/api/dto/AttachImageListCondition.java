package com.bubblecloud.oa.api.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 素材库列表查询条件（PHP {@code AttachController::index} where + 分页）。
 *
 * @author qinlei
 * @date 2026/4/4 12:00
 */
@Data
@Schema(description = "附件图片列表条件")
public class AttachImageListCondition {

	@Schema(description = "企业 ID")
	private int entid;

	@Schema(description = "来源 way")
	private int way;

	@Schema(description = "分类 ID，null 表示不按分类过滤")
	private Integer cid;

	@Schema(description = "上传存储类型")
	private Integer upType;

	@Schema(description = "文件名模糊（real_name）")
	private String nameLike;

	@Schema(description = "扩展名白名单")
	private List<String> fileExt;

	@Schema(description = "偏移")
	private long offset;

	@Schema(description = "每页条数")
	private long limit;

}
