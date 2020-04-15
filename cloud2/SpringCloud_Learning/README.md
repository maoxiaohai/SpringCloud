# 微服务创建步骤 
### 一、Eureca Server
创建 Eureka Server分为三步：
#### step1：
引入pom依赖：
```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
<!--一般都是在SpringBoot中使用-->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
```
#### step2：
创建配置文件
```properties
#本服务端口号
server:
  port: 7001
#eureka的配置
eureka:
  instance:
    hostname: eureka_7001.com ##Eureka服务端的实例名称(只是单纯的名称而已)
  client:
    #false表示不向注册中心中注册自己
    register-with-eureka: false
    #false表示自己就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    service-url:
      #设置客户端与Eureka Server之间交互的地址，查询服务和注册服务都需要依赖这个地址
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
      #defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka #集群版
  server:
      #关闭Eureka自我保护机制，保证不可用服务被及时剔除
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 2000
```
#### step3：
使用注解使Eureka起效
```java
@SpringBootApplication
@EnableEurekaServer
public class eureca7001 {
    public static void  main(String[] args){
        SpringApplication.run(eureca7001.class,args);
    }
}
```
#### 测试：访问 127.0.0.1:7001(让服务起来)

### 二、服务提供者和服务消费者(Ribbon)---》》Ribbon是客户端的负载均衡组件，主要是采用轮训的方式实现
Eureka中已经集成了Ribbon
#### 1.服务提供者
##### step1:引入依赖
```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
```
##### step2:创建配置文件
```properties
server:
  port: 8001

spring:
  application:
    name: provider(如果是有多个provider，name这里的name应该保持一致，否则消费者不好调用)

eureka:
  client:
    #表示是否将自己注册进EurekaServer，默认为true
    register-with-eureka: true
    #是否从EurekaServer中抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    fetchRegistry: true
    service-url:
      #defaultZone: http://localhost:7001/eureka #单机版
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka #集群版
  instance:
    instance-id: provider8001 #修改Status名称
    prefer-ip-address: true
    #Eureka客户端向服务端发送心跳的时间间隔，单位为秒(默认30)
    lease-renewal-interval-in-seconds: 10
    #Eureka服务端在收到最后一次心跳后等待的时间上限，单位为秒(默认90)，超时剔除服务
    lease-expiration-duration-in-seconds: 60
```
##### step3:加入注解
```java
@SpringBootApplication
@EnableEurekaClient
public class provider8001 {
    public static void main(String[] args) {
        SpringApplication.run(provider8001.class,args);
    }
}
```
##### step4:创建controller和service层
##### step5:启动两个Eureka服务器和provider，检查服务是否注册成功。访问127.0.0.1:8001/test,检查是否访问成功。
创建provider集群时，创建的步骤是相同的，不同的地方在于端口和instance-id区分开就行。

#### 2.服务消费者
##### step1:引入依赖
provider和consumer都是注册到Eureka上，所以也要引入Eureka-client
```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
```
##### Step2:创建配置文件
```yaml
server:
  port: 80

spring:
  application:
    name: cloud-order-service
eureka:
  client:
    #表示是否将自己注册进EurekaServer，默认为true
    register-with-eureka: true
    #是否从EurekaServer中抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    fetchRegistry: true
    service-url:
      #defaultZone: http://localhost:7001/eureka #单机版
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka #集群版
```
##### Step3:使用注解
```java
@SpringBootApplication
@EnableEurekaClient
public class consumer80 {
    public static void main(String[] args) {
        SpringApplication.run(consumer80.class,args);
    }
}
```
##### Step4:编写业务逻辑以及配置
```java
@Configuration
public class RestConfig {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
```
```java
@RestController
public class orderController {
    public static final String PAYMENT_URL = "http://provider";
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/he")
    public String test(){
        return restTemplate.getForObject(PAYMENT_URL+"/test",String.class);
    }
}
```
### 三、 Swagger的使用
#### step1：依赖引入
```xml
 <!-- 引入 Swagger 依赖 -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>

        <!-- 引入 Swagger UI 依赖，以实现 API 接口的 UI 界面 -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
```
网上也有说可以直接添加第三方的依赖
```xml
	<dependency>
		<groupId>com.spring4all</groupId>
		<artifactId>swagger-spring-boot-starter</artifactId>
		<version>1.7.0.RELEASE</version>
	</dependency>
```
#### step2:配置
```java
@Configuration
@EnableSwagger2  //引入Swagger
public class SwaggerConfig {
    public Docket createResrApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))//指定生成API的路径
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("我是一个标题")
                .description("我是一段描述")
                .version("1.0.0.1")
                .contact(new Contact("mao","127.0.0.1","893987837"))
                .build();
    }
}
```
#### step3:按业务创建Controller
```java
@RestController
@RequestMapping("test")
@Api(tags = "ceshi")
public class testController {
    @ApiOperation(value = "查询")
    @GetMapping("list")
    public String get(){
        return "list";
    }
    @ApiOperation(value = "添加")
    @GetMapping("add")
    public String add(){
        return "add";
    }
    @ApiOperation(value = "删除")
    @GetMapping("delete")
    public String delete(){
        return "delete";
    }
}
```
##### step4:测试， 127.0.0.1:80/he

