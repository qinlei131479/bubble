package com.bubblecloud.oa.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 新建附件记录（PHP {@code POST ent/system/attach/save}）。
 *
 * @author qinlei
 * @date 2026/4/4 12:00
 */
@Data
@Schema(description = "附件保存请求")
public class AttachSaveDTO {

	@Schema(description = "分类 ID")
	private Integer cid;

	@Schema(description = "文件信息：name/url/size/type")
	private JsonNode file;

	@Schema(description = "来源：1 总后台 2 分后台 3 用户")
	private Integer way;

	@Schema(description = "关联类型字符串，如 client、follow")
	private String relationType;

	@Schema(description = "关联主键")
	private Integer relationId;

	@Schema(description = "企业侧业务 id，客户模块可与 relation_id 联动")
	private String eid;

}
