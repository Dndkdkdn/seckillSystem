

# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.3.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.3.1/maven-plugin/reference/html/#build-image)
* [Thymeleaf](https://docs.spring.io/spring-boot/docs/3.3.1/reference/htmlsingle/index.html#web.servlet.spring-mvc.template-engines)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.1/reference/htmlsingle/index.html#web)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.



推荐一个前端页面代码生成的网站：http://www.ibootstrap.cn/





# 秒杀项目

基于SpringBoot+Redis轻松实现Java高并发秒杀系统-我们要能够撑住100W级压力

![3](E:\Learning\JAVA\JAVA_project_ideafile\java_redis_HighConcurrency_seckill\yuyu-seckill\md_image\3.png)

 1、首先用户通过任意渠道访问我们的网站，然后根据一定的路由规则（比如对用户的ID进行hash）被分配到某个服务器接受服务。（我这里没有用Nginx做负载均衡，但实际应该要做的，不然所有用户都访问同一个服务器，负载 太大。）

 2、当用户进行秒杀时，因为考虑到同一个时刻，并发量可能会特别大。所以不能让服务器直接访问DB，不然DB很容易挂掉，所以应该使用redis加缓存。用户在秒杀的时候在Redis中预减库存减少数据库的访问，同时使用内存标记减少redis的访问，（redis的处理能力也是有限的，负载太大也是会宕机的，所以这里也要进行Redis的保护，即加一个标记变量记录是否还有商品，如果商品已经没有了，那就置位，这样的话，后续的请求就不会去访问redis然后直接返回秒杀失败）。

 3、RabbitMQ队列缓冲，异步下单。因为服务器处理下单涉及DB的读写，当并发量很大的时候，需要很多时间，从而用户体验会很不好，因为需要等待很久才知道结果。所以采用消息队列异步下单。即如果用户秒杀成功，那么创建的订单并不直接写入DB。而是给rabbitmq发送一条message.然后就直接返回给用户说下单成功。然后由监听消息队列的消费者根据接收到的消息，创建订单并写入DB.这里为了提高效率，可以使用一个线程池来解决并发及连接复用的的问题。

 4、用户下单完成后，点击订单详情可以查看订单详情，然后选择立即支付。 可以使用支付宝支付，因为时测试，所以使用的是沙箱环境。想体验的朋友可以下载沙箱钱包来测试一下。支付完成后，可以回到商品列表继续秒杀。



https://blog.csdn.net/weixin_65777087/article/details/126827406



## 技术栈

前端：Thymeleaf + Bootstrap + Jqueryy

SpringBoot + Mybatis-Plus + Lombok

中间件：

RabbitMQ：异步、解耦系统中的一些模块、流量削峰作用

Redis：缓存









## 课程介绍

项目搭建
分布式Session： 秒杀-> 商城 -> 微服务 -> 分布式 -> 分布式共享Session
秒杀功能：增删改查
压力测试：超卖、并发量
页面优化
服务优化：异步、接口优化：Redis的预减库存、内存标记Redis、减少Redis的访问、分布式锁
接口安全：秒杀地址隐藏、黄牛脚本、验证码、接口限流

## 学习目标

安全优化

- 隐藏秒杀地址

- 验证码
- 接口限流

服务优化

- RabbitMQ消息队列：缓冲、异步下单

- 接口优化：从数据库到Redis，到网络通信、到内存标记
- 分布式锁：控制库存

页面优化

- 页面优化

- 静态化分离

分布式会话

- 用户登录

- 共享Session

功能开发

- 商品列表

- 商品详情
- 秒杀
- 订单详情

系统压测

- JMeter入门
- 自定义变量
- 正式压测



## 如何设计一个秒杀系统

稳、准、快：高可用、数据一致性、高性能

- 高性能

  秒杀涉及大量并发读和写，动静分离方案、热点的发现和隔离、请求的削峰与分层过滤、服务端的极致优化

- 高可用

  保证系统的高可用和准确性，还要设计一个PlanB来兜底

- 一致性

  有限数量的商品在同一时刻被很多倍的请求同时减库存，减库存分为：“拍下减库存”、“付款减库存”以及预扣等，保证数据的准确性

应对高并发：缓存、异步、安全用户

解决：并发读、并发写

- 并发读

  尽量减少用户到服务端读数据、读更少数据

- 并发写

  数据库层面独立出一个特殊库做特殊处理

- 针对秒杀系统做保护

- 意料之外的情况设计兜底方案

## 项目搭建

配置文件：

```
spring:
  application:
    name: yuyu-seckill
  thymeleaf:
    cache: false #thymeleaf缓存关闭，因为不需要
  datasource: # 数据源配置
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&serverTimezone=Asia/Shanghai
    username: root
    password: root
    hikari: #springboot自带的数据库连接池（号称是最快的连接池）
      pool-name: DateHikariCP #自己起的连接池的名字
      minimum-idle: 5  #最小的空闲连接数
      idle-timeout:  1800000 #空闲连接存活的时间（默认600000   10分钟）
      maximum-pool-size: 10 #最大连接数  默认为10
      auto-commit: true #自动提交  从连接池返回的连接自动提交
      max-lifetime: 1800000 #连接最大存活时间 ，0表示永久存活，默认1800000（30分钟）
      connection-timeout: 30000 #连接超时时间 默认30s
      connection-test-query: SELECT 1 #心跳机制 测试连接是否是可用的 一个查询语句

#mybatis-plus配置
mybatis-plus:
  mapper-locations: classpath*:com/example/mapper/*Mapper.xml #配置××××Mapper.xml映射文件的位置
  type-aliases-package: com.example.pojo #返回的别名  配置Mybatis数据返回类型别名（默认是全类名）

#Mybatis SQL 日志打印（方法接口所在的包，不是Mapper.xml所在的包）
logging:
  level:
    com.example.seckill.mapper: debug


```

**注意点：**（如果想要通过thymeleaf前端页面看到model设置属性展示效果）

```
/**
 * 使用@RestController和@RequestMapping：@RestController是@Controller和@ResponseBody的组合注解，主要用于返回JSON或XML等内容，而不是视图页面。
 * 返回视图的方式：在Spring Boot中，如果你想返回一个视图页面而不是JSON，需要使用@Controller而不是@RestController。
 */

/**
 * 将@RestController改为@Controller后，Spring Boot会将返回的字符串视为视图名，
 * 而不是返回JSON。在访问http://localhost:8080/test/hello时，
 * Spring Boot会找到名为hello的模板并渲染它，同时将name属性传递给视图，
 * 从而在页面上显示hello yuyu。
 */
```



## 分布式会话



### 登录功能

数据库(先创建数据库中用户表)

```
CREATE TABLE t_user(
`id` BIGINT(20) NOT NULL COMMENT '用户ID，手机号码',
`nickname` VARCHAR(255) NOT NULL,
`password` VARCHAR(32) DEFAULT NULL COMMENT 'MD5(MD5(pass明文+固定salt)+salt)',
`salt` VARCHAR(10) DEFAULT NULL,
`head` VARCHAR(128) DEFAULT NULL COMMENT '头像',
`register_date` DATETIME DEFAULT NULL COMMENT'注册时间',
`last_login_date` DATETIME DEFAULT NULL COMMENT '最后一次登录时间',
`login_count` INT(11) DEFAULT '0' COMMENT '登录次数',
PRIMARY KEY(`id`)
)
```

两次MD5加密：保证安全

**用户端MD5加密是为了防止用户密码在网络中明文传输，服务端MD5是为了提高密码安全性，双重保险** 

- 第一次：用户端输入明文密码，传到后端的时候，进行一次MD5加密，明文密码在网络中传输容易被截获  用户端：PASS=MD5(明文+固定salt)
- 第二次：后端接到已完成第一次MD5加密的数据在存到数据库之前再进行一次MD5加密，进一步提高安全性  服务端：PASS=MD5(用户输入+随机salt)

MD5工具类：pom.xml  依赖引入

 

```
<!--        MD5依赖-->
        <dependency>
            <groupId>commons-codec</groupId>
            <artifactId>commons-codec</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.6</version>
        </dependency>
```

编写MD5工具类：MD5Util

```
@Component
public class MD5Util {
    /**
     * MD5加密
     * @param src
     * @return
     */
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";//准备第一次MD5加密时用的固定的salt(自定义的salt)
    //此处的salt是为了和前端的salt进行统一，因为第一次加密是用户输完密码之后，传到后端的时候，在前端进行的。后端也需要这个salt获取到真正的密码，
    //然后将真正的密码通过第一次MD5加密后，获取到的秘钥，再与数据库中存储的随机salt进行二次MD5加密

    /**
     * 第一次MD5加密
     * @param inputPass
     * @return
     */
    public static String inputPassToFromPass(String inputPass){
        String src = salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);//此处可以随意用salt和inputPass构造要进行MD5加密的字符串
        return md5(src);
    }

    /**
     * 第二次MD5加密
     * @param fromPass
     * @param salt
     * @return
     */
    public static String fromPassToDBPass(String fromPass, String salt){//此处的参数salt与上面自定的salt不是一个（此处指的是数据库中存储的salt【二次加密所用的随机的salt】）
        String src = salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(5) + salt.charAt(4);//此处可以随意用salt和inputPass构造要进行MD5加密的字符串
        return md5(src);
    }

    /**
     * 上述两个方法整合为为外部调用的方法
     * @param inputPass
     * @param salt
     * @return 返回的密文 是最终要存到数据库的password
     */
    public static String inputPassToDBPass(String inputPass, String salt){//salt指的是随机的salt
        String fromPass = inputPassToFromPass(inputPass);
        return fromPassToDBPass(fromPass, salt);

    }
}
```

PS:引入包时，MD5所用的包不是import org.springframework.util.DigestUtils;而是import org.apache.commons.codec.digest.DigestUtils;

（mybatis逆向生成工具----->使用逆向生成工具对之前创建的t_user表生成对应的pojo、mapper、mapper.xml）

但是mybatisplus逆向生成工具----->使用逆向生成工具对之前创建的t_user表生成对应的pojo、mapper、mapper.xml【此外还可以生成对应的service/serviceImpl/controller(还包括单表的增删改查)】

pom.xml

```
<!--        mybatis-plus依赖-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>3.5.5</version>
        </dependency>
<!--mybatis-plus的代码生成器-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-generator</artifactId>
            <version>3.5.5</version>
        </dependency>
<!--    由于代码生成器用到了模板引擎，请自行引入您喜好的模板引擎。    -->
        <dependency>
            <groupId>org.freemarker</groupId>
            <artifactId>freemarker</artifactId>
            <version>2.3.30</version>
        </dependency>
        <!-- 模板引擎 -->
        <dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity-engine-core</artifactId>
            <version>2.0</version>
        </dependency>
```

CodeGenerator.java

```
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
}
```



### 参数校验

在开发登录功能的过程中涉及很多参数校验，比如格式，非空等等

pom.xml

```
<!--    validation组件    参数校验-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
```

参数校验处理类ValidatorUtil：

```
public class ValidatorUtil {

    private static final Pattern mobile_pattern = Pattern.compile("^1[3-9]\\d{9}$");

    public static boolean isMobile(String mobile){
        if(StringUtils.isEmpty(mobile)){
            return false;
        }
        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }

}
```



自定义mobile参数校验注解IsMobile（使用自定义参数校验规则IsMobileValidator以及参数校验处理类ValidatorUtil）  控制台输出确实是拦截了不合规的参数，参数校验确实成功了，但是没有在前端页面显示出校验结果信息，而是将校验结果以异常的形式在控制台抛出。  所以后续应该正确的定义异常，将异常信息展示到页面上去。---> 捕获异常  --->  异常处理



自定义注解：

```
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {IsMobileValidator.class}
)
public @interface IsMobile {
    boolean required() default true;

    String message() default "手机号码格式错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

有了自定义注解要有自定义规则：

```
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {//初始化最主要的是去获取  是否是必填的
        required = constraintAnnotation.required();

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required){//是否是必填的，如果是必填的
            return ValidatorUtil.isMobile(s);
        }else{//如果是非必填的话
            if(StringUtils.isEmpty(s)) return true;//如果mobile为空的话
            else return ValidatorUtil.isMobile(s);
        }
    }
}

