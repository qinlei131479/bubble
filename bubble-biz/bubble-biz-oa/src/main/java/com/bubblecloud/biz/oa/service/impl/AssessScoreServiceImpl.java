package com.bubblecloud.biz.oa.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.biz.oa.mapper.AssessScoreMapper;
import com.bubblecloud.biz.oa.service.AssessScoreService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.dto.hr.AssessConfigSaveDTO;
import com.bubblecloud.oa.api.entity.AssessScore;
import com.bubblecloud.oa.api.vo.SimplePageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 绩效评分级别/配置服务实现。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Service
public class AssessScoreServiceImpl extends UpServiceImpl<AssessScoreMapper, AssessScore>
		implements AssessScoreService {

	@Override
	public SimplePageVO pageScoreConfig(Pg<AssessScore> pg) {
		Page<AssessScore> r = findPg(pg, new AssessScore());
		return SimplePageVO.of((int) r.getCurrent(), (int) r.getSize(), r.getTotal(), r.getRecords());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveScoreConfig(AssessConfigSaveDTO dto) {
		// 积分配置保存逻辑：以 key 为区分，后续按实际表结构扩展
	}

	@Override
	public Object getExamineConfig() {
		return java.util.Collections.emptyMap();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R create(AssessScore req) {
		return super.create(req);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(AssessScore req) {
		return super.update(req);
	}

}
