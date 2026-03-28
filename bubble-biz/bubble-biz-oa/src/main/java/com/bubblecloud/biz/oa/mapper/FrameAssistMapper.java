package com.bubblecloud.biz.oa.mapper;

import java.util.List;

import com.bubblecloud.oa.api.dto.FrameAssistView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * eb_frame_assist 查询。
 *
 * @author qinlei
 */
@Mapper
public interface FrameAssistMapper {

	@Select("SELECT fa.id, fa.entid, fa.frame_id AS frameId, fa.user_id AS userId, fa.is_mastart AS isMastart, "
			+ "fa.is_admin AS isAdmin, fa.superior_uid AS superiorUid, f.name AS frameName "
			+ "FROM eb_frame_assist fa LEFT JOIN eb_frame f ON f.id = fa.frame_id AND f.deleted_at IS NULL "
			+ "WHERE fa.user_id = #{userId} AND fa.entid = #{entid} AND fa.deleted_at IS NULL "
			+ "ORDER BY fa.is_mastart DESC")
	List<FrameAssistView> selectUserFrames(@Param("userId") Long userId, @Param("entid") Integer entid);

}
