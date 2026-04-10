package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.config.DictDataTreeQueryDTO;
import com.bubblecloud.oa.api.entity.DictData;
import com.bubblecloud.oa.api.vo.config.DictDataTreeNodeVO;

/**
 * 字典数据服务。
 *
 * @author qinlei
 * @date 2026/3/30 下午10:10
 */
public interface DictDataService extends UpService<DictData> {

	List<DictDataTreeNodeVO> treeDictData(DictDataTreeQueryDTO query);

}