```



### 异常处理

我们知道，系统中异常包括：编译时异常和运行时异常RuntimeException，前者通过捕获异常从而获取异常信息，后者主要通过规范代码开发、测试通过手段减少运行时异常的发生。在开发中，不管是dao层、service层还是controller层，都有可能抛出异常，在SpringMVC中，能将所有类型的异常处理从各处理过程解耦出来，既保证了相关处理过程的功能比较单一，也实现了异常信息的统一处理和维护。

![3](E:\Learning\JAVA\JAVA_project_ideafile\java_redis_HighConcurrency_seckill\yuyu-seckill\md_image\3.jpg)

springboot全局异常处理方式有两种：第一种使用@RestControllerAdvice和@ExceptionHandler(Exception.class) //指定能够处理的异常类型注解这两个组合注解  第二种是使用ErrorException类来实现  两种方式的区别是第一种@RestControllerAdvice只能处理控制器抛出的异常，但是可以定义多个拦截方法，拦截不同的异常，并且可以抛出对应的异常信息，因为这个时候请求呢已经进入控制器里面了，第二种ErrorException类可以处理所有的异常，比如404 401这种还未进入控制器的异常。该项目采用第一种进行异常处理。

全局异常对象类  异常pojo类：

```
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalException extends RuntimeException{
    private RespBeanEnum respBeanEnum;
}
```

异常处理类：

```
@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理异常
    @ExceptionHandler(Exception.class) //指定能够处理的异常类型
    public RespBean ex(Exception e){
        if(e instanceof GlobalException){
            GlobalException globalException = (GlobalException) e;//异常属于定义的全局异常的对象或者子对象的话，进行类型提升？？强转？？
            return RespBean.error(globalException.getRespBeanEnum());
        }else if(e instanceof BindException){
            BindException bindException = (BindException) e;
            RespBean respBean =  RespBean.error(RespBeanEnum.VALIDATION_ERROR);
            respBean.setMessage(respBean.getMessage() + ":" + bindException.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        e.printStackTrace();//打印堆栈中的异常信息
        //捕获到异常之后，响应一个标准的RespBean
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
```





### 分布式Session

到此为止，登录功能做了很多东西：校验组件、校验规则、异常处理 ---- > 登录后要进行秒杀抢购，商品秒杀抢购页面必然要判断用户是否登录的  后续要进行请求过滤或拦截，进行是否登录成功的判断的--->简单的 cookie和session【具体实现看课程讲解，此处登录验认证我使用JWT令牌实现  JWT令牌＋拦截器的写法+分布式session】



课程cookie+session:

CookieUtil

UUIDUtil

```
public class UUIDUtil {
    public static String uuid() {
    	return UUID.randomuuID().toString().replace( target: "-"，replacement: "");
    }
}
```

生成Cookie:

```
//生成cookie
String ticket = UUIDUtil.uuid();
request.getSession().setAttribute(ticket , user);
CookieUtil.setCookie(request,response, "userTicket" ,ticket);
return RespBean.success();
```



**此处遇到的问题：**

```
原视频中没有实现：1.JWT 2.二次登录redis二次存储token,此时应该删除前一次存储的token
#3.请求拦截器，统一校验 如果未登录，应该返回相应信息后，由前端重定向至登录页面  然后如果是前后端分离的项目，就方便很多了，请求拦截器直接从request Header中获取token
```

1. 由于这个项目没有配套的前端项目，所有目前实现都是前后端不分离的，那么此处我在前端简单的方式没办法实现：向后端发起的请求都在Request Header 中带有Token字段，所以我暂时采用了 JWT令牌＋拦截器的写法+分布式session的方式实现，登陆成功后将生成的token存储在session中，在拦截器中获取session中存储的token，至于session无法跨域且在服务器集群环境下（分布式环境下）无法直接使用Session这些问题 可以使用Redis存储来解决。

   

2. ***【 JWT令牌＋拦截器的写法（将JWT令牌在前端存储在LocalStorage中，并且设置每个请求头有一个"Token"字段）  这种写法在黑马程序员JavaWeb企业级开发流程中，案例-前端实现了】***

   

3. 要么去公司问问写前端的目前三人篮球项目 是不是 JWT令牌＋拦截器的写法+分布式session（将JWT存储在session中再利用redis实现跨域分布式） 还是说是 JWT令牌＋拦截器的写法（将JWT令牌在前端存储在LocalStorage中，并且设置每个请求头有一个"Token"字段）






会话跟踪技术有两种：

1. Cookie（客户端会话跟踪技术）

   - 数据存储在客户端浏览器当中
   - **优缺点**
     - 优点：HTTP协议中支持的技术（像Set-Cookie 响应头的解析以及 Cookie 请求头数据的携带，都是浏览器自动进行的，是无需我们手动操作的）
     - 缺点：
       - 移动端APP(Android、IOS)中无法使用Cookie
       - 不安全，用户可以自己禁用Cookie
       - Cookie不能跨域

2. Session（服务端会话跟踪技术）

   - 数据存储在储在服务端

   - **优缺点**

     - 优点：Session是存储在服务端的，安全
     - 缺点：
       - 服务器集群环境下无法直接使用Session
       - 移动端APP(Android、IOS)中无法使用Cookie
       - 用户可以自己禁用Cookie
       - Cookie不能跨域

     > PS：Session 底层是基于Cookie实现的会话跟踪，如果Cookie不可用，则该方案，也就失效了。

3. 令牌技术

   - 大家会看到上面这两种传统的会话技术，在现在的企业开发当中是不是会存在很多的问题。 为了解决这些问题，在现在的企业开发当中，基本上都会采用第三种方案，通过令牌技术来进行会话跟踪。
   - JWT令牌最典型的应用场景就是登录认证：
     1. 在浏览器发起请求来执行登录操作，此时会访问登录的接口，如果登录成功之后，我们需要生成一个jwt令牌，将生成的 jwt令牌返回给前端。
     2. 前端拿到jwt令牌之后，会将jwt令牌存储起来。在后续的每一次请求中都会将jwt令牌携带到服务端。
     3. 服务端统一拦截请求之后，先来判断一下这次请求有没有把令牌带过来，如果没有带过来，直接拒绝访问，如果带过来了，还要校验一下令牌是否是有效。如果有效，就直接放行进行请求的处理。

   ```
   <!-- JWT令牌依赖(登陆认证)-->
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt</artifactId>
       <version>0.9.1</version>
   </dependency>
   ```

分布式session的问题：（部署在多台系统中，高并发  nginx负载均衡服务器）

刚开始我们在Tomcat1登录之后，用户信息放在Tomcat1的Session里。过了一会，请求又被Nginx分发到了Tomcat2上，这时Tomcat2 上 session里还没有用户信息，于是又要登录。

原因：

由于Nginx使用默认负载均衡策略（轮询），请求将会按照时间顺序逐一分发到后端应用上。也就是说刚开始我们在Tomcat1登录之后，用户信息放在Tomcat1的session里。过了一会，请求又被Nginx分发到Tomcat2上，这时Tomcat2上Session里还没有用户信息，于是又要登录。

解决方案：

1. Session复制

   优点：无需修改代码，只修改Tomcat配置

   缺点：Session同步传输占用内网带宽，多台Tomcat同步性能指数级下降，Session占用太多内存，无法有效水平扩展

2. 前端存储

   优点：不占用服务端内存

   缺点：占用外网带宽，存在安全风险，数据大小受cookie限制

3. Session粘滞

   优点：无需修改代码，服务端可以水平扩展

   缺点：增加新机器，会重新Hash，导致重新登录，应用重启需要重新登录

4. 后端集中存储

   优点：安全，容易水平扩展

   缺点：增加复杂度，需要修改代码

5. JWT方式，利用token

![屏幕截图 2024-07-31 233323](E:\Learning\JAVA\JAVA_project_ideafile\java_redis_HighConcurrency_seckill\yuyu-seckill\md_image\屏幕截图 2024-07-31 233323.jpg)

![1](E:\Learning\JAVA\JAVA_project_ideafile\java_redis_HighConcurrency_seckill\yuyu-seckill\md_image\1.jpg)

![2](E:\Learning\JAVA\JAVA_project_ideafile\java_redis_HighConcurrency_seckill\yuyu-seckill\md_image\2.jpg)



### Redis存储用户信息【后面还会用作秒杀缓存】

Redis实现：分布式session  学习redis只需要学习五种数据类型的操作就可以了 redis通过redis集群的Redis Sentinel哨兵和自动分区提供高可用

- springsession 存储到集中的地方，存储到了Redis里
- 整个把用户信息存储到Redis里面

#### **redis操作：**

##### string:

set name zhangsan

get name --> "zhangsan"



mset age 18 addr shanghai

mget age addr---> "18" "shanghai"

del name(删除"name" key-value)



##### hash：

hset user name zhangsan

hget user name ----> "zhangsan"



hmset user name lisi age 19 addr hangzhou

hmget user name addr  ----> "lisi" "hangzhou"

hgetall user  ---->  "name" "lisi" "age" "19" "addr" "hangzhou"

hdel user addr(删除user key对应的哈希桶中的addr及对应值)

del user (删除user key-vale)



##### list:

lpush students zhangsan lisi 从数组左侧添加

rpush students wangwu zhangliu  从数组右侧添加

lrange students 0 3 遍历访问数组----->  1) "lisi"
2) "zhangsan"
3) "wangwu"
4) "zhangliu"



llen students --> 4(长度)

lrem students [count] [value] (删除list中几个指定的value（因为list中元素可以重复）)



##### set:[无序，不可重复（内部排序）]

sadd letters aaa bbb ccc ddd eee

smembers letters
1) "ddd"
2) "ccc"
3) "bbb"
4) "aaa"
5) "eee"

scard letters  -----> (integer) 5  获取长度

srem letters ccc

smembers letters
1) "bbb"
2) "aaa"
3) "ddd"
4) "eee"







##### sorted set: 不可重复且有序

zadd [key] [score member ... ] 添加后按照score排序

zrange lettersSorted 0 4
1) "aaa"
2) "bbb"
3) "ccc"
4) "ddd"
5) "eee"

zcard lettersSorted  ----> 5  获取长度

zrem lettersSorted aaa ccc





##### 失效时间：到时间就会删除对应的key-value：

###### 两种设置方式：

1. 在新增key并设值的时候直接设置失效时间
2. 给已经存在的key-value设置失效时间

set [key] [value] [expiration EX seconds|PX milliseconds] [NX|XX];

#####set code test ex 10  //在10s后失效

127.0.0.1:6379> set code test ex 10
OK
127.0.0.1:6379> get code
"test"
127.0.0.1:6379> get code
"test"
127.0.0.1:6379> get code  （10s后失效了，获取不到 。可以通过ttl code 输出还有多少s失效(pttl 是还有多少ms失效)，来查看是否失效（输出-2则说明已经失效了;输出-1则说明永不失效））
(nil)



set code test [NX|XX]:NX表示key之前不存在的情况下才能设置成功  XX表示key之前存在的情况下才能设置成功  一般不指定NX还是XX时，无论key之前是否存在都能设置成功  其实NX和XX可以用来做锁（lua脚本设置锁、 redrom实现锁  、java代码实现锁/分布式锁）

expire/pexpire [key]  s/ms









redis安装配置：

为什么我使用Redis Desktop Manager连接centos docker中的redis时，只配置了host和端口号就可以连接上，但是连接centos docker外的redis时必须使用SSH Tunnel配置centos的用户名和密码 才能连接成功？ 已知是防火墙没关闭的原因。那么我产生了以下两个问题：一：为何防火墙开启状态下，使用SSH Tunnel配置centos的用户名和密码 就能连接成功？  二：为何在防火墙开启的状态下Redis Desktop Manager连接centos docker中的redis时，只配置了host和端口号就可以连接上？

这两个问题的关键在于防火墙配置和网络访问的机制。

#### 一、为何防火墙开启状态下，使用 SSH Tunnel 配置 CentOS 的用户名和密码就能连接成功？

##### 原因

1. **SSH Tunnel 的工作原理**：
   - 当你使用 SSH Tunnel 配置 Redis Desktop Manager 时，你实际上是通过 SSH 创建了一条安全的加密通道，这条通道将 Redis 的端口（6379）从远程服务器转发到你的本地计算机上。这个过程绕过了防火墙的直接限制，因为防火墙只会阻止直接对 Redis 端口的访问，而不会阻止 SSH 连接。
2. **防火墙的作用**：
   - 防火墙阻止外部网络直接访问 Redis 的端口（6379），但是它不会阻止通过 SSH 隧道的流量。因为从防火墙的角度看，所有通过 SSH 隧道转发的流量都是来自一个安全的、允许的 SSH 端口（通常是 22），防火墙只会处理 SSH 数据流的安全性，而不会检查转发的数据内容。
3. **连接流程**：
   - 你在本地通过 SSH 隧道连接 Redis 的时候，实际是通过 SSH 服务器的端口（22）进行通信，防火墙只需要允许 SSH 流量进出，而不需要直接开放 Redis 的端口给外部访问。

#### 二、为何在防火墙开启的状态下 Redis Desktop Manager 连接 CentOS Docker 中的 Redis 时，只配置了 host 和端口号就能连接上？

##### 原因

1. **容器网络配置**：
   - Docker 容器在默认情况下会使用桥接网络模式，这意味着容器和宿主机的网络是隔离的，但 Docker 会自动配置网络以允许宿主机和容器之间的通信。在这种模式下，你可以从宿主机直接访问容器的服务。
2. **宿主机和容器的网络**：
   - 如果你在 Redis Docker 容器中运行 Redis，Docker 会将容器的端口（6379）映射到宿主机的端口（6379），所以你可以通过宿主机的 IP 地址和端口直接访问 Redis 服务。因为 Redis Desktop Manager 连接的是宿主机的端口，并且这个端口已经被 Docker 映射到容器内的 Redis 端口，所以它不受宿主机防火墙规则的影响。
3. **防火墙规则**：
   - 宿主机的防火墙可能只会限制对直接访问宿主机端口的流量，而 Docker 内部的网络和映射规则允许你从宿主机内部访问到容器内部的服务。这意味着，即使宿主机的防火墙开启，只要 Docker 映射的端口允许访问，Redis Desktop Manager 还是能够连接到 Redis 容器。

#### 总结

- **SSH Tunnel** 通过在安全的 SSH 通道中转发流量，从而绕过了直接的防火墙限制。
- **Docker 容器的端口映射** 允许你从宿主机直接访问容器服务，而不受到宿主机防火墙的直接限制。



#### 通过redis去实现分布式session方案一：SpringSession实现分布式session

添加spring-sesson依赖＋配置redis信息



#### 通过redis去实现分布式session方案二：redis存储用户信息

 其实第二种方案不能称之为分布式session了，因为不会用到session了，相当于把用户信息从session中提取出来存到redis中。



### 优化登录功能

通过MVC 即 ArgumentResolver 不用每次都判断用户信息，可以直接在Controller里获取用户信息

MVC配置类

```
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    //自定义的拦截器对象
    @Autowired
    private UserArgumentResolver userArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

}
```

自定义用户参数：

```
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        //session用于获取cookie以及用户信息   model用于跳转页面时，从上一个页面传到下一个页面的信息的传递  ticket cookie的值，我们可以通过注解@CookieValue 名为userTicket的拿到
        if(StringUtils.isEmpty(ticket)){//如果ticket的值为空，则说明未登录
            return null;
        }
    //这里需修改成从redis获取User对象 信息
    //    User user = (User)session.getAttribute(ticket);//通过Key ticket获取到session中存储的用户的信息
        return userService.getUserByCookie(ticket, request,response);
    }
}
```





## 秒杀功能

商品表、秒杀表、秒杀订单表、订单表

#### 数据库

```
#商品表
CREATE TABLE `t_goods`(
                          `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
                          `goods_name` VARCHAR(255) DEFAULT NULL COMMENT '商品名称',
                          `goods_title` VARCHAR(255) DEFAULT NULL COMMENT '商品标题',
                              `goods_img` VARCHAR(300) DEFAULT NULL COMMENT '商品图片',
                              `goods_detail` LONGTEXT COMMENT '商品详情',
                              `goods_price` DECIMAL(10,2) DEFAULT '0.00' COMMENT '商品价格',
                              `goods_stock` INT(11) DEFAULT '0' COMMENT '商品库存，-1表示没有限制',
                              PRIMARY KEY(`id`)
)ENGINE = INNODB AUTO_INCREMENT=3 DEFAULT CHARSET= utf8mb4;

#订单表
CREATE TABLE `t_order`(
                          `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
                          `user_id` BIGINT(20) DEFAULT NULL COMMENT '用户ID',
                          `goods_id` BIGINT(20) DEFAULT NULL COMMENT '商品ID',
                          `delivery_addr_id` BIGINT(20) DEFAULT NULL  COMMENT '收货地址ID',
                          `goods_name` VARCHAR(255) DEFAULT NULL COMMENT'冗余过来的商品名称',
                          `goods_count` INT(11)DEFAULT '0' COMMENT '商品数量',
                          `goods_price` DECIMAL(10,2) DEFAULT '0.00' COMMENT '商品单价',
                              `order_channel` TINYINT(4) DEFAULT '0' COMMENT '1pc, 2android,3ios',
                          `status` TINYINT(4) DEFAULT '0' COMMENT '订单状态，0新建未支付，1已支付，2已发货，3已收货，4己退款，5已完成',
                              `create_date` datetime DEFAULT NULL COMMENT '订单的创建时间',
                          `pay_date` datetime DEFAULT NULL COMMENT '支付时间',
                              PRIMARY KEY(`id`)
)ENGINE = INNODB AUTO_INCREMENT=3 DEFAULT CHARSET= utf8mb4;

