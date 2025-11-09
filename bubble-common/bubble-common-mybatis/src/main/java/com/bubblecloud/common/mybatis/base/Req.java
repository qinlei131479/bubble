package com.bubblecloud.common.mybatis.base;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import jakarta.validation.GroupSequence;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口请求参数对象的父类
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
@Data
public class Req<T> extends Model<Req<T>> implements Serializable {

	private static final long serialVersionUID = 1L;

	@GroupSequence({Create.class, Update.class})
	public interface CreateUpdate {
	}

	public interface Create {
	}

	public interface Update {
	}

	public interface Delete {
	}

}
