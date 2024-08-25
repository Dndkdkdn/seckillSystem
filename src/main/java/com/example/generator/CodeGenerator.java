package com.example.generator;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import org.apache.commons.lang3.StringUtils;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * ClassName: CodeGenerator
 * Package: com.example.generator
 * Description:
 *
 * @Author YUYU
 * @Create 2024/7/18 23:33
 * @Version 1.0
 */
// 演示例子，执行 main 方法控制台输入模块表名回车自动生成对应项目目录中
public class CodeGenerator {

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }


    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&serverTimezone=Asia/Shanghai", "root", "root")
                .globalConfig(builder -> {
                    builder.author("yuyu") // 设置作者
//                            .enableSpringdoc() // 开启 SpringDoc 模式(或者单独添加.disableSwagger()来禁用swagger)
                            .outputDir(projectPath + "/src/main/java") // 指定输出目录
                            .disableOpenDir(); // 生成代码后不打开文件夹
                })
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);
                }))
                .packageConfig(builder -> {
                    builder.parent("com.example") // 设置父包名
                            .entity("pojo")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/src/main/resources/mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude(scanner("表名，多个英文逗号分割").split(","))
                            .addTablePrefix("t_") // 设置过滤表前缀
                            .entityBuilder()
                            .enableLombok()
                            .naming(NamingStrategy.underline_to_camel) // 表名映射到实体类，使用下划线转驼峰命名
                            .columnNaming(NamingStrategy.underline_to_camel) // 表字段映射到实体类属性，使用下划线转驼峰命名
                            .enableFileOverride()  // entity 包开启 lombok 和覆盖
                            .mapperBuilder()
                            .enableFileOverride() // mapper 包开启覆盖
                            .serviceBuilder()
                            .formatServiceFileName("%sService") // 设置 Service 接口名称格式
                            .enableFileOverride() // service 包开启覆盖
                            .controllerBuilder()
                            .enableFileOverride()
                            .enableRestStyle(); // controller 包开启覆盖并启用 RestController 模式
//                            .controllerMappingHyphenStyle(true); // RequestMapping中 驼峰转连字符 -
                })
                .templateConfig(builder -> {
                    builder.xml(null); // 不生成 XML 文件
                })// 使用 Freemarker 引擎模板，默认的是 Velocity 引擎模板
                .execute();
    }
    /**
     * 旧版本
     */