#秒杀商品表
CREATE TABLE `t_seckill_goods`(
                                  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀商品ID',
                                  `goods_id` BIGINT(20) DEFAULT NULL COMMENT '商品ID',
                                  `seckill_price` DECIMAL(10,2) DEFAULT '0.00' COMMENT '秒杀价',
                                      `stock_count` INT(10) DEFAULT NULL COMMENT '库存数量',
                                      `start_date` datetime DEFAULT NULL COMMENT '秒杀开始时间',
    `end_date` datetime DEFAULT NULL COMMENT '秒杀结束时间',
    PRIMARY KEY(`id`)
)ENGINE = INNODB AUTO_INCREMENT=3 DEFAULT CHARSET= utf8mb4;

#秒杀订单表
CREATE TABLE `t_seckill_order`(
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀订单ID',
	`user_id` BIGINT(20) DEFAULT NULL COMMENT '用户ID',
    `order_id` BIGINT(20) DEFAULT NULL COMMENT '订单ID',
    `goods_id` BIGINT(20) DEFAULT NULL COMMENT '商品ID',
    PRIMARY KEY(`id`)
)ENGINE = INNODB AUTO_INCREMENT=3 DEFAULT CHARSET= utf8mb4;

INSERT  INTO t_goods (id,goods_name,goods_title,goods_img,goods_detail,goods_price,goods_stock) values (1,'IPHONE 12 64GB', 'IPHONE12 64GB', '/img/iphone12.jpg', 'IPHONE 12 64GB', '6299.00', 100),
(2,'IPHONE12 PRO 128GB', 'IPHONE12 PRO 128GB', '/img/iphone12pro.jpg', 'IPHONE 12 PRO 128GB', '9299.00', 100);

INSERT  INTO t_seckill_goods (id,goods_id,seckill_price,stock_count,start_date,end_date) values (1,1, '4299.87', 10, '2024-09-01 08:00:00','2024-09-01 09:00:00'),
(2,2, '6299.87', 10, '2024-09-01 08:00:00','2024-09-01 09:00:00');
```







### 实现商品列表页

商品名称、商品图片、商品原价、秒杀价、库存数量、详情

```
SELECT
    g.id,
    g.goods_name,g.goods_title,g.goods_img,
    g.goods_detail,g.goods_price,g.goods_stock,
    sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date
FROM
	t_goods g
	LEFT J0IN t_seckill_goods AS sg ON g.id = sg.goods_id

```







### 实现商品详情页

商品名称、商品图片、秒杀开始时间、商品原价、秒杀价、库存数量

```
SELECT
    g.id,
    g.goods_name,g.goods_title,g.goods_img,g.goods_detail,g.goods_price,g.goods_stock,
    sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date
FROM
	t_goods g
	LEFT J0IN t_seckill_goods As sg oN g.id = sg.goods_id
WHERE
	g.id = #{goodsId}

```





### 秒杀倒计时

时间格式化：在实体类中的时间字段上添加@JsonFormat注解

```
@RequestMapping("/toDetail/{goodsId}")
public String toDetail(Hodel model,User user,@PathVariable Long goodsId){
    model.addAttribute( "user" , user);
    GoodsVo goodsVo = goodsService.findGoodsVoBy6oodsId(goodsId);
    Date startDate = goodsVo.getstartDate();
    Date endDate = goodsVo. getEndDate();
    Date nowDate = new Date();
    //秒杀状态
    int secKillStatus = 0;
    //秒杀倒计时
    int remainSeconds = 0;
    //秒杀还未开始
    if (nowDate.before(startDate)){
        remainSeconds = ((int) ((startDate.getTime() - nowDate.getTime())/ 1000));
    }else if (nowDate.after(endDate)){
        //秒杀已结束
        secKillStatus = 2;
        remainSeconds = -1;
    }else {
        //秒杀中
        secKillstatus = 1;
        remainSeconds = 0;
    }
    model.addAttribute( "remainSeconds" , remainSeconds);
    model.addAttribute( "secKillstatus" ,seckillstatus);
    model.addAttribute( "goods" , goodsVo);
	return "goodsDetail";
}

```



### 秒杀按钮

```
<tr>
	<td>秒杀开始时间</td>
	<td th:text="${#dates.format(goods.startDate, ' vvvy-MN-dd HH:mm:ss')}"></td>
    <td id="seckillTip">
		<input type="hidden" id="remainseconds" th:value="$iremainSeconds}">
        <span th:if="${seckillStatus eq 0}">秒杀倒计时:<span id="countDown" th:text="${remainSeconds}"></span>秒
		</span>
		<span th:if="${secKillStatus eq 1}">秒杀进行中</span>
        <span th: if="$isecKillStatus eq 2}">秒杀已结束</span>
    </td>
</tr>


<script>
    $ (function (){
    	countDown();
    });
    
    function countDown(){
        var remainSeconds = $("#remainSeconds" ).val();
        var timeout;
        //秒杀还未开始
        if (remainseconds > 0){
        	timeout = setTimeout(function (){
                 $("#countDown" ).text(remainSeconds - 1);
        		$("#remainSeconds" ).val(remainSeconds - 1);
                countDown();
        },1000) ;
        //秒杀进行中
        }else if (remainSeconds == 0){
    		if (timeout){
   			 clearTimeout(timeout);
            }
            $("#seckillTip").html("秒杀进行中")
        }else {
		   $("#seckil1Tip").html("秒杀已经结束");
		}
    };
</script>

```



```
<td>
    <form id="secKillForm" method="post" action="/seckill/doSeckill">
        <input type="hidden" name="goodsId" th: value="${goods.id}">
        <button class="btn btn-primary btn-block" type="submit" id="buyButton">立即秒杀</button>
    </form>
</td>

<script>
    $ (function (){
    	countDown();
    });
    
    function countDown(){
        var remainSeconds = $("#remainSeconds" ).val();
        var timeout;
        //秒杀还未开始
        if (remainseconds > 0){
             $("#buyButton" ).attr("disabled",true);
        	timeout = setTimeout(function (){
                 $("#countDown" ).text(remainSeconds - 1);
        		$("#remainSeconds" ).val(remainSeconds - 1);
                countDown();
        },1000) ;
        //秒杀进行中
        }else if (remainSeconds == 0){
             $("#buyButton" ).attr("disabled",false);
    		if (timeout){
   			 clearTimeout(timeout);
            }
            $("#seckillTip").html("秒杀进行中")
        }else {
            $("#buyButton" ).attr("disabled",true);
		   $("#seckil1Tip").html("秒杀已经结束");
		}
    };
</script>

```





### 秒杀功能实现

库存够不够、用户不能重复秒杀

```
@RequestMapping("/doSecKill")
public String doSeckill(Model model,User user,Long goodsId) {
    if (user == null) {
        return "login" ;
    }
    model.addAttribute("user", user);
    GoodsVo goods = goodsservice.findGoodsVoByGoodsId(goodsId);
    //判断库存
    if (goods.getstockCount() < 1) {
        model.addAttribute(attributeName: "errmsg"，RespBeanEnum.EINIPTY_STOcK.getNessage());
        return "secKillFail";
    }
	//判断是否重复抢购
Seckill0rder seckill0rder = seckillorderService.getone(new QueryWrapper<Seckill0rder>().eq( "user_id"，user.getId
()).eq("goods_id",goodsId));
    if (seckill0rder != null) {
    	model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage())
        return "secKillFail";
    }
    Order order = orderservice.seckill(user, goods);
    model.addAttribute("order",order);
    model.addAttribute("goods",goods);
    return "orderDetail" ;
}

```

```
@Override
public Order seckill(User user, GoodsVo goods) {
    //秒杀商品表减库存
    SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWIrapper<SeckillGoods>().eq("goods_id",goods.getId()));
    seckillGoods.setStockCount(seckillGoods.getstockCount()-1);
    seckillGoodsService. updateById(seckillGoods) ;
    //生成订单
    Order order = new Order();
    order.setUserId(user.getId());
    order.setGoodsId(goods.getId();
    order.setDeliveryAddrId(0L);  
   	order.setGoodsName(goods.getGoodsName());
    order.setGoodsCount(1);
    order.setGoodsPrice(seckillGoods.getseckillPrice());
    order.set0rderChannel(1);
    order.setstatus(0);
    order.setCreateDate(new Date());
    orderMapper.insert(order);
    //生成秒杀订单
    SeckillOrderr seckillOrder = new SeckillOrder();
    seckillOrder.setuserId(user.getId());
    seckillOrder.setOrderId(order.getId());
    seckillOrder.setGoodsId(goods.getId());
    seckillOrderService.save(seckil1Order);
   return order;
}

