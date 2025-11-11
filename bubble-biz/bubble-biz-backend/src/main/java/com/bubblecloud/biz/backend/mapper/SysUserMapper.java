package com.bubblecloud.biz.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubblecloud.backend.api.dto.UserDTO;
import com.bubblecloud.backend.api.entity.SysUser;
import com.bubblecloud.backend.api.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author qinlei
 * @since 2017-10-29
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

	/**
	 * 根据用户DTO获取用户VO
	 * @param userDTO 用户查询条件DTO
	 * @return 用户信息VO
	 */
	UserVO getUser(@Param("query") UserDTO userDTO);

	/**
	 * 分页查询用户信息（含角色）
	 * @param page 分页参数
	 * @param userDTO 用户查询条件
	 * @return 分页用户信息列表
	 */
	IPage<UserVO> getUsersPage(Page page, @Param("query") UserDTO userDTO);

	/**
	 * 查询用户列表
	 * @param userDTO 查询条件
	 * @return 用户VO列表
	 */
	List<UserVO> listUsers(@Param("query") UserDTO userDTO);

}
