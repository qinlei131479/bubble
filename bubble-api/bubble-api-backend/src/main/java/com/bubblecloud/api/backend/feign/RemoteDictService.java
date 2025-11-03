package com.bubblecloud.api.backend.feign;

import com.bubblecloud.api.backend.entity.SysDictItem;
import com.bubblecloud.common.core.constant.ServiceNameConstants;
import com.bubblecloud.common.core.util.R;
import com.bubblecloud.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author lengleng
 * @date 2020/5/12
 * <p>
 * 查询参数相关
 */
@FeignClient(contextId = "remoteDictService", value = ServiceNameConstants.BACKEND_SERVICE)
public interface RemoteDictService {

	/**
	 * 通过字典类型查找字典
	 * @param type 字典类型
	 * @return 同类型字典
	 */
	@NoToken
	@GetMapping("/dict/remote/type/{type}")
	R<List<SysDictItem>> getDictByType(@PathVariable("type") String type);

}