```







## 系统压测

项目并发多少--->这种说法不太准确

其实应该是，当项目并发为x时，QPS或者TPS为y

QPS：每秒查询率，一台服务器每秒查询次数，特定的查询服务器在规定时间内所处理流量多少的衡量标准【类似于吞吐量】

TPS：事务/秒，软件测试结果的测量单位，一个事务：一个客户机向服务器发送请求，服务器做出响应的过程，客户机在发出请求时开始计时，收到服务器响应之后结束计时（TPS请求流程一般是：用户请求服务器，服务器内部查询等操作处理请求，服务器返回响应）每秒完成多少个这样的过程，TPS



- jmeter入门

  

- 安装mysql

  

- 自定义变量（测试用户）

  

- 正式压测







### JMeter的使用





linux安装mysql、修改密码、配置远程连接、新建数据库并添加数据

项目打包，发布到linux上并在linux通过jmeter来测试

测试计划：

- 添加 -> 线程 -> 线程组
  线程属性：线程数、Ramp-Up时间（几秒钟之内启动线程数）、循环次数
- 添加 -> 配置元件 -> HTTP请求默认值
  Web服务器：协议：HTTP、IP地址：localhost、端口：8080
- 添加 -> 取样器 -> HTTP请求
  HTTP请求路径
- 添加 -> 监听器 -> 查看结果数、聚合报告、用表格查看结果



在Linux里运行JMeter

- 在Linux里安装MySQL，或将项目地址改成本机地址
- 把项目打包成jar扔到服务器上
- 把JMeter扔到服务器上去，解压之后直接使用
- 通过命令把运行脚本（Windows版本的线程组测试脚本创建好）扔进去
- 将生成的报告文件扔出来放到Windows版本里查看







### 配置同一用户测试

添加 -> 取样器 -> HTTP请求

- HTTP请求：添加参数：名称、值





### 配置不同用户测试

添加 -> 配置元件 -> CSV Data Set Config

添加 -> 配置元件 -> HTTP Cookie管理器

添加 -> 取样器 -> HTTP请求

此处发现问题：

1. Linux和Windows下的优化前QPS差距过大
2. 库存出现负数，出现超卖问题
3. 秒杀结束后，别人知道秒杀接口仍然可以进行秒杀下单



18856018296 + 41e61c40baa14cb99493739af80cb758

18737046676 + f68ff90c00544516b86d72a25e38b5a3

正式的压力测试：准备不同的用户，用不同的用户同时压测我们的接口（暂时选择测试：商品列表和秒杀的接口）

商品列表：windows优化前QPS：2000.2/sec  linux优化前QPS：1027.7/sec

秒杀接口：windows优化前QPS：1091.6/sec  linux优化前QPS：1104.5/sec





写个工具类准备5000个不同的用户，放到数据库，让他们去登录并生成userTicket，然后写入到config.txt中





## 优化

因为高并发系统瓶颈在数据库：

 根本解决方案加缓存！！！在保证数据一致性的前提下加缓存！

**页面优化技术：**

 1、页面缓存+URL缓存+对象缓存

 因为内存redis里面比DB要快，所以最好加各种各样的缓存，尽量少访问DB。

 对象级缓存，如果有更新一定要记得把数据库和缓存一起更新，这样的才能保证数据一致性。

 2、页面静态化，前后端分离

 最近比较火的，Restful 风格：前后端分离，在本项目中前端就是html+Ajax ，只传输动态参数，也就是VO对象。然后前端拿到数据后通过Ajax进行渲染。把页面等静态资源缓存在客户端，这样前端和后端之间的交互就只传输需要的参数就行。并不需要后端模板引擎吧页面渲染好然后把整个页面传到前端，极大的减少了服务器的压力和网络带宽的压力。

 3、静态资源优化

 js/css压缩，减小流量。

 多个js/css组合。减少连接数。

 4、CDN优化（未涉及）

**接口优化技巧：**

 1、Redis预减库存减少数据库的访问。

 2、内存标记减少Redis访问。（即：如果预减库存那一步已经把flag置位，表示没有商品了。那就不应该访问redis了。这样以后的请求就可以直接返回秒杀失败，从而减少redis的压力）。

 3、RabbitMQ队列异步下单，增强用户体验。原理见：秒杀过程

**安全优化：**

 1、秒杀接口地址隐藏：

 为了防止别人提前得到接口，通过机器人来刷单。所以必须得等到秒杀开始时候才能点击按钮，此时再去请求真正的地址。然后再进行秒杀。

 2、数学公式验证码：

 数学验证码或者其他什么选字验证码之类的，主要是也是为了防止机器人刷单比如按键脚本什么的。但是对于用户来说的话不太友好。因为太麻烦了。所以这个看情况使用。

 3、接口防刷

 主要就是防止按键脚本之类的。疯狂点击秒杀按钮。这样疯狂发送请求的话，容易给服务器带来很大的压力。所以我们对每个用户进行了限定。比如每个用户1秒钟内或者3秒钟内只能点击5次按钮。超过规定的次数。就返回点击太频繁的异常提示。后台接口不处理业务，直接返回异常。这样的话可以很大程度上减少服务器的压力。

 具体实现：使用redis缓存服务存储每个user在一段时间内的访问次数。设置一个key和过期时间。如果在过期时间内次数超过规定次数，那就返回点击频繁异常。不进行任何操作。








## 页面优化 主要是前两个优化

**QPS最大的瓶颈在于数据库的操作，可以将数据库的操作提取出来放入缓存，（前提是该缓存数据频繁被读取且变更比较少）**



### 第一个优化：添加缓存（添加页面缓存）

最简单的优化，不仅仅是秒杀方面的优化 ----> 添加缓存

做缓存的话，一般针对那些频繁被读取，变更也比较少的数据做缓存（但是做缓存需要考虑与数据库的一致性问题）







缓存分类：区别在于缓存的粒度  是粗粒度缓存还是细粒度缓存



- 页面缓存 放到redis里面做缓存

不是跳转页面了，而是直接返回一个页面，并把这个页面放到redis缓存里面

先从redis中读取缓存，读取之后发现有页面的话，直接返回给浏览器，没有的话，手动渲染模板然后存入redis中

并且把结果输出到浏览器端

```
 @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response){

        ValueOperations valueOperations = redisTemlate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //如果都没问题的话  就把用户信息传到前端跳转页面去
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        // 修改后 Spring Boot3.0：
        JakartaServletWebApplication jakartaServletWebApplication = JakartaServletWebApplication.buildApplication(request.getServletContext());
        WebContext ctx = new WebContext(jakartaServletWebApplication.buildExchange(request, response), request.getLocale(), model.asMap());
        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", ctx);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList", html, 60, TimeUnit.MINUTES);
        }
//        return "goodsList";
        return html;
    }//cookie的写法
```



- url缓存 

跟页面缓存原理类似，针对商品详情页面来说：传过来的goodsId不同，页面显示的内容就不同，针对这个url进行缓存

虽然我们把它放在redis里面做了缓存，加快了他的读取速度，但是我们从后端处理完了之后发送给前端时，仍然需要发送整个页面，传输数据量很大，所以后面会做前后端分离（对象缓存之后）

```
    @RequestMapping(value = "/toDetail/{id}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(Model model, User user, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response){
        ValueOperations valueOperations = redisTemlate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + id);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(id);
        LocalDateTime startDate = goodsVo.getStartDate();
        LocalDateTime endDate = goodsVo.getEndDate();
        LocalDateTime nowDate = LocalDateTime.now();
        int seckillStatus = 0;
        long remainSeconds = 0;
        System.out.println(nowDate);
        System.out.println(startDate);
        System.out.println(endDate);
        if(nowDate.isBefore(startDate)){
            remainSeconds = Duration.between( nowDate, startDate).getSeconds();
        }
        else if(nowDate.isAfter(endDate)){
            seckillStatus = 2;
            remainSeconds = -1;
        }else{
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("goods", goodsVo);
        JakartaServletWebApplication jakartaServletWebApplication = JakartaServletWebApplication.buildApplication(request.getServletContext());
        WebContext ctx = new WebContext(jakartaServletWebApplication.buildExchange(request, response), request.getLocale(), model.asMap());
        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", ctx);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail:" + id, html, 60, TimeUnit.SECONDS);
        }
//        return "goodsDetail";
        return html;
    }
```



- 对象缓存

  页面缓存应用场景较为局限，应用场景大多是：不太会变更的列表页面，详情页面等等，正常来说，商品列表都会有对应的分页，那如果说分页100页，就需要缓存100页吗？很明显不合适。

  对象缓存是更加细粒度的缓存，主要是针对对象进行缓存。之前分布式会话中，为了解决session在分布式的场景下会出现登录用户识别不一致的问题，使用redis缓存了User用户。现在用户是一直存在redis中并且永不失效的。但是如果用户修改了密码等信息，（当数据库中的数据更新时，一定要记得更新redis中的信息，保证数据的一致性）

```
@Override
public RespBean updatePassword(String userTicke,String password,HttpServletRequest request,
                               HttpservletResponse response) {
    User user = getUserByCookie(userTicket,request,response);
    if (user == null) {
        throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
    }
    user.setPassword(MD5Util.inputPassToDBPass(password,user.getslat()));
    int result = userMapper.updateById(user);
    if (1 == result) {
        //删除Redis
        redisTemplate.delete("user: " + userTicket);
        return RespBean.success();
    }
    return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
}

```



**************************************************************************************************************************************************************************************************************************************************************************************************************************************

商品列表：windows优化后QPS：5381.4/sec  （linux优化后QPS：null）

**************************************************************************************************************************************************************************************************************************************************************************************************************************************





- 页面静态化：前后端分离

为什么要做页面静态化也就是前后端分离呢？因为不这样的话，就像现在使用的是thymeleaf模板，使用thymeleaf模板，每次浏览器请求的时候都要从服务器端获取到数据，拼接成模板渲染返回给我们的浏览器，即使加了页面缓存，但是中间传输的时候还是传输一整个模板引擎，**所以最后肯定要做页面静态化**，前端就是一些html页面，里面一些动态的数据，我们才会通过服务端发给前端，前端一些基本上不会变更的东西就是静态的。前后端分离后，出现了很多前端框架：vue、react.....







### 第二个优化：页面静态化

为什么要做页面静态化也就是前后端分离呢？因为不这样的话，就像现在使用的是thymeleaf模板，使用thymeleaf模板，每次浏览器请求的时候都要从服务器端获取到数据，拼接成模板渲染返回给我们的浏览器，即使加了页面缓存，但是中间传输的时候还是传输一整个模板引擎，所以最后肯定要做页面静态化，前端就是一些html页面，里面一些动态的数据，我们才会通过服务端发给前端，前端一些基本上不会变更的东西就是静态的。前后端分离后，出现了很多前端框架：vue、react.....

做一个异步处理，渲染和请求分开做，然后拿到结果后再套入进去

页面跳转到公共的返回对象，进行返回，通过静态页面跳转，并通过ajax获取静态数据，调接口获取数据，手动渲染





后端

```
@RequestMapping(value = "/toDetail/{goodsId}")
@ResponseBody
public RespBean toDetail(User user,@PathVariable Long goodsId) {
    GoodsVo goodsVo = goodsService.findGoodsVoBy6oodsId(goodsId);
    Date startDate = goodsVo.getstartDate();
    Date endDate = goodsVo. getEndDate();
    Date nowDate = new Date();
    //秒杀状态
    int secKillStatus = 0;
    //秒杀倒计时
    int remainSeconds = 0;
    //秒杀还未开始
    if (nowDate.before(startDate)){
        remainSeconds = ((int) ((startDate.getTime() - nowDate.getTime())/ 1000));
    }else if (nowDate.after(endDate)){
        //秒杀已结束
        secKillStatus = 2;
        remainSeconds = -1;
    }else {
        //秒杀中
        secKillstatus = 1;
        remainSeconds = 0;
    }
    DetailVo detailVo = new DetailVo();
    detailVo.setUser(user);
    detailVo.setGoodsVo(goodsVo);
    detailVo.setSecKillstatus(seckillstatus);
    detailVo.setRemainSeconds(remainSeconds);
    return RespBean.success(detailVo);
}

```



前端：

```
<script>
$(function(){
	// countDown();
    // alert("1");
    getDetail();
});

function doSeckill(){
    $.ajax({
        url:"/seckill/doSeckill",
        type:"POST",
        data:{
            id:$("#goodsId").val()
        },
        // contentType: "application/json", // 设置请求头 后端期望参数为表单格式而非对象类型的JSON数据
        success:function (response){
            if (response.code == 200) {
                // alert(response.obj);
                var orderId = response.obj.order.id;
                var seckillGoodsId = response.obj.goods.id;
                // alert(orderId);
                // alert(seckillGoodsId)
                window.location.href="/orderDetail.html?orderId=" + orderId +"&seckillGoodsId=" + seckillGoodsId;
            }else{
                alert("秒杀商品失败：" + response.message);
            }
        },
        error:function (){
            layer.msg("客户端请求出错！");
            alert("秒杀商品失败，请稍后再试");
        }
    })
}

function getDetail(){
    let id = g_getQueryString("id");
    // alert(id);
    $.ajax({
        url:"/goods/detail/" + id,
        type: "GET",
        contentType: "application/json", // 设置请求头
        success:function (response){
            if (response.code == 200) {
                // alert(response.obj);
                render(response.obj);
            }else{
                alert("加载商品详情失败：" + response.message);
            }
        },
        error:function (){
            layer.msg("客户端请求出错！");
            alert("加载商品详情失败，请稍后再试");
        }
    })
}

function render(goodsRes){
    // alert("ok");
    var user = goodsRes.user;
    var goods = goodsRes.goodsVo;
    var seckillStatus = goodsRes.seckillStatus;
    var remainSeconds = goodsRes.remainSeconds;
    if(user){
        $("#userTipr").hide();
    }
    $("#goodsName").text(goods.goodsName);
    $("#goodsImg").attr("src", goods.goodsImg);
    $("#startTime").text(moment.tz(goods.startDate, 'YYYY-MM-DD HH:mm:ss', 'Asia/Shanghai').format('YYYY-MM-DD HH:mm:ss'));
    $("#remainSeconds").val(remainSeconds);
    $("#goodsId").val(goods.id);
    $("#goodsPrice").text(goods.goodsPrice);
    $("#seckillPrice").text(goods.seckillPrice);
    $("#stockCount").text(goods.stockCount);
    countDown();
}



function countDown(){
	var remainSeconds = $("#remainSeconds").val();
	var timeout;
	if(remainSeconds > 0){//秒杀还没开始，倒计时
		$("#buyButton").attr("disabled", true);
        $("#seckillTip").html("秒杀倒计时:" + remainSeconds)
		timeout = setTimeout(function(){
			// $("#countDown").text(remainSeconds - 1);
			$("#remainSeconds").val(remainSeconds - 1);
			countDown();
		},1000);
	}else if(remainSeconds == 0){//秒杀进行中
		$("#buyButton").attr("disabled", false);
		if(timeout){
			clearTimeout(timeout);
		}
		$("#seckillTip").html("秒杀进行中");
	}else{//秒杀已经结束
		$("#buyButton").attr("disabled", true);
		$("#seckillTip").html("秒杀已经结束");
	}
}

