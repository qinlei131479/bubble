package com.bubblecloud.codegen.tests;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.bubblecloud.codegen.BubbleCodeGenApplication;
import com.bubblecloud.codegen.dto.TableFieldDTO;
import com.bubblecloud.codegen.mapper.TableFieldMapper;
import com.bubblecloud.codegen.util.FreemarkerUtil;
import com.bubblecloud.common.core.util.HuToolUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础代码生成工具类
 *
 * @author ：Rampart Qin
 * @date ：Created in 2022/4/16 3:06 下午
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BubbleCodeGenApplication.class)
public class GenerateCode {
	/**
	 * 生成代码模块配置
	 */
	public static String projectName = "bubble-biz/bubble-biz-backend";
	public static String projectName_entity = "bubble-api/bubble-api-backend";

	public static String packageName = "com.bubblecloud.backend";
	public static String packageName_entity = "com.bubblecloud.api.backend";
	public static String packageName_common = "com.bubblecloud.common";

	public static String dbName = "bubble";
	public static String author = "Rampart Qin";
	/**
	 * 填写要生成的表名
	 */
	public static List<String> tableNames = Arrays.asList("sys_dept_test","sys_file_test");

	// 全局配置
	public static String ftlPath = "classpath:/ftl/code/";
	public static String basePath = new File("").getAbsolutePath().replace("bubble-visual/bubble-codegen", projectName);
	public static String basePath_entity = new File("").getAbsolutePath().replace("bubble-visual/bubble-codegen",
			projectName_entity);
	public static String javaFilePath_entity = "/src/main/java/" + packageName_entity.replaceAll("\\.", "/");
	public static String javaFilePath = "/src/main/java/" + packageName.replaceAll("\\.", "/");
	public static String resourcesFilePath = "/src/main/resources/";
	public static Map<String, Object> dataMap = new HashMap<String, Object>();

	static {
		dataMap.put("package", packageName);
		dataMap.put("package_entity", packageName_entity);
		dataMap.put("packageName_core", packageName_common);
		dataMap.put("author", author);
	}

	@Resource
	private TableFieldMapper tableFieldMapper;

	private void generateJavaFile(String basePath, String packagePath, String subPath, String ftlName, String fileName)
			throws Exception {
		String ftlNameNew = StrUtil.isBlank(ftlName) ? subPath : ftlName;
		String content = FreemarkerUtil.getTextWithTemplate(ftlPath, ftlNameNew + ".txt", "UTF-8", dataMap);
		FileUtil.writeUtf8String(content, HuToolUtil.joinDirs(basePath, packagePath, subPath, fileName));
	}

	@Test
	public void testCreateAll() throws Exception {
		tableNames.forEach(tableName -> {
			String className = StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
			dataMap.put("tableName", tableName);
			dataMap.put("className", className);
			List<TableFieldDTO> ret = tableFieldMapper.findTableFieldListByTableName(dbName, tableName);
			dataMap.put("tableFields", ret);
			String tableComment = tableFieldMapper.findTableComment(dbName, tableName);
			dataMap.put("tableComment", tableComment);
			try {
				generateJavaFile(basePath_entity, javaFilePath_entity, "entity", null, className + ".java");
				generateJavaFile(basePath, javaFilePath, "mapper", null, className + "Mapper.java");
				generateJavaFile(basePath, resourcesFilePath, "mapper", "mapperxml", className + "Mapper.xml");

				generateJavaFile(basePath, javaFilePath, "service", null, className + "Service.java");
				generateJavaFile(basePath, javaFilePath, "service/impl", "serviceimpl", className + "ServiceImpl.java");

				generateJavaFile(basePath, javaFilePath, "controller", null, className + "Controller.java");

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
