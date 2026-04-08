package com.bubblecloud.biz.oa.constant;

/**
 * 项目/任务动态类型与动作（对齐 PHP DynamicEnum）。
 *
 * @author qinlei
 * @date 2026/4/8 12:00
 */
public interface ProgramOaConstants {

	 int DYNAMIC_TYPE_PROGRAM = 1;

	 int DYNAMIC_TYPE_TASK = 2;

	 int DYNAMIC_ACTION_CREATE = 1;

	 int DYNAMIC_ACTION_UPDATE = 2;

	 int DYNAMIC_ACTION_DELETE = 3;

	/** PHP Attach relation_type：项目附件 */
	 int ATTACH_RELATION_PROGRAM = 9;

}
