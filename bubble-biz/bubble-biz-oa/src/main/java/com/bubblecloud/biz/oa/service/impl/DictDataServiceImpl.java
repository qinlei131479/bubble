package com.bubblecloud.biz.oa.service.impl;

import com.bubblecloud.biz.oa.mapper.DictDataMapper;
import com.bubblecloud.biz.oa.service.DictDataService;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.DictData;
import org.springframework.stereotype.Service;

/**
 * 字典数据服务实现。
 *
 * @author qinlei
 * @date 2026/3/30 下午10:10
 */
@Service
public class DictDataServiceImpl extends UpServiceImpl<DictDataMapper, DictData> implements DictDataService {

}