</script>
```







### 解决库存超卖

[秒杀系统常见问题—如何避免库存超卖？_秒杀系统_做梦都在改BUG_InfoQ写作社区](https://xie.infoq.cn/article/b17581181718be762bfaf113d)

减库存 -> 生成订单 -> 生成秒杀订单

而解决库存超卖需要做一些判断，判断商品库存是否大于0，判断时间节点是当你进行更新操作时，即更新操作时先判断库存

- 给数据库加唯一索引，防止重复购买【采用 用户id+商品id的唯一索引，解决同一个用户秒杀多个商品问题，虽然性能降低但是解决超卖问题】

  ![](E:\Learning\JAVA\JAVA_project_ideafile\java_redis_HighConcurrency_seckill\yuyu-seckill\md_image\database_order_index_unique.jpg)

- **扣库存用sql语句处理，同时判断库存大于0**【[秒杀系统常见问题—如何避免库存超卖？_秒杀系统_做梦都在改BUG_InfoQ写作社区](https://xie.infoq.cn/article/b17581181718be762bfaf113d)】

- 从Redis中判断是否重复抢购



```
@RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(User user, @RequestBody SeckillRequest request){
        if(null == user){
            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(request.getId());
        //判断库存大于0，才能进行秒杀---->防止库存超卖
        if(goods.getStockCount() < 1){
            return  RespBean.error(RespBeanEnum.STOCK_IS_EMPTY);
        }
        //判断该用户是否重复抢购
        System.out.println(user.getId());
        System.out.println("判断该用户是否重复抢购");
//        SeckillOrder seckillOrder = seckillOrderService.getSeckillOrder(user.getId(), goods.getGoodsId());
        //采用 用户id+商品id的唯一索引，解决同一个用户秒杀多件同一商品问题，虽然性能降低但是解决超卖问题
        // 因为虽然能在这一步判断该用户是否重复抢购 但是如果是高并发场景下两个线程都配置了同一用户，
        // 同时执行该判断，都判断未抢购，则会出现重复下单情况
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getGoodsId());
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.SECKILL_FAIL_REPEAT);
        }
        Order order= seckillOrderService.add(user, goods);
        SeckillGoodsRes response = SeckillGoodsRes.init(goods, order);
        return RespBean.success(response);
    }
```



```
@Override
    @Transactional(rollbackFor = Exception.class)
    public Order add(User user, GoodsVo goods) {

        SeckillGoods seckillGoods = seckillGoodsMapper.selectById(goods.getId());
        if(seckillGoods.getStockCount() < 1){
            return null;
        }
//        int res = seckillGoodsMapper.updateById(seckillGoods);
        //防止库存超卖，做出调整
        UpdateWrapper<SeckillGoods> wrapper = new UpdateWrapper<SeckillGoods>().setSql(
                "stock_count = " + "stock_count - 1")
                .eq("id", goods.getId())
                .gt("stock_count", 0);
        int res = seckillGoodsMapper.update(wrapper);
        if(res < 1){
            return null;
        }


        Order order = Order.init(user.getId(), seckillGoods.getGoodsId(), 0L, goods.getGoodsName(), 1, seckillGoods.getSeckillPrice(), (byte) 1, (byte) 0);
        orderMapper.insert(order);

        SeckillOrder seckillOrder = SeckillOrder.init(user.getId(), order.getId(), seckillGoods.getGoodsId());
        seckillOrderMapper.insert(seckillOrder);

        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getGoodsId(), seckillOrder);//解决库存超卖，将生成的秒杀订单在redis中进行缓存

        return order;
    }

```

以上可发现优化后的QPS提升并不大，因为库存卖完后在判断同一个用户重复下单时放到了Redis，速度更快





### 还可继续优化的点

第三个优化：静态资源优化（略）比如说把一些资源css, js, img都可以提前把他们放在另外的地方，提前把他们做一些相应的处理。

第四个优化：CDN优化（略）







## 接口优化-服务优化



### RabbitMQ安装以及和springboot的集成

默认端口：15672；默认用户名密码：guest

SpringBoot整合RabbitMQ

- 引入依赖

  ```
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-amqps</artifactId>
  </dependency>
  ```

- 配置文件

  ```
  #RabbitMQ
  rabbitmq:
  	#服务器
      host: 192.168.1.128
      #用户名
      username: guest
      #密码
      password: guest
      #虚拟主机
      virtual-host: /
      #端口
      port: 5672
      listener:
      	simple:
              #消费者最小数量
              concurrency: 10
              #消费者最大数量
              max-concurrency: 10
              #限制消费者每次只处理一条消息，处理完再继续下一条消息  能者多劳
              prefetch: 1
              #启动时是否默认启动容器，默认true
              auto-startup: true
              #被拒绝时重新进入队列
              default-requeue-rejected: true
      template:
          retry:
              #发布重试，默认false
              enabled: true
              #重试时间，默认1000ms
              initial-interval: 1000ms
              #重试最大次数，默认3次
              max-attempts: 3
              #重试最大问隔时间，默认10000ms
              max-interval: 1000@ms
          	#重试的间隔乘数。比如配2.0，第一次就等10s，第二次就等20s，第三次就等40s
          	multiplier: 1
  ```

- 配置类

  ```
  @Configuration
  public class RabbitMQConfig {
      @Bean
          public Queue queue(){
          return new Queue("queue",true);
      }
  }
  ```

- 消息发送者

  ```
  @Service
  @Slf4j
  public class MQSender {
      @Autowired
      private RabbitTemplate rabbitTemplate;
      public void send(Object msg) {
          log.info("发送消息:" +msg);
          rabbitTemplate.convertAndSend( "queue", msg);
      }
  }
  ```

- 消息消费者

  ```
  @Service
  @Slf4j
  public class MQReceiver {
      @RabbitListener(queues = "queue")
      public void receive(object msg) {
          log.info("接收消息:" +msg );
      }
  }
  ```

- Controller

  ```
  @Autowired
  private MQSender mqSender;
  /**
  *测试发送Rabbit消息
  */
  @RequestMapping( "/mq")
  @ResponseBody
  public void mq(){
  mqSender.send("Hello");
  }
  ```











### RabbitMQ交换机模式

交换机：一边接收来自生产者的消息，一边将消息推送到队列，交换机必须确切的知道如何处理接收到的消息，他的规则由交换机类型定义（direct、topic 、headers、fanout）

- Fanout模式（广播模式、发布订阅模式）

  - 消息不仅仅被一个队列接收，而是能够被多个队列接收，多个队列接收的是同一个生产者发送的同一条消息
  -  可以有多个队列
  - 每个队列都要绑定到Exchange（交换机）
  - 生产者发送的消息，只能发送到交换机，交换机来决定要发给哪个队列，生产者无法决定
  - 交换机把消息发送给绑定过的所有队列
  - 订阅队列的消费者都能拿到消息
  - 在广播模式下，消息发送流程是这样的：

  ```
  @Configuration
  public class RabbitMQConfig {
  	//fanout
      private static final String FANOUTQUEUE01 = "fanout.queue01";
      private static final String FANOUTQUEUE02 = "fanout.queue02";
      private static final String FANOUTEXCHANGE = "fanout.exchange";
  	
      //fanout
      @Bean
      public FanoutExchange fanoutExchange(){
          return new FanoutExchange(FANOUTEXCHANGE);
      }
  
      @Bean
      public Queue fanoutQueue1(){
          return new Queue(FANOUTQUEUE01, true);
      }
  
      /**
       * 绑定队列和交换机
       */
      /**
       * 另一种写法
       * @Bean
       * public Binding binding01(){
       *     return BindingBuilder.bind(queue01()).to(fanoutExchange());
       * }
       */
      @Bean
      public Binding bindingFanoutQueue1(Queue fanoutQueue1, FanoutExchange fanoutExchange){
          return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
      }
  
      @Bean
      public Queue fanoutQueue2(){//如果这个函数名字不叫fanoutQueue2，则bindingQueue2中参数编译报错：could not autowire,There is more than one bean of 'Queue' type.
          return new Queue(FANOUTQUEUE02, true);
      }
      /**
       * 绑定队列和交换机
       */
      /**
       * 另一种写法
       * @Bean
       * public Binding binding02(){
       *     return BindingBuilder.bind(queue02()).to(fanoutExchange());
       * }
       */
      @Bean
      public Binding bindingFanoutQueue2(Queue fanoutQueue2, FanoutExchange fanoutExchange){
          return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
      }
  }
  ```

​		

```
@Service
@Slf4j
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void sendFanout(String msg){
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("fanout.exchange", "", msg);//发送消息到指定广播模式交换机
        //rabbitTemplate.convertAndSend("fanout.exchange", "", msg);三个参数exchange routingKey message
    }
}
```

```
@Service
@Slf4j
public class RabbitMQReceiver {

    //发布订阅-fanout广播消息模型
    @RabbitListener(queues = "fanout.queue01")
    public void listenFanoutQueue1(String msg){
        log.info("从fanoutqueue01接收消息：" + msg);
    }

    @RabbitListener(queues = "fanout.queue02")
    public void listenFanoutQueue2(String msg){
        log.info("从fanoutqueue02接收消息：" + msg);
    }
}
```





- Direct模式（路由模式）在Fanout模式中，一条消息，会被所有订阅的队列都消费。但是，在某些场景下，我们希望不同的消息被不同的队列消费。这时就要用到Direct类型的Exchange。
  - 消息去到队列，绑定一个key，明确匹配了路由key
  - 所有发送到Direct的j消息都会被转发到路由key中指定的一个Queue
  - Direct可以使用RabbitMQ自带的交换机

 在Direct模型下：

- 队列与交换机的绑定，不能是任意绑定了，而是要指定一个`RoutingKey`（路由key）
- 消息的发送方在 向 Exchange发送消息时，也必须指定消息的 `RoutingKey`。
- Exchange不再把消息交给每一个绑定的队列，而是根据消息的`Routing Key`进行判断，只有队列的`Routingkey`与消息的 `Routing key`完全一致，才会接收到消息

```
@Service
@Slf4j
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendDirect(String msg, String routingKey){
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("direct.exchange", routingKey, msg);//发送消息到指定广播模式交换机
        //rabbitTemplate.convertAndSend("fanout.exchange", "", msg);三个参数exchange routingKey message
    }
  
}
```

```
@Service
@Slf4j
public class RabbitMQReceiver {



