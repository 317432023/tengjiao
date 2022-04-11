package com.tengjiao.seed.admin.sample;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * MyBatis-Plus 代码生成器
 * 模板的所有变量参考 com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine#getObjectMap
 */
public class CodeGenerator {
  public static final String
    DFEAULT_DB_IP = "127.0.0.1",
    DEFAULT_DB_NAME = "tengjiao-seed-admin",
    DEFAULT_USERNAME = "root",
    DEFAULT_PASSWORD = "root",
    DEFAULT_PAR_PACKAGE = "com.tengjiao.seed.admin";
  public static final String DB_URL_TPL =
    "jdbc:mysql://DB_IP:3306/DB_NAME?useUnicode=true&useSSL=false&characterEncoding=utf8";

  public static void main(String[] args) {
    String dbIP = scanner("数据库IP：");
    dbIP = StringUtils.isEmpty(dbIP) ? DFEAULT_DB_IP : dbIP;

    String dbName = scanner("数据库名：");
    dbName = StringUtils.isEmpty(dbName) ? DEFAULT_DB_NAME : dbName;

    String username = scanner("数据库用户名：");
    username = StringUtils.isEmpty(username) ? DEFAULT_USERNAME : username;

    String password = scanner("数据库用户名：");
    password = StringUtils.isEmpty(password) ? DEFAULT_PASSWORD : password;

    String dbUrl = DB_URL_TPL.replace("DB_IP", dbIP).replace("DB_NAME", dbName);

    String parentPackage = scanner("输入项目或模块完整包名：");

    parentPackage = parentPackage == null || "".equals(parentPackage) ? DEFAULT_PAR_PACKAGE : parentPackage;

    String[] table = scanner("表名，多个英文逗号分割").split(",");

    generate(dbUrl, username, password, table, parentPackage);
  }

  /**
   * <p>
   * 读取控制台内容
   * </p>
   */
  private static String scanner(String tip) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("请输入" + tip + "：");
    if (scanner.hasNext()) {
      String ipt = scanner.next();
      if (ipt != null && !"".equals(ipt)) {
        return ipt;
      }
    }
    throw new MybatisPlusException("请输入正确的" + tip + "！");
  }

  /**
   * 生成代码
   *
   * @param parentPackage 父包名
   * @param table         哪些表
   */
  private static void generate(String dbUrl, String username, String password, String[] table, String parentPackage) {
    // 代码生成器
    AutoGenerator mpg = new AutoGenerator();

    // begin for 全局配置
    GlobalConfig gc = new GlobalConfig();
    String projectPath = /*scanner("请输入你的项目路径")*/System.getProperty("user.dir");
    gc.setOutputDir(projectPath + "/target/generated-sources/java");
    gc.setAuthor("tengjiao");
    gc.setOpen(false);// 生成之后是否打开资源管理器
    gc.setFileOverride(false);// 重新生成时是否覆盖文件
    gc.setDateType(DateType.ONLY_DATE);// 设置日期类型
    gc.setSwagger2(true);// 开启实体属性 Swagger2 注解
    gc.setBaseResultMap(true);
    gc.setIdType(IdType.AUTO);// 设置主键生成策略 自动增长
    gc.setServiceName("%sService");// mp生成service层代码，默认接口名称第一个字母是有I
    gc.setBaseColumnList(true);// mapper.xml中生成列
    mpg.setGlobalConfig(gc);
    // end for 全局配置

    // 数据源配置
    mpg.setDataSource(getDataSourceConfig(dbUrl, username, password));

    // begin for 包配置
    PackageConfig pc = new PackageConfig();
    pc.setModuleName(scanner("请输入模块名，例如输入 module.sys 或 sys"));//eg. 例如输入 module.sys
    pc.setParent(parentPackage);
    pc.setController("controller");
    pc.setService("service");
    pc.setServiceImpl("service.impl");
    pc.setMapper("mapper");
    pc.setEntity("entity");
    pc.setXml("mapper");
    mpg.setPackageInfo(pc);
    // end for 包配置

    // begin for 策略配置
    StrategyConfig strategy = new StrategyConfig();
    strategy.setVersionFieldName("version");// 乐观锁表字段
    strategy.setInclude(table);// 设置哪些表需要自动生成
    strategy.setNaming(NamingStrategy.underline_to_camel);// 实体名称驼峰命名
    strategy.setColumnNaming(NamingStrategy.underline_to_camel);// 列名名称驼峰命名
    strategy.setEntityLombokModel(true);// 使用Lombok简化getter和setter
    strategy.setRestControllerStyle(true);// 设置controller的api风格为RestController
    strategy.setControllerMappingHyphenStyle(true); // 驼峰转连字符
    // 公共父类
    //strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置！");
    // 父类中的公共字段，多个以逗号分隔
    //strategy.setSuperEntityColumns("id");
    strategy.setTablePrefix(pc.getModuleName() + "_"); // 生成的实体类不包含模块前缀
    mpg.setStrategy(strategy);
    mpg.setTemplateEngine(new FreemarkerTemplateEngine());
    // end for 策略配置

    // begin for 配置Java代码生产模板
    TemplateConfig templateConfig = new TemplateConfig();
    templateConfig.setEntity("/templates/java/model.java");
    templateConfig.setController("/templates/java/controller.java");
    templateConfig.setMapper("/templates/java/mapper.java");
    templateConfig.setXml("/templates/java/mapper.xml");
    templateConfig.setService("/templates/java/service.java");
    templateConfig.setServiceImpl("/templates/java/serviceImpl.java");
    mpg.setTemplate(templateConfig);
    //Vue代码生成
    String jsTemplate = "/templates/vue/js.ftl";
    String vueTemplate = "/templates/vue/vue.ftl";
    InjectionConfig cfg = new InjectionConfig() {
      @Override
      public void initMap() {
        // to do nothing
      }
    };
    List<FileOutConfig> vueList = new ArrayList<>();
    //Vue文件
    vueList.add(new FileOutConfig(vueTemplate) {
      @Override
      public String outputFile(TableInfo tableInfo) {
        return String.format("%s/target/generated-sources/vue/views/%s.vue", projectPath, tableInfo.getEntityName().toLowerCase());
      }
    });
    //js文件
    vueList.add(new FileOutConfig(jsTemplate) {
      @Override
      public String outputFile(TableInfo tableInfo) {
        return String.format("%s/target/generated-sources/vue/js/%s.js", projectPath, tableInfo.getEntityName().toLowerCase());
      }
    });
    cfg.setFileOutConfigList(vueList);
    mpg.setCfg(cfg);
    // end for 配置Java代码生产模板

    mpg.execute();
  }

  /**
   * 数据源配置
   *
   * @return /
   */
  private static DataSourceConfig getDataSourceConfig(String dbUrl, String username, String password) {
    DataSourceConfig dsc = new DataSourceConfig();
    dsc.setUrl(dbUrl);
    dsc.setDriverName("com.mysql.jdbc.Driver");//com.mysql.cj.jdbc.Driver for mysql6+
    dsc.setUsername(username);
    dsc.setPassword(password);
    return dsc;
  }
}