其中@Api用在类上，标记这是一个文档资源
@ApiOperation用在方法上，标记这是一个Api操作。
#### step4:测试 http://localhost:8080/swagger-ui.html(swagger.html路径可以被修改)
### 四、 OpenFeign的使用
#### Step1:提供依赖
```xml
<!--        Spring  Web-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
<!--        OpenFeign-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
<!--OpenFeign也是客户端负载均衡,需要和注册中心做交互的-->
        <!-- eureka client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```
#### Step2:创建配置文件
```yaml
server:
  port: 80

eureka:
  client:
    register-with-eureka: false
    service-url:
      #defaultZone: http://localhost:7001/eureka #单机版
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka #集群版
```
#### Step3: 创建接口(Controller层可以直接调用接口，接口中的方法名不需要和服务的方法名一致，只要URL一致即可)
```java
@Component
@FeignClient(value = "provider")
public interface FeignService {
    @GetMapping("/test")
    public String dd();
}
```
#### Step4: 使用注解
```java
@SpringBootApplication
@EnableFeignClients
public class openFeign {
    public static void main(String[] args) {
        SpringApplication.run(openFeign.class,args);
    }
}
```
#### Step5:测试 127.0.0.1:80/he

# 问题汇总
### 一、创建eureca-7001项目的时候，导入依赖，pom.xml文件出错
```xml
<!--Spring-Boot-web-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
<!--Eureca-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>

        </dependency>
```
发现子模块eureca中的pom.xml文件报错，并且可以定位到是Eureca的问题。之后上网检查发现eureca依赖并没有倒错，
修改
```xml
<!--Eureca-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
            <version>2.2.1.RELEASE</version>
        </dependency>
```
一切正常。那就是缺少版本指定。子模块的版本号一般都是在父项目中的pom.xml文件中指定的，检查下父项目的pom.xml,发现确实是没有指定版本。
问题解决
。
### 二、eureka服务器名不能出现下划线。
最终发现是
```xml
eureka:
  instance:
    hostname: eureka_7001.com ##Eureka服务端的实例名称(只是单纯的名称而已)
```
发现将下划线去掉就成功了
```xml
eureka:
  instance:
    hostname: eureka7001.com ##Eureka服务端的实例名称(只是单纯的名称而已)
```
### 三、Eureca集群配置DS Replicas只能显示其中一个
Eureca集群中每个Eureca配置都是相同的，唯一的区别在于
```xml
 defaultZone: http://eureka7002.com:7002/eureka/
```
这是eureca7001服务中配置的，而在eureca7002中配置如下：
```xml
 defaultZone: http://eureka7001.com:7001/eureka/
```
如果将eureca7001/2换成127.0.0.1是不行的，个人理解如下：
虽然两个eureca server的hostname是不一样的，但是在平台上显示的还是127.0.0.1(可能可以在什么地方进行配置吧，以后有时间看下源码)，所以个人觉得还是要换成不同的名称。
不然不能识别出不同的server。(虽然在hosts中配置eureca7001/2.com都是指向127.0.0.1，)
### 四、Eureka cilent访问失败(入口函数main和controller必须要在同一个目录下)
启动两个Eureka Server之后，启动provider8001(Eureka client)，检查发现服务注册成功，并且Eureka Server集群构建成功，但是访问localhost:8001失败。
反复检查依赖和配置，均没有发下问题。最后将controller所在的文件夹放在启动类同一个目录下，成功了。原因是注解@SpringBootApplication没有扫描到Controller.
(使用IDEA工具发现容器中并没有controller的bean，所以证实了是没有扫描到)

# 知识点补充
### 一、Maven版本管理
父工程可以管理子模块的版本号。一般创建一个SpringBoot Maven项目的时候，有两种方式可以加入父依赖。
#### 1.通过parent节点。
```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.0.RELEASE</version>
        <relativePath/>
    </parent>
```
在这个公用的父工程中，指定了大部分组件的版本号，当然如果需要改变组件的版本，可以在自己所在模块中指定，这样就可以覆盖掉父工程中的版本号
#### 2.直接引入spring-boot-dependencies
```xml
        <dependencyManagement>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.1.0.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencyManagement>
```
在这里需要注意的是<dependencyManagement>节点是管理组件版本号的，相当于只是声明而不真正的引入依赖。而
```xml
<scope>import</scope>
```
在我看来，就相当于将spring-boot-dependencies 2.1.0版本的jar包直接导入进来，等同于使用
```xml
<parent></parent>
```
的结果。
