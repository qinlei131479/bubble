package com.bubblecloud.biz.backend.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubblecloud.backend.api.entity.SysOauthClientDetails;
import com.bubblecloud.biz.backend.service.SysOauthClientDetailsService;
import com.bubblecloud.biz.backend.mapper.SysOauthClientDetailsMapper;
import com.bubblecloud.common.core.constant.CacheConstants;
import com.bubblecloud.common.core.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OAuth2客户端详情服务实现类
 *
 * @author lengleng
 * @since 2018-05-15
 */
@Service
@RequiredArgsConstructor
public class SysOauthClientDetailsServiceImpl extends ServiceImpl<SysOauthClientDetailsMapper, SysOauthClientDetails>
		implements SysOauthClientDetailsService {

	/**
	 * 根据客户端信息更新客户端详情
	 * @param clientDetails 客户端详情信息
	 * @return 更新结果，成功返回true
	 */
	@Override
	@CacheEvict(value = CacheConstants.CLIENT_DETAILS_KEY, key = "#clientDetails.clientId")
	@Transactional(rollbackFor = Exception.class)
	public Boolean updateClientById(SysOauthClientDetails clientDetails) {
		this.insertOrUpdate(clientDetails);
		return Boolean.TRUE;
	}

	/**
	 * 保存客户端信息
	 * @param clientDetails 客户端详细信息
	 * @return 操作是否成功
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveClient(SysOauthClientDetails clientDetails) {
		this.insertOrUpdate(clientDetails);
		return Boolean.TRUE;
	}

	/**
	 * 插入或更新客户端对象
	 * @param clientDetails 客户端详情对象
	 * @return 更新后的客户端详情对象
	 */
	private SysOauthClientDetails insertOrUpdate(SysOauthClientDetails clientDetails) {
		// 更新数据库
		saveOrUpdate(clientDetails);
		return clientDetails;
	}

	/**
	 * 分页查询OAuth客户端详情
	 * @param page 分页参数
	 * @param query 查询条件
	 * @return 分页查询结果
	 */
	@Override
	public Page getClientPage(Page page, SysOauthClientDetails query) {
		return baseMapper.selectPage(page, Wrappers.query(query));
	}

	/**
	 * 同步客户端缓存
	 * @return 操作结果
	 */
	@Override
	@CacheEvict(value = CacheConstants.CLIENT_DETAILS_KEY, allEntries = true)
	public R syncClientCache() {
		return R.ok();
	}

}
