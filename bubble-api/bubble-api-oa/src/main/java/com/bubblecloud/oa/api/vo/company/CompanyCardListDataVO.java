package com.bubblecloud.oa.api.vo.company;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 员工档案列表接口载荷（PHP {@code list} + {@code count}）。
 *
 * @author qinlei
 */
@Data
@Schema(description = "员工档案列表数据")
public class CompanyCardListDataVO {

	private List<CompanyCardListItemVO> list = new ArrayList<>();

	private long count;

}
