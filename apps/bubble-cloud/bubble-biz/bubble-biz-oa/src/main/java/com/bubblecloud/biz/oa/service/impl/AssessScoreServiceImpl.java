package com.bubblecloud.biz.oa.service.impl;

import java.math.BigDecimal;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bubblecloud.biz.oa.mapper.AssessScoreMapper;
import com.bubblecloud.biz.oa.service.AssessScoreService;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.mybatis.service.impl.UpServiceImpl;
import com.bubblecloud.oa.api.entity.AssessScore;
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
	public List<AssessScore> listByEntidOrderByLevel(long entid) {
		return list(Wrappers.lambdaQuery(AssessScore.class)
			.eq(AssessScore::getEntid, entid)
			.orderByAsc(AssessScore::getLevel));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void replaceAllByEntid(long entid, List<AssessScore> rows) {
		remove(Wrappers.lambdaQuery(AssessScore.class).eq(AssessScore::getEntid, entid));
		if (CollUtil.isEmpty(rows)) {
			return;
		}
		for (AssessScore r : rows) {
			r.setId(null);
			r.setEntid(entid);
			baseMapper.insert(r);
		}
	}

	@Override
	public Integer resolveGrade(long entid, BigDecimal score) {
		if (ObjectUtil.isNull(score)) {
			return 0;
		}
		Integer g = baseMapper.selectLevelByEntidAndScore(entid, score);
		return ObjectUtil.defaultIfNull(g, 0);
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
