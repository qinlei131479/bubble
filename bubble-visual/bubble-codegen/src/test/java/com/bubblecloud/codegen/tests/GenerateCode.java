package com.bubblecloud.codegen.tests;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.bubblecloud.codegen.BubbleCodeGenApplication;
import com.bubblecloud.codegen.dto.TableFieldDTO;
import com.bubblecloud.codegen.mapper.TableFieldMapper;
import com.bubblecloud.codegen.util.FreemarkerUtil;
import com.bubblecloud.codegen.util.HuToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
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
	// model配置back，front
	public static String className = "";
	public static String projectName = "bubble-biz/bubble-biz-backend";
	public static String projectName_entity = "bubble-api/bubble-api-backend";
	public static String packageName = "com.bubblecloud.backend";
	public static String packageName_entity = "com.bubblecloud.api.backend";
	public static String packageName_core = "com.bubblecloud.common.core";
	public static String dbName = "bubble";
	public static String tableName = "sys_dept_test";
	public static String author = "Rampart Qin";

	// 全局配置
	public static String ftlPath = "classpath:/ftl/code/";
	public static String basePath = new File("").getAbsolutePath().replace("bubble-visual/bubble-codegen", projectName);
	public static String basePath_entity = new File("").getAbsolutePath().replace("bubble-visual/bubble-codegen",
			projectName_entity);
	public static String javaFilePath_entity = "/src/main/java/" + packageName_entity.replaceAll("\\.", "/");
	public static String javaFilePath = "/src/main/java/" + packageName.replaceAll("\\.", "/");
	public static String resourcesFilePath = "/src/main/resources/";
	public static boolean createServiceFlag = true;
	public static boolean createControllerFlag = true;
	public static Map<String, Object> dataMap = new HashMap<String, Object>();

	static {
		className = StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
		dataMap.put("tableName", tableName);
		dataMap.put("className", className);
		dataMap.put("package", packageName);
		dataMap.put("package_entity", packageName_entity);
		dataMap.put("package_core", packageName_core);
		dataMap.put("author", author);
	}

	@Resource
	private TableFieldMapper tableFieldMapper;

	@Before
	public void before() throws Exception {
		TableFieldDTO dto = new TableFieldDTO();
		dto.setTableName(tableName);
		dto.setDbName(dbName);
		dto.setClassName(className);
		if (tableFieldMapper != null) {
			List<TableFieldDTO> ret = tableFieldMapper.findTableFieldListByTableName(dto);
			dataMap.put("tableFields", ret);
			String tableComment = tableFieldMapper.findTableComment(dto);
			dataMap.put("tableComment", tableComment);
		}
	}

	private void writeJavaContent(String basePath, String packagePath, String subPath, String ftlName, String fileName)
			throws Exception {
		String ftlNameNew = StrUtil.isBlank(ftlName) ? subPath : ftlName;
		String content = FreemarkerUtil.getTextWithTemplate(ftlPath, ftlNameNew + ".txt", "UTF-8", dataMap);
		FileUtil.writeUtf8String(content, HuToolUtil.joinDirs(basePath, packagePath, subPath, fileName));
	}

	@Test
	public void testCreateModel() throws Exception {
		writeJavaContent(basePath_entity, javaFilePath_entity, "entity", null, className + ".java");
	}

	@Test
	public void testCreateMapper() throws Exception {
		writeJavaContent(basePath, javaFilePath, "mapper", null, className + "Mapper.java");
	}

	@Test
	public void testCreateMapperXml() throws Exception {
		writeJavaContent(basePath, resourcesFilePath, "mapper", "mapperxml", className + "Mapper.xml");
	}

	@Test
	public void testCreateService() throws Exception {
		writeJavaContent(basePath, javaFilePath, "service", null, className + "Service.java");
		writeJavaContent(basePath, javaFilePath, "service/impl", "serviceimpl", className + "ServiceImpl.java");
	}

	@Test
	public void testCreateController() throws Exception {
		writeJavaContent(basePath, javaFilePath, "controller", null, className + "Controller.java");
	}

	@Test
	public void testCreateAll() throws Exception {
		try {
			testCreateModel();
			testCreateMapper();
			testCreateMapperXml();
			if (createServiceFlag) {
				testCreateService();
			}
			if (createControllerFlag) {
				testCreateController();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