    //发布订阅-direct直接路由消息模型
    /**
     * 基于@Bean的方式声明队列和交换机比较麻烦，Spring还提供了基于注解方式来声明。
     *
     * 在consumer的SpringRabbitListener中添加两个消费者，同时基于注解来声明队列和交换机：
     *
     */

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue01"),
            exchange = @Exchange(name = "direct.exchange", type = ExchangeTypes.DIRECT),
            key = {"1","2"}
    ))
    public void listenDirectQueue1(String msg){
        log.info("从direct.queue01接收消息：" + msg);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue02"),
            exchange = @Exchange(name = "direct.exchange", type = ExchangeTypes.DIRECT),
            key = {"1","3"}
    ))
    public void listenDirectQueue2(String msg){
        log.info("从direct.queue02接收消息：" + msg);
    }
}
```



- Topic模式（主题模式） (常用)

  - 为方便管理路由key引入通配符（#（匹配零个或多个）、*（匹配明确的一个））

  ```
  @Configuration
  public class RabbitMQConfig {
  
      //topic
      private static final String TOPICQUEUE01 = "topic.queue01";
      private static final String TOPICQUEUE02 = "topic.queue02";
      private static final String TOPICEXCHANGE = "topic.exchange";
      private static final String TOPICROUTINGKEY01 = "china.#";
      private static final String TOPICROUTINGKEY02 = "*.news";
  
      //topic
  
      @Bean
      public TopicExchange topicExchange(){
          return new TopicExchange(TOPICEXCHANGE);
      }
  
      @Bean
      public Queue topicQueue1(){
          return new Queue(TOPICQUEUE01, true);
      }
  
  
      /**
       * 绑定队列和交换机
       */
      /**
       * 另一种写法
       * @Bean
       * public Binding binding01(){
       *     return BindingBuilder.bind(queue01()).to(fanoutExchange());
       * }
       */
      @Bean
      public Binding bindingTopicQueue1(Queue topicQueue1, TopicExchange topicExchange){
          return BindingBuilder.bind(topicQueue1).to(topicExchange).with(TOPICROUTINGKEY01);
      }
  
      @Bean
      public Queue topicQueue2(){//如果这个函数名字不叫fanoutQueue2，则bindingQueue2中参数编译报错：could not autowire,There is more than one bean of 'Queue' type.
          return new Queue(TOPICQUEUE02, true);
      }
      /**
       * 绑定队列和交换机
       */
      /**
       * 另一种写法
       * @Bean
       * public Binding binding02(){
       *     return BindingBuilder.bind(queue02()).to(fanoutExchange());
       * }
       */
      @Bean
      public Binding bindingTopicQueue2(Queue topicQueue2, TopicExchange topicExchange){
          return BindingBuilder.bind(topicQueue2).to(topicExchange).with(TOPICROUTINGKEY02);
      }
  }
  ```

  ```
  @Service
  @Slf4j
  public class RabbitMQSender {
      
      public void sendTopic(String msg, String routingKey){
          log.info("发送消息：" + msg);
          rabbitTemplate.convertAndSend("topic.exchange", routingKey, msg);//发送消息到指定广播模式交换机
          //rabbitTemplate.convertAndSend("fanout.exchange", "", msg);三个参数exchange routingKey message
      }
      
  }
  ```

  ```
  @Service
  @Slf4j
  public class RabbitMQReceiver {
  
      //发布订阅-topic主题路由消息模型
      @RabbitListener(queues = "topic.queue01")
      public void listenTopicQueue1(String msg){
          log.info("从topic.queue01接收消息" + msg);
      }
  
      @RabbitListener(queues = "topic.queue02")
      public void listenTopicQueue2(String msg){
          log.info("从topic.queue02接收消息" + msg);
      }
  
  }
  ```

  

- Headers模式

  ```
  @Configuration
  public class RabbitMQConfig {
  
      //headers
      private static final String HEADERSQUEUE01 = "headers.queue01";
      private static final String HEADERSQUEUE02 = "headers.queue02";
      private static final String HEADERSEXCHANGE = "headers.exchange";
  //    private static final String HEADERSROUTINGKEY01 = "china.#";
  //    private static final String HEADERSROUTINGKEY02 = "*.news";
  
  
  
      //headers
  
      @Bean
      public HeadersExchange headersExchange(){
          return new HeadersExchange(HEADERSEXCHANGE);
      }
  
      @Bean
      public Queue headersQueue1(){
          return new Queue(HEADERSQUEUE01, true);
      }
  
  
      /**
       * 绑定队列和交换机
       */
      /**
       * 另一种写法
       * @Bean
       * public Binding binding01(){
       *     return BindingBuilder.bind(queue01()).to(fanoutExchange());
       * }
       */
      @Bean
      public Binding bindingHeadersQueue1(Queue headersQueue1, HeadersExchange headersExchange){
          Map<String, Object> map = new HashMap<>();
          map.put("color", "red");
          map.put("size", "small");
          return BindingBuilder.bind(headersQueue1).to(headersExchange).whereAny(map).match();
      }
  
  
  
      @Bean
      public Queue headersQueue2(){//如果这个函数名字不叫fanoutQueue2，则bindingQueue2中参数编译报错：could not autowire,There is more than one bean of 'Queue' type.
          return new Queue(HEADERSQUEUE02, true);
      }
      /**
       * 绑定队列和交换机
       */
      /**
       * 另一种写法
       * @Bean
       * public Binding binding02(){
       *     return BindingBuilder.bind(queue02()).to(fanoutExchange());
       * }
       */
      @Bean
      public Binding bindingHeadersQueue2(Queue headersQueue2, HeadersExchange headersExchange){
          Map<String, Object> map = new HashMap<>();
          map.put("color", "red");
          map.put("size", "big");
          return BindingBuilder.bind(headersQueue2).to(headersExchange).whereAll(map).match();
      }
  }
  ```

  ```
  @Service
  @Slf4j
  public class RabbitMQSender {
  
      public void sendHeadersTwo(String msg){
          log.info("发送queue01&queue02消息：" + msg);
          MessageProperties messageProperties = new MessageProperties();
          messageProperties.setHeader("color", "red");
          messageProperties.setHeader("size", "big");
          Message message = new Message(msg.getBytes(), messageProperties);
          rabbitTemplate.convertAndSend("headers.exchange", "", message);//发送消息到指定广播模式交换机
          // rabbitTemplate.convertAndSend("fanout.exchange", "", msg);三个参数exchange routingKey message
      }
  
      public void sendHeadersQueue01(String msg){
          log.info("发送queue01消息：" + msg);
          MessageProperties messageProperties = new MessageProperties();
          messageProperties.setHeader("color", "red");
          messageProperties.setHeader("size", "midle");
          Message message = new Message(msg.getBytes(), messageProperties);
          rabbitTemplate.convertAndSend("headers.exchange", "", message);//发送消息到指定广播模式交换机
          //rabbitTemplate.convertAndSend("fanout.exchange", "", msg);三个参数exchange routingKey message
      }
  
  }
  ```

  ```
  @Service
  @Slf4j
  public class RabbitMQReceiver {
  
      //发布订阅-headers头部路由消息模型
      @RabbitListener(queues = "headers.queue01")
      public void listenHeadersQueue1(Message msg){
          log.info("从headers.queue01接收消息" + msg);
          log.info("从headers.queue01接收消息" + new String(msg.getBody()));
      }
  
      @RabbitListener(queues = "headers.queue02")
      public void listenHeadersQueue2(Message msg){
          log.info("从headers.queue02接收消息" + msg);
          log.info("从headers.queue02接收消息" + new String(msg.getBody()));
      }
  
  }
  ```

  

测试类：

```
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RabbitMQSender rabbitMQSender;


    /**
     * 测试页面跳转
     * @param model
     * @return
     */
    @RequestMapping("/hello")
    public String hello(Model model){
        model.addAttribute("name", "yuyu");
        return "hello";
    }


    /**
     * 测试RabbitMQ----发送消息
     */
    @RequestMapping("/mq")
    @ResponseBody
    public void mq(){
        rabbitMQSender.send("Hello RabbitMQ!");
    }

    /**
     * 测试RabbitMQ----Fanout发送消息
     */
    @RequestMapping("/fanout")
    @ResponseBody
    public void fanoutTest(){
        rabbitMQSender.sendFanout("Hello RabbitMQ!");
    }

    /**
     * 测试RabbitMQ----direct发送消息
     */
    @RequestMapping("/direct")
    @ResponseBody
    public void directTest(){
        rabbitMQSender.sendDirect("Hello RabbitMQ!", "1");
    }


    /**
     * 测试RabbitMQ----topic发送消息
     */
    @RequestMapping("/topic")
    @ResponseBody
    public void topicTest(){
        rabbitMQSender.sendTopic("Hello RabbitMQ china.news!", "china.news");//queue01&queue02
        rabbitMQSender.sendTopic("Hello RabbitMQ china.news.queue1!", "china.news.queue1");//queue01
        rabbitMQSender.sendTopic("Hello RabbitMQ queue2.news!", "queue2.news");//queue02
        rabbitMQSender.sendTopic("Hello RabbitMQ abandon.queue2.news!", "abandon.queue2.news");//丢弃
    }


    /**
     * 测试RabbitMQ----headers发送消息
     */
    @RequestMapping("/headers")
    @ResponseBody
    public void headersTest(){
        rabbitMQSender.sendHeadersTwo("Hello RabbitMQ queue01&02!");//queue01&queue02
        rabbitMQSender.sendHeadersQueue01("Hello RabbitMQ queue01!");//queue01

    }
}
```



### 1.Redis预减库存（减少对数据库的访问）

### 2.进一步还可以通过内存标记减少对redis的访问

### 3.请求进入RabbitMQ，进行异步下单。秒杀操作

封装了一个消息对象，通过RabbitMQ发送消息对象，在监听者里做了之前在Controller里做的事（判断库存、判断是否重复抢购、下单操作），使用RabbitMQ变成了异步操作，可以在Controller中快速返回，进行一个流量削峰的作用





收到请求 --> redis预减库存 --> 库存不足则返回秒杀失败，库存够的话也并非立即去数据库进行下单操作 --> 而是请求进入RabbitMQ消息队列 --> 立即返回给客户端正在排队中的响应 --> 异步操作（异步生成订单，真正的减少数据库中的库存【同时要更新redis保证数据一致性吗？？？】） --> 客户端轮询是否真正的下单成功，有订单信息，如果有的话，秒杀成功，反之失败

在系统初始化的时候就把商品库存加载到redis中去，当收到秒杀请求时，redis就去预减库存。



```
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitMQSender sender;
    private Map<Long, Boolean> memoryMarkEmptyStock = new HashMap<>();

    /**
     *
     * @param user
     * @param request
     * @return  orderId:下单成功  -1:秒杀失败  0:排队中
     *
     */
    @RequestMapping(value = "/querySeckillResult", method = RequestMethod.POST)
    @ResponseBody
    public RespBean querySeckillResult(User user, @RequestBody SeckillRequest request){
        if(Objects.isNull(user)){
            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, request.getGoodsId());
        return RespBean.success(orderId);

    }


    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(User user, @RequestBody SeckillRequest request){
        if(null == user){
            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        //通过内存标记减少redis的访问
        if(memoryMarkEmptyStock.get(request.getId())){
            return  RespBean.error(RespBeanEnum.STOCK_IS_EMPTY);
        }

        //先判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + request.getGoodsId());
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.SECKILL_FAIL_REPEAT);
        }
        //redis预减库存
        //decrement()  递减  如果value是数字类型的话  每调用一次就会减一  并且递增递减都是原子性的  stockCountResult是递减之后的库存
        Long stockCountResult = redisTemplate.opsForValue().decrement("SeckillGoods:" + request.getId());
        if(stockCountResult < 0){//判断库存大于等于0，才能进行秒杀---->防止库存超卖
            memoryMarkEmptyStock.put(request.getId(), true);
            redisTemplate.opsForValue().increment("SeckillGoods:" + request.getId());//秒杀失败，恢复库存
            return  RespBean.error(RespBeanEnum.STOCK_IS_EMPTY);
        }
        //下单 用RabbitMQ 可以从之前的代码中看出下单需要 用户对象信息和商品信息  所以需要去封装一个发送Message的对象，携带这两个信息
        RabbitMQSeckillMessage message = new RabbitMQSeckillMessage(user, request.getId());
        sender.sendDoSeckill(JSON.toJSONString(message), "doSeckill.create.order");
        return RespBean.success(0);//0正在排队中  然后转到MQ消费者  去消费
    }

    /**
     * 系统初始化时，可以把商品库存信息加载到redis中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.findGoodsVo();
        if(Collections.isEmpty(goodsVoList)){
            return;
        }
        goodsVoList.forEach(goods -> {
            if(goods.getStockCount() > 0){
                memoryMarkEmptyStock.put(goods.getId(), false);
            }else{
                memoryMarkEmptyStock.put(goods.getId(), true);
            }
            redisTemplate.opsForValue().set("SeckillGoods:" + goods.getId(), goods.getStockCount());
        });
    }

}
```



```
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RabbitMQSeckillMessage {
    private User user;

    private Long id;
}
```



```
@Configuration
public class RabbitMQConfig {

    //topic
    private static final String SECKILLTOPICQUEUE = "seckill.topic.queue";
    private static final String SECKILLTOPICEXCHANGE = "seckill.topic.exchange";
    private static final String SECKILLTOPICROUTINGKEY = "doSeckill.#";



    //topic

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(SECKILLTOPICEXCHANGE);
    }

    @Bean
    public Queue seckillTopicQueue(){
        return new Queue(SECKILLTOPICQUEUE, true);
    }

    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding bindingTopicQueue(Queue seckillTopicQueue, TopicExchange topicExchange){
        return BindingBuilder.bind(seckillTopicQueue).to(topicExchange).with(SECKILLTOPICROUTINGKEY);
    }
}
```



```
@Service
@Slf4j
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void sendDoSeckill(String msg, String routingKey){
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("seckill.topic.exchange", routingKey, msg);//发送消息到指定广播模式交换机
        //rabbitTemplate.convertAndSend("fanout.exchange", "", msg);三个参数exchange routingKey message
    }
}
```



```
@Service
@Slf4j
public class RabbitMQReceiver {


    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillOrderService seckillOrderService;


    //发布订阅-topic主题路由消息模型  实现异步下单操作
    @RabbitListener(queues = "seckill.topic.queue")
    public void listenSeckillTopicQueue(String msg){
        log.info("从topic.queue接收消息" + msg);
        RabbitMQSeckillMessage message = JSON.parseObject(msg, RabbitMQSeckillMessage.class);
        Long id = message.getId();
        User user = message.getUser();
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(id);
        if(goods.getStockCount() < 1){
            redisTemplate.opsForValue().set("SeckillResult" + user.getId() + ":" + goods.getGoodsId(), true);//秒杀失败
            return ;//库存不足
        }
        //为了防止并发情况下，同一用户两个线程由于并发操作导致生成了两个订单（重复抢购）
        //先判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getGoodsId());
        if(seckillOrder != null){
            redisTemplate.opsForValue().set("SeckillResult" + user.getId() + ":" + goods.getGoodsId(), true);//秒杀失败
            return ;//重复抢购
        }
        //下单
        seckillOrderService.add(user, goods);
    }
}
```



SeckillOrderServiceImpl.add()  && getResult():

```
@Override
    @Transactional(rollbackFor = Exception.class)
    public Order add(User user, GoodsVo goods) {

        SeckillGoods seckillGoods = seckillGoodsMapper.selectById(goods.getId());
        if(seckillGoods.getStockCount() < 1){
            redisTemplate.opsForValue().set("SeckillResult" + user.getId() + ":" + goods.getGoodsId(), true);//秒杀失败
            return null;
        }
//        int res = seckillGoodsMapper.updateById(seckillGoods);
        //防止库存超卖，做出调整
        UpdateWrapper<SeckillGoods> wrapper = new UpdateWrapper<SeckillGoods>().setSql(
                "stock_count = " + "stock_count - 1")
                .eq("id", goods.getId())
                .gt("stock_count", 0);
        int res = seckillGoodsMapper.update(wrapper);
        if(res < 1){
            redisTemplate.opsForValue().set("SeckillResult" + user.getId() + ":" + goods.getGoodsId(), true);//秒杀失败
            return null;
        }
        Order order = Order.init(user.getId(), seckillGoods.getGoodsId(), 0L, goods.getGoodsName(), 1, seckillGoods.getSeckillPrice(), (byte) 1, (byte) 0);
        orderMapper.insert(order);

        SeckillOrder seckillOrder = SeckillOrder.init(user.getId(), order.getId(), seckillGoods.getGoodsId());
        seckillOrderMapper.insert(seckillOrder);

        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getGoodsId(), seckillOrder);//解决库存超卖，将生成的秒杀订单在redis中进行缓存
        return order;
    }
    
    
    
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            return seckillOrder.getOrderId();
        }else if((Boolean) redisTemplate.opsForValue().get("SeckillResult" + user.getId() + ":" + goodsId)){
            return -1L;
        }
        return 0L;//正在排队
    }
```



如果库存相应的访问等问题搞定后，接下来就要处理下单的操作，此时还是不能让所有的请求都直接去访问数据库，所以此时可以用到队列，请求先进入队列进行缓冲。然后通过队列进行异步下单。







### 客户端轮训查询秒杀结果

前端：

```
function doSeckill(){
    $.ajax({
        url:"/seckill/doSeckill",
        type:"POST",
        data:JSON.stringify({
            id:$("#id").val(),
            goodsId:$("#goodsId").val()
        }),
        contentType: "application/json", // 设置请求头 后端期望参数为表单格式而非对象类型的JSON数据
        success:function (response){
            if (response.code == 200) {
                // alert(response.obj);
                // var orderId = response.obj.order.id;
                // var seckillGoodsId = response.obj.goods.id;
                // alert(orderId);
                // alert(seckillGoodsId)
                // window.location.href="/orderDetail.html?orderId=" + orderId +"&seckillGoodsId=" + seckillGoodsId;
                getResult($("#goodsId").val());
            }else{
                alert("秒杀商品失败：" + response.message);
            }
        },
        error:function (){
            layer.msg("客户端请求出错！");
            alert("秒杀商品失败，请稍后再试");
        }
    })
}


function getResult(goodsId){
    g_showLoading();
    $.ajax({
        url:"/seckill/querySeckillResult",
        type:"POST",
        data:JSON.stringify({
            goodsId:goodsId
        }),
        contentType: "application/json", // 设置请求头 后端期望参数为表单格式而非对象类型的JSON数据
        success:function (response){
            if (response.code == 200) {
                var result = response.obj;
                if(result < 0){
                    layer.msg("秒杀商品失败：" + response.message);
                }else if(result == 0){//还在排队状态，要轮询
                    setTimeout(function (){
                        getResult(goodsId);
                    }, 50);
                }else{
                    layer.confirm("恭喜您，秒杀成功！查看订单？",{btn:["确定", "取消"]},
                    function (){
                        window.location.href="/orderDetail.html?orderId=" + result +"&seckillGoodsId=" + $("#id").val();
                    },
                    function (){
                        layer.close();
                    })
                }

            }else{
                alert("秒杀商品失败：" + response.message);
            }
        },
        error:function (){
            layer.msg("客户端请求出错！");
            alert("秒杀商品失败，请稍后再试");
        }
    })
}
```



**************************************************************************************************************************************************************************************************************************************************************************************************************************************

商品列表：windows优化后QPS：4981.7/sec  （linux优化后QPS：null）  具体数值还会受到系统目前运行状态/情况有所不同  测性能差异最好是在间隔不太久的同一个时间段内

**************************************************************************************************************************************************************************************************************************************************************************************************************************************



### Redis实现分布式锁

redis锁--> 框架redission？？

spring自带的data redis ---> redisTemplate

redis性能受网络影响较大----> 如果涉及锁，多个操作变成原子性---> lua脚本  redis内置对lua脚本的支持，lua脚本可以在redis服务端原子性的执行多个redis的命令  （提前在redis端写好lua脚本，然后在java客户端去调用；或者在java客户端写好lua脚本然后需要执行时，将脚本发送到redis端去执行）

- Redis的递增递减本身带有原子性

- Redis分布式锁，锁本身是个占位的意思，当线程进来操作发现已经占位即放弃或稍候再使用，当前线程执行完毕释放锁

  ```
  @Bean
  public DefaultRedisscript<Boolean> script(){
      DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
      //lock.luα脚本位置利application.yml同级目录
      redisScript.setLocation(new classPathResource("lock.lua"));
      redisScript.setResultType(Boolean.class);
      return redisscript;
  }//redisConfig
  
  ```

  redisLock.lua

  ```
  if redis.call("get", KEYS[1])==ARGV[1] then
      return redis.call("del", KEYS[1])
  else
      return 0
  end
  ```

  ```
  @SpringBootTest
  public class YuyuSeckillApplicationTests {
  
      @Autowired
      private RedisTemplate redisTemplate;
      @Autowired
      private RedisScript<Boolean> redisScript;
  
      @Test
      public void textLock01() {
          ValueOperations valueOperations = redisTemplate.opsForValue();
          Boolean isLock = valueOperations.setIfAbsent("k1", "v1");//当当前设置的key存在的情况下是设置不成功的，只有当该key不存在才能设置成功
  
          //这样会存在多线程删除不属于自己的Key的情况
  
          if (isLock){
              valueOperations.set("name", "yuyu1");
              System.out.println((String) valueOperations.get("name"));
  //            System.out.println(1/0);//模拟异常
              redisTemplate.delete("k1");
          }else{
              System.out.println("有其他线程正在使用，请稍等");
          }
      }
  
  
      @Test
      public void textLock02() {//当当前设置的key存在的情况下是设置不成功的，只有当该key不存在才能设置成功
          ValueOperations valueOperations = redisTemplate.opsForValue();
          //配置超时时间 避免一直握锁 导致死锁
          //给锁添加一个过期时间，防止应用在运行过程中抛出异常导致锁无法正常释放
          Boolean isLock = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
  
          //这样会存在多线程删除不属于自己的Key的情况
  
          if (isLock){
              valueOperations.set("name", "yuyu1");
              System.out.println(1/0);//模拟异常
              System.out.println((String) valueOperations.get("name"));
              redisTemplate.delete("k1");
          }else{
              System.out.println("有其他线程正在使用，请稍等");
          }
      }
  
      @Test
      public void textLock03() {//当当前设置的key存在的情况下是设置不成功的，只有当该key不存在才能设置成功
          ValueOperations valueOperations = redisTemplate.opsForValue();
          String value = UUID.randomUUID().toString();
          //配置超时时间 避免一直握锁 导致死锁
          Boolean isLock = valueOperations.setIfAbsent("k1",value, 120, TimeUnit.SECONDS);
  
          //这样会存在多线程删除不属于自己的Key的情况
  
          if (isLock){
              valueOperations.set("name", "yuyu1");
  //            System.out.println(1/0);//模拟异常
              System.out.println((String) valueOperations.get("name"));
              Boolean res = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), value);
              System.out.println(res);
          }else{
              System.out.println("有其他线程正在使用，请稍等");
          }
      }
  
  }
  ```

  





### 优化Redis预减库存

采用分布式锁优化预减缓存

```
@Bean
public DefaultRedisscript<Long> script(){
    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
    //lock.luα脚本位置利application.yml同级目录
    redisScript.setLocation(new classPathResource("lock.lua"));
    redisScript.setResultType(Long.class);
    return redisscript;
}//redisConfig

```

redisLockStock.lua

```
if (redis.call("exists", KEYS[1]) == 1) then
    local stock = tonumber(redis.call("get",KEYS[1]))
    if(stock > 0) then
        redis.call("incrby",KEYS[1],-1)
        return stock
    end
    return 0
end
```

SeckillController.java

```
@RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
@ResponseBody
public RespBean doSeckill(User user, @RequestBody SeckillRequest request)里面，修改一行
```

```
//        Long stockCountResult = redisTemplate.opsForValue().decrement("SeckillGoods:" + request.getId());
        //进一步优化redis预减库存    使用lua脚本  redis分布式锁
        Long stockCountResult = (Long) redisTemplate.execute(script, Collections.singletonList("SeckillGoods:" + request.getId()),Collections.EMPTY_LIST);
        
```



微服务---> 限流（其他秒杀接口优化方案）



## 安全优化

黄牛脚本 --> 接口安全性

1.隐藏接口地址：秒杀开始时，并不会直接去调用秒杀的地址，而是需要获取真正的秒杀接口地址，并且这个地址根据不同用户秒杀的不同商品，其秒杀地址是不一样的。避免脚本抢购

缺点：还是有人能用脚本获取到该秒杀地址



2.验证码  过滤掉一些请求 另一方面还能分流



3.接口限流  限制请求1s中多少次





### 秒杀地址隐藏

前端：

```
<button class="btn btn-primary btn-block" type="button" id="buyButton" onclick="getSeckillPath()">立即秒杀
```

```
function getSeckillPath(){
    g_showLoading();
    $.ajax({
        url:"/seckill/getPath",
        type:"POST",
        data:JSON.stringify({
            id:$("#id").val(),
            goodsId:$("#goodsId").val()
        }),
        contentType: "application/json", // 设置请求头 后端期望参数为表单格式而非对象类型的JSON数据
        success:function (response){
            if (response.code == 200) {
                var seckillPathStr = response.obj;
                doSeckill(seckillPathStr);
            }else{
                layer(response.message);
            }
        },
        error:function (){
            layer.msg("客户端请求出错！");
        }
    })

}
function doSeckill(seckillPathStr){
    $.ajax({
        url:"/seckill/" + seckillPathStr + "/doSeckill",
        type:"POST",
        data:JSON.stringify({
            id:$("#id").val(),
            goodsId:$("#goodsId").val()
            // path: path  避免明文传输  所以使用pathVariable
        }),
        contentType: "application/json", // 设置请求头 后端期望参数为表单格式而非对象类型的JSON数据
        success:function (response){
            if (response.code == 200) {
                getResult($("#goodsId").val());
            }else{
                alert("秒杀商品失败：" + response.message);
            }
        },
        error:function (){
            layer.msg("客户端请求出错！");
            alert("秒杀商品失败，请稍后再试");
        }
    })
}
```

后端：

```
/**
     * 获取秒杀地址
     * @param user
     * @param request
     * @return
     */
    @RequestMapping(value = "/getPath", method = RequestMethod.POST)
    @ResponseBody
    public RespBean getPath(User user, @RequestBody SeckillRequest request){
        if(Objects.isNull(user)){
            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        String path = seckillGoodsService.createSeckillPath(user, request);
        return RespBean.success(path);
    }
```

```
@Override
    public String createSeckillPath(User user, SeckillRequest request) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "GoodsId:" + request.getGoodsId() + "SeckillGoodsId:" + request.getId());//生成接口地址随机值
        //接口地址生成之后需要存起来，因为在真正秒杀时还要对秒杀地址做校验
        //存入redis 并设置失效时间为 1 minute 失效后需要获取新的秒杀地址
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + request.getGoodsId(), str, 60, TimeUnit.SECONDS);
        return str;
    }
