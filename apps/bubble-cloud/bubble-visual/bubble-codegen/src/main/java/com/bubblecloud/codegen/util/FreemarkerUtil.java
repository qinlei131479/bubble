package com.bubblecloud.codegen.util;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

/**
 * Freemarker工具类
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
public class FreemarkerUtil {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	/**
	 * 通过文本模板，获取内容
	 *
	 * @param templateString
	 * @param root
	 * @return
	 */
	public static String getTextWithStringTemplate(String templateString, Object root) {
		return getTextWithStringTemplate(templateString, root, null, null);
	}

	/**
	 * 通过文本模板，获取内容(会重新抛出异常）
	 *
	 * @param templateString
	 * @param root
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String getTextWithStringTemplateThrowError(String templateString, Object root) throws Throwable {
		Writer out = null;
		try {
			return getTextWithStringTemplateThrowError(out, templateString, root);
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通过文本模板，获取内容，内部函数
	 *
	 * @param out
	 * @param templateString
	 * @param root
	 * @return
	 * @throws Throwable
	 */
	private static String getTextWithStringTemplateThrowError(Writer out, String templateString, Object root)
		throws Throwable {
		StringTemplateLoader stringLoader = new StringTemplateLoader();
		stringLoader.putTemplate("myTemplate", templateString);
		Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		cfg.setNumberFormat("#");
		cfg.setTemplateLoader(stringLoader);
		Template temp = cfg.getTemplate("myTemplate", "utf-8");
		out = new StringWriter(2048);
		temp.process(root, out);
		out.flush();
		return out.toString();
	}

	/**
	 * 通过文本模板，获取内容
	 *
	 * @param templateString
	 * @param root
	 * @param error          :异常信息存储
	 * @param errorMaxLen    ：异常最大长度
	 * @return
	 */
	public static String getTextWithStringTemplate(String templateString, Object root, StringBuffer error,
		Integer errorMaxLen) {
		Writer out = null;
		try {
			return getTextWithStringTemplateThrowError(out, templateString, root);
		} catch (Throwable e) {
			e.printStackTrace();
			if (error != null && errorMaxLen != null) {
				error.append(StrUtil.maxLength(ExceptionUtil.getMessage(e), errorMaxLen));
			}
			return null;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public static String getTextWithTemplate(String templatePath, String templateName, String charset, Object model)
		throws Exception {
		return FreeMarkerTemplateUtils.processTemplateIntoString(getTemplate(templatePath, templateName, charset),
			model);
	}

	public static String getTextWithTemplate(String templatePath, String templateName, Object model) {
		try {
			return FreeMarkerTemplateUtils.processTemplateIntoString(getTemplate(templatePath, templateName), model);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static Template getTemplate(String templatePath, String templateName) throws Exception {
		return getTemplate(templatePath, templateName, "UTF-8");
	}

	public static Template getTemplate(String templatePath, String templateName, String charset) throws Exception {
		FreeMarkerConfigurationFactoryBean freemarkerConfiguration = new FreeMarkerConfigurationFactoryBean();
		freemarkerConfiguration.setTemplateLoaderPath(templatePath);
		Configuration cfg = freemarkerConfiguration.createConfiguration();
		return cfg.getTemplate(templateName, charset);
	}

	public static Date getTime() {
		return new Date();
	}
}
