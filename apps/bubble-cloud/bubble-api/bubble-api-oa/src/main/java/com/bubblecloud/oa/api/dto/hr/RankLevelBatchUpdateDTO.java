package com.bubblecloud.oa.api.dto.hr;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 批量修改职位等级 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "批量修改职位等级")
public class RankLevelBatchUpdateDTO {

	@Schema(description = "职位等级列表")
	private List<RankLevelSaveDTO> list;

}