```

```
@RequestMapping(value = "/{seckillPathStr}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(User user, @PathVariable String seckillPathStr, @RequestBody SeckillRequest request){
        if(null == user){
            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        if(StringUtils.isEmpty(seckillPathStr)){
            return RespBean.error(RespBeanEnum.SECKILL_PATH_NULL_ERROR);
        }
        if (!seckillPathStr.equals((String)redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + request.getGoodsId()))){
            return RespBean.error(RespBeanEnum.SECKILL_PATH_VALIDATION_ERROR);
        }//其实我觉得此处的校验应该这样理解 比如生成SeckillPath时，不使用随机值，或者记录下随机值，
        // 然后用用户ID 商品ID和随机值重新进行md5加密，与传进来的seckillPathStr进行比较
        // （而将生成的SeckillPath用用户ID和商品ID标记存入redis就实现了这个想法）



        //通过内存标记减少redis的访问
        if(memoryMarkEmptyStock.get(request.getId())){
            return  RespBean.error(RespBeanEnum.STOCK_IS_EMPTY);
        }

        //先判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + request.getGoodsId());
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.SECKILL_FAIL_REPEAT);
        }
        //redis预减库存
        //进一步优化redis预减库存    使用lua脚本  redis分布式锁
        Long stockCountResult = (Long) redisTemplate.execute(script, Collections.singletonList("SeckillGoods:" + request.getId()),Collections.EMPTY_LIST);
        if(stockCountResult < 0){//判断库存大于等于0，才能进行秒杀---->防止库存超卖
            memoryMarkEmptyStock.put(request.getId(), true);
            redisTemplate.opsForValue().increment("SeckillGoods:" + request.getId());//秒杀失败，恢复库存
            return  RespBean.error(RespBeanEnum.STOCK_IS_EMPTY);
        }
        //下单 用RabbitMQ 可以从之前的代码中看出下单需要 用户对象信息和商品信息  所以需要去封装一个发送Message的对象，携带这两个信息
        RabbitMQSeckillMessage message = new RabbitMQSeckillMessage(user, request.getId());
        sender.sendDoSeckill(JSON.toJSONString(message), "doSeckill.create.order");
        return RespBean.success(0);//0正在排队中  然后转到MQ消费者  去消费
    }