//    public static void main(String[] args) {
//        // 代码生成器
//        //构建一个代码自动生成器对象
//        AutoGenerator mpg = new AutoGenerator();
//
//        // 1、创象建全局配置类的对
//        GlobalConfig gc = new GlobalConfig();
//        //获取当前项目路径
//        String projectPath = System.getProperty("user.dir");
////        System.out.println("projectPath:"+projectPath);
////        System.out.println("projectPath = " + projectPath);
//        //自动生成代码存放的路径
//        gc.setOutputDir(projectPath + "/src/main/java");
//        //设置 --作者注释
//        gc.setAuthor("yuyu");
//        //是否打开文件夹
//        gc.setOpen(false);
//        //是否覆盖已有文件
//        gc.setFileOverride(false);
//        //各层文件名称方式，例如： %sAction 生成 UserAction  %s占位符
//        gc.setServiceName("%sService");
//        gc.setEntityName("%s");
//        gc.setControllerName("%sController");
//        gc.setMapperName("%sMapper");
//        gc.setServiceImplName("%sServiceImpl");
//        gc.setXmlName("%sMapper");
//
//        //设置日期策略  date类型
//        gc.setDateType(DateType.ONLY_DATE);
//        //设置主键策略 雪花算法
//        gc.setIdType(IdType.ASSIGN_ID);
//        //设置开启 swagger2 模式
//        gc.setSwagger2(true);
//        //把全局配置放入代码生成器
//        mpg.setGlobalConfig(gc);
//
//        // 2、数据源配置
//        DataSourceConfig dsc = new DataSourceConfig();
//        dsc.setUrl("jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&serverTimezone=Asia/Shanghai");
//        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
//        dsc.setUsername("root");
//        dsc.setPassword("root");
//        dsc.setDbType(DbType.MYSQL);
//        mpg.setDataSource(dsc); //把数据源配置加入到代码生成器
//
//        // 3、包配置
//        PackageConfig pc = new PackageConfig();
//        pc.setParent("com.example");
//        pc.setEntity("pojo");
//        pc.setMapper("mapper");
//        pc.setService("service");
//        pc.setServiceImpl("service.impl");
//        pc.setController("controller");
////        pc.setPathInfo();
//        // ...  有默认值，点击查看源码
//        mpg.setPackageInfo(pc);//包加入代码生成器
//
//        // 4、策略配置
//        StrategyConfig strategy = new StrategyConfig();
//        //数据库表映射到实体类时的命名策略，下划线转驼峰命名  表
//        strategy.setNaming(NamingStrategy.underline_to_camel);
//        // 数据库表字段映射到实体类属性时的命名策略 下划线转驼峰命名字段
//        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        //实体类是否加上lombok注解
//        strategy.setEntityLombokModel(true);
//        //控制层采用RestControllerStyle注解
////        strategy.setRestControllerStyle(true);  //因为需要渲染视图，返回视图页面，而不是返回JSON或XML等内容
//        // RequestMapping中 驼峰转连字符 -
//        strategy.setControllerMappingHyphenStyle(true);
//        strategy.setTablePrefix("t_"); //去掉表中前缀
//        //要映射的数据库表名  （重点）
//        strategy.setInclude(scanner("basketball_defend_shot").split(","));//可以一下子生成数据库中的多张表【表名之间用逗号分割】
//        //添加表名前缀
//        //strategy.setTablePrefix("m_"); //自动拼接上m_
//        //逻辑删除字段名
////        strategy.setLogicDeleteFieldName("deleted");
//        //乐观锁字段名
////        strategy.setVersionFieldName("version");
//        // -------自动填充策略
////        ArrayList<TableFill> fillList = new ArrayList<>();
////        fillList.add(new TableFill("createTime", FieldFill.INSERT));
////        fillList.add(new TableFill("updateTime",FieldFill.INSERT_UPDATE));
//        // 参数是 List<TableFill> 的链表
////        strategy.setTableFillList(fillList);
//        mpg.setStrategy(strategy);
//
//        //---------------------------------
//        // 自定义配置
//        InjectionConfig cfg = new InjectionConfig() {
//            @Override
//            public void initMap() {
//                // to do nothing
//            }
//        };
//
//        // 如果模板引擎是 freemarker
////        String templatePath = "/templates/mapper.xml.ftl";
//        // 如果模板引擎是 velocity
//        String templatePath = "/templates/mapper.xml.vm";
//
//        // 自定义输出配置
//        List<FileOutConfig> focList = new ArrayList<>();
////        // 自定义配置会被优先输出
//        focList.add(new FileOutConfig(templatePath) {
//            @Override
//            //输出了 静态资源下的 Mapper
//            public String outputFile(TableInfo tableInfo) {
//                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
//                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
//                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
//            }
//        });
//
//        cfg.setFileOutConfigList(focList);
//        mpg.setCfg(cfg);
//
//        //配置模版
//        TemplateConfig templateConfig = new TemplateConfig();
////                .setEntity("templates/entity2.java")
////                .setMapper("templates/mapper2.java")
////                .setService("templates/service2.java")
////                .setServiceImpl("templates/serviceImpl2.java")
////                .setController("templates/controller2.java");
//        templateConfig.setXml(null);
//        mpg.setTemplate(templateConfig);
//
////                FreemarkerTemplateEngine模板引擎
////        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
//        mpg.execute();
//    }
}
