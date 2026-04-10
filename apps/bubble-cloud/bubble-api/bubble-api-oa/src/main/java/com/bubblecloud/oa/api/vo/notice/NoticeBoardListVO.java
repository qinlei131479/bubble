package com.bubblecloud.oa.api.vo.notice;

import java.util.List;

import lombok.Data;

/**
 * 通知公告列表占位。
 *
 * @author qinlei
 * @date 2026/3/29 下午5:00
 */
@Data
public class NoticeBoardListVO {

	private List<NoticeBoardItemVO> list;

	private Integer count;

}