```





### 验证码

#### 生成验证码：



```
<!--        验证码项目引入easy-captcha -->
        <dependency>
            <groupId>com.github.whvcse</groupId>
            <artifactId>easy-captcha</artifactId>
            <version>1.6.2</version>
        </dependency>
        <!--nashorn引擎依赖、JDK17中没有-->
<!--        https://blog.csdn.net/m0_69519887/article/details/140420512-->
        <dependency>
            <groupId>org.openjdk.nashorn</groupId>
            <artifactId>nashorn-core</artifactId>
            <version>15.4</version>
        </dependency>
```



```
/**
     * 在秒杀商品时生成验证码
     *
     * @param
     * @param response
     * @throws IOException
     */
    @RequestMapping("/captcha")
    public void captcha(User user, Long goodsId, HttpServletResponse response){
        if(Objects.isNull(user)){
            throw new GlobalException(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        // 设置请求头为输出图片类型
//        response.setContentType("image/jpg");
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);//永不失效

        // 三个参数分别为宽、高、位数
        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(130, 48, 5);
        arithmeticCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置

        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, arithmeticCaptcha.text(), 5, TimeUnit.MINUTES);
        try {
            arithmeticCaptcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }
```



```
<div class="row">
     <div class="form-inline">
           <img id="captchaImg" onclick="refreshVerifyCode()" width="130" height="32" style="display: none">
           <input id="captcha" class="form-control" style="display: none">
           <button class="btn btn-primary" type="button" id="buyButton" onclick="getSeckillPath()">立即秒杀
                  <input type="hidden" name="id" id="goodsId" />
                  <input type="hidden" name="id" id="id" />
           </button>
     </div>
</div>

function refreshVerifyCode(){
    $("#captchaImg").attr("src", "/seckill/captcha?goodsId="+$("#goodsId").val()+"&timestamp="+new Date().getTime());
}




function countDown(){
	var remainSeconds = $("#remainSeconds").val();
	var timeout;
	if(remainSeconds > 0){//秒杀还没开始，倒计时
		$("#buyButton").attr("disabled", true);
        $("#seckillTip").html("秒杀倒计时:" + remainSeconds)
		timeout = setTimeout(function(){
			// $("#countDown").text(remainSeconds - 1);
			$("#remainSeconds").val(remainSeconds - 1);
			countDown();
		},1000);
	}else if(remainSeconds == 0){//秒杀进行中
        $("#seckillTip").html("秒杀进行中");
        $("#captchaImg").attr("src", "/seckill/captcha?goodsId="+$("#goodsId").val()+"&timestamp="+new Date().getTime());
        $("#captchaImg").show();
        $("#captcha").show();
		if(timeout){
			clearTimeout(timeout);
		}

    }else{//秒杀已经结束
		$("#buyButton").attr("disabled", true);
		$("#seckillTip").html("秒杀已经结束");
        $("#captchaImg").hide();
        $("#captcha").hide();
	}
}


```



#### 校验验证码：



```
@RequestMapping(value = "/getPath", method = RequestMethod.POST)
    @ResponseBody
    public RespBean getPath(User user, @RequestBody SeckillRequest request){
        if(Objects.isNull(user)){
            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        boolean check = seckillGoodsService.checkCaptcha(user, request.getGoodsId(), request.getCaptcha());
        if (!check){
            return RespBean.error(RespBeanEnum.VERIFY_CODE_ERROR);
        }
        String path = seckillGoodsService.createSeckillPath(user, request);
        return RespBean.success(path);
    }
```

```
@Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(StringUtils.isEmpty(captcha) || Objects.isNull(user)){
            return false;
        }
        if(redisTemplate.hasKey("captcha:" + user.getId() + ":" + goodsId)){
            String verifyCaptcha = (String)redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
            return verifyCaptcha.equals(captcha);
        }
        return false;
    }
```



```
function getSeckillPath(){
    g_showLoading();
    $.ajax({
        url:"/seckill/getPath",
        type:"POST",
        data:JSON.stringify({
            id:$("#id").val(),
            goodsId:$("#goodsId").val(),
            captcha:$("#captcha").val()
        }),
        contentType: "application/json", // 设置请求头 后端期望参数为表单格式而非对象类型的JSON数据
        success:function (response){
            if (response.code == 200) {
                var seckillPathStr = response.obj;
                doSeckill(seckillPathStr);
            }else{
                layer.msg(response.message);
            }
        },
        error:function (){
            layer.msg("客户端请求出错！");
        }
    })

}
```





其实这里我发现了一个问题：就是假如我从商品list点进id=1的秒杀商品的详情页面，打算进行秒杀，点进去时，还处于秒杀进行中的状态，但是马上还有10几秒秒杀就结束了，我不主动返回并重新进入该秒杀商品详情页面的话，是不会检测到秒杀已经结束了的，因为在秒杀商品详情页面并没有处理（距离秒杀结束还有多久）这个时间值。所以此时即使秒杀已经结束了，我还是能秒杀下单成功。



还有个问题是：

商品详情页：当验证码输入框为空时，立即秒杀的按钮，无法点击，移到按钮上就提示需要输入验证码才能秒杀，当验证码输入框不为空时 按钮才有效







### 接口限流（接口防刷）

主流的限流算法：计数器、漏斗、令牌桶

计数器算法、漏桶算法、令牌桶算法(常用)



**简单接口限流：**

此处使用简单的计数器算法做限流。比如一分钟只能请求100次，超过100次就得延后再请求了。

每次请求时准备一个计数器，每请求一次，计数器就加1。每过一个规定的时间周期，计数器就会清零，重新开始计数。可以用redis实现。（自增+失效时间）

秒杀接口限流：

```
@RequestMapping(value = "/getPath", method = RequestMethod.POST)
    @ResponseBody
    public RespBean getPath(User user, @RequestBody SeckillRequest request, HttpServletRequest servletRequest){
        if(Objects.isNull(user)){
            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //限制访问次数，5S内最多访问5次
        String uri = servletRequest.getRequestURI();//请求地址
        //为了方便测试接口限流 所以默认将传进来的captcha赋值为"0"，就不用每次真的算一遍验证码然后再传进来了
        request.setCaptcha("0");
        Integer requestCount = (Integer)valueOperations.get(user.getId() + ":" + uri);
        if(Objects.isNull(requestCount)){
            valueOperations.set(user.getId() + ":" + uri, 1, 5, TimeUnit.SECONDS);
        }else if(requestCount < 5){
            valueOperations.increment(user.getId() + ":" + uri);
        }else{
            return RespBean.error(RespBeanEnum.REQUEST_IS_FREQUENCY);
        }
        boolean check = seckillGoodsService.checkCaptcha(user, request.getGoodsId(), request.getCaptcha());
        if (!check){
            return RespBean.error(RespBeanEnum.VERIFY_CODE_ERROR);
        }
        String path = seckillGoodsService.createSeckillPath(user, request);
        return RespBean.success(path);
    }
```





设置的限流的规则一般是：系统能够承受的最大的QPS的70%~80%

临界问题和浪费资源问题！！（临界比如，请求全集中在前一分钟的后30s和后一分钟的前30s  ；  浪费资源比如一分钟内，请求集中在前半分钟，后半分钟服务器处于空闲状态）

特定化：比如针对不同的请求设置不同限流规则：所以需要一个通用的接口限流的代码。【以上操作冗余性大，需要进行优化】







**通用接口限流：**

（AOP  切面编程？？？）

这里使用的是注解＋拦截器实现的

@AccessLimit(second=5,maxCount=5,needLogin=true)

```
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {

    int second();

    int maxCount();

    boolean needLogin() default true;
}
```



```
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<User>();

    /**
     * ThreadLocal 解决线程安全问题 每个线程绑定自己的值 秒杀场景为高并发多线程
     * 不能在线程中公共的存用户信息，会导致错误。
     * 每个线程里面都要存储自己的用户信息（私有数据）
     *访问ThreadLocal这个变量的话，他会获取到自己变量的本地副本
     */
    public static void setUser(User user){
        userHolder.set(user);
    }
    public static User getUser(){
        return userHolder.get();
    }
}
```



```
@Component//当前拦截器对象由Spring创建和管理
@Slf4j
public class AccessLimitInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(handler instanceof HandlerMethod){
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(Objects.isNull(accessLimit)){
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String uri = request.getRequestURI();//请求地址
            if(needLogin){
                if(Objects.isNull(user)){
                    render(response, RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
                    return false;
                }
                uri = user.getId() + ":" + uri;
            }
//            System.out.println(uri);
            ValueOperations valueOperations = redisTemplate.opsForValue();
            //限制访问次数，5S内最多访问5次
            Integer requestCount = (Integer)valueOperations.get(uri);
//            System.out.println(requestCount);
            if(Objects.isNull(requestCount)){
                valueOperations.set(uri, 1, second, TimeUnit.SECONDS);
            }else if(requestCount < maxCount){
                valueOperations.increment(uri);
            }else{
//                System.out.println("ok");
                render(response, RespBeanEnum.REQUEST_IS_FREQUENCY);
                return false;
            }
        }
        return true;
    }

    /**
     * 构建返回对象（JSON数据类型）
     * @param response
     * @param respBeanEnum
     */
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(RespBean.error(respBeanEnum)));
        out.flush();
        out.close();
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response){
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isEmpty(ticket)){//如果ticket的值为空，则说明未登录
            return null;
        }
        return userService.getUserByCookie(ticket, request,response);
    }
}
```



```
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    //自定义的拦截器对象
    @Autowired
    private UserArgumentResolver userArgumentResolver;
    @Autowired
    private AccessLimitInterceptor accessLimitInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInterceptor);
    }
}
```



```
@AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/getPath", method = RequestMethod.POST)
    @ResponseBody
    public RespBean getPath(User user, @RequestBody SeckillRequest request, HttpServletRequest servletRequest){
        if(Objects.isNull(user)){
            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        request.setCaptcha("0");
        boolean check = seckillGoodsService.checkCaptcha(user, request.getGoodsId(), request.getCaptcha());
        if (!check){
            return RespBean.error(RespBeanEnum.VERIFY_CODE_ERROR);
        }
        String path = seckillGoodsService.createSeckillPath(user, request);
        return RespBean.success(path);
    }
```







## 主流秒杀方案分析（实际开发中可能会用到的）

除了减少数据库的访问【收到请求 --> redis预减库存 --> 库存不足则返回秒杀失败，库存够的话也并非立即去数据库进行下单操作 --> 而是请求进入RabbitMQ消息队列 --> 立即返回给客户端正在排队中的响应 --> 异步操作（异步生成订单，真正的减少数据库中的库存） --> 客户端轮询是否真正的下单成功，有订单信息，如果有的话，秒杀成功，反之失败】方案外，还有一些别的方案：增强数据库 ---> 数据库集群（例如alibaba出的中间件mycat对数据库进行分库分表来增强数据库的性能）





秒杀项目需要注意的点：

- 高并发以及刷接口等黑客请求对服务器端的负载冲击

- 高并发所带来的超卖问题
- 高荷载情况下下单的速度和成功率的保障

抢购之前的预约通知：点击预约产生token，token会放在用户的浏览器里，无token的用户只是在前端提示商品不足，获取token的用户可以请求后台，将重复请求前端拦截

抢购开始之前暴露接口。被黑客截取，通过脚本参与秒杀：使用网关，通过网关进行相应的限流，如：黑名单（将IP地址、用户ID），重复请求放在Redis集群，将同一个IP的发起采取拒绝。考虑Redis的性能瓶颈可以做分片，带宽，统一处理

对没有token的用户：尽快处理前面已经获得token的请求，将商品进行卖光，在网关处直接终结请求，每一个Tomcat可做一到两千的QPS，令牌桶发放完就进入下单阶段

对于下单阶段要最快生成订单，否则会出现超时，可使用Redis。考虑Redis的性能可以使用分片，作用是速度快，订单查询可减少对数据库的冲击，同时订单走队列（如RabbitMQ）进行削峰，后端进行消费，入库成功后就可将Redis中的数据删除

出现令牌桶发放超出库存情况采用分布式锁，Redis封装好的分布式锁的方案，针对商品Id加分布式锁，但是如果商品众多，加锁反而会对性能产生影响，对Redis的压力较大

可直接在服务器实例里写好商品数量，在内存里判空，不用走Redis,不用通信，性能较高

使用到微服务采用配置中心，通过配置中心下发每个实例的商品数量，可以后台控制，在抢购开始的时候，通过配置中心下发到每个服务商品数量，当实例将内存中的商品数量消耗完毕，即为卖完了

抢购过程中服务挂掉了，大不了少卖一些，等所有服务卖完，统计订单数量，将剩余库存再次启动，再次售卖






![总结_1](E:\Learning\JAVA\JAVA_project_ideafile\java_redis_HighConcurrency_seckill\yuyu-seckill\md_image\总结_1.jpg)



![总结_2](E:\Learning\JAVA\JAVA_project_ideafile\java_redis_HighConcurrency_seckill\yuyu-seckill\md_image\总结_2.jpg)







在接口限流时，实现令牌桶算法

整合在线支付



MD5 JWT redis(登录前如果之前有旧的token要先从redis中删掉，再存入新的) OSS 第三方支付 前后端分离 mybatis-plus 

库存超卖？？

秒杀进行中也要有倒计时？？？

购物车->订单 第三方支付->订单？？？？

BaseResponse  分页

