package com.bubblecloud.biz.oa.util;

import java.util.Collections;
import java.util.List;

import com.bubblecloud.oa.api.constant.OaNoticeActionConstants;
import com.bubblecloud.oa.api.vo.common.NoticeButtonItemVO;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 对齐 PHP {@code NoticeRecordService::getButtonInfo}。
 *
 * @author qinlei
 * @date 2026/4/5
 */
public final class OaNoticeButtonBuilder {

	private OaNoticeButtonBuilder() {
	}

	public static List<NoticeButtonItemVO> buildButtons(String noticeType, int status, int id, String url,
			String uniUrl) {
		String nt = StrUtil.nullToEmpty(noticeType);
		NoticeButtonItemVO data = new NoticeButtonItemVO();
		data.setUrl(StrUtil.nullToEmpty(url));
		data.setUniUrl(StrUtil.nullToEmpty(uniUrl));
		String newUrl = data.getUrl().contains("?") ? data.getUrl() + "&id=" + id : data.getUrl() + "?id=" + id;
		String newUniUrl = data.getUniUrl().contains("?") ? data.getUniUrl() + "&id=" + id
				: data.getUniUrl() + "?id=" + id;
		switch (status) {
			case -1:
				if (containsAny(nt, "contract_abnormal", "contract_overdue_day_remind", "contract_soon_overdue_remind",
						"contract_overdue_remind", "contract_urgent_renew", "contract_day_remind", "approv", "recall",
						"contract_return_money", "contract_renew", "contract_expend", "finance_verify_fail")) {
					data.setAction(OaNoticeActionConstants.RECALL);
					data.setTitle("已作废");
				}
				else if (nt.contains("enterprise")) {
					data.setAction(OaNoticeActionConstants.DELETE);
					data.setTitle("已处理");
				}
				else if (nt.contains("assess_abnormal")) {
					data.setAction(OaNoticeActionConstants.RECALL);
					data.setTitle("");
				}
				else {
					data.setAction(OaNoticeActionConstants.DELETE);
					data.setTitle("已删除");
				}
				data.setUrl("");
				data.setUniUrl("");
				break;
			case 1:
				if (nt.contains("daily") || nt.contains("assess") || nt.contains("news")) {
					data.setTitle("立即查看");
				}
				else {
					data.setTitle("已通过");
				}
				data.setAction(OaNoticeActionConstants.DETAIL);
				if (id > 0) {
					data.setUrl(newUrl);
					data.setUniUrl(newUniUrl);
				}
				break;
			case 2:
				if (!nt.contains("assess")) {
					data.setAction(OaNoticeActionConstants.DETAIL);
					data.setTitle("未通过");
					if (id > 0) {
						data.setUrl(newUrl);
						data.setUniUrl(newUniUrl);
					}
				}
				else {
					data.setAction(OaNoticeActionConstants.DETAIL);
					data.setTitle("立即查看");
					if (id > 0) {
						data.setUrl(newUrl);
						data.setUniUrl(newUniUrl);
					}
				}
				break;
			case 4:
				data.setAction(OaNoticeActionConstants.DELETE);
				data.setTitle("已结束");
				data.setUrl("");
				data.setUniUrl("");
				break;
			case 5:
				if (nt.contains("assess")) {
					data.setAction(OaNoticeActionConstants.DETAIL);
					data.setTitle("立即查看");
					if (id > 0) {
						data.setUrl(newUrl);
						data.setUniUrl(newUniUrl);
					}
				}
				else {
					data.setAction(OaNoticeActionConstants.DELETE);
					data.setTitle("已删除");
					data.setUrl("");
					data.setUniUrl("");
				}
				break;
			default:
				data.setAction(OaNoticeActionConstants.DETAIL);
				if (nt.contains("daily") && !nt.contains("attendance")) {
					data.setTitle("立即填写");
				}
				else {
					data.setTitle("立即查看");
				}
				if (id > 0) {
					data.setUrl(newUrl);
					data.setUniUrl(newUniUrl);
				}
		}
		return Collections.singletonList(data);
	}

	private static boolean containsAny(String nt, String... parts) {
		if (StrUtil.isBlank(nt)) {
			return false;
		}
		for (String p : parts) {
			if (nt.contains(p)) {
				return true;
			}
		}
		return false;
	}

	public static int linkStatusOrZero(Integer v) {
		return ObjectUtil.defaultIfNull(v, 0);
	}

	public static int linkIdOrZero(Integer v) {
		return ObjectUtil.defaultIfNull(v, 0);
	}

}
