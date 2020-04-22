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

### 五、HyStrix的使用(服务熔断和降级)
在微服务中，服务之间的调用非常频繁，但是当其中一个服务不可用之后，就会导致后续的调用全部不可用。hystrix就是要针对这种情况下出现的。
对于熔断和降级理解不是很深刻。(个人理解不管是服务熔断还是降级都是发生在客户端的，因为hystrix主要是针对服务不可用的异常设计的。而熔断和降级就是针对不同的服务不可用而设计的。)
本项目主要是在OpenFeign下使用hystrix。(Feign中已经内嵌了Hystrix)
#### 1.引入依赖
```xml
<!--hystrix-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
<!-- eureca client-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
<!--web-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
<!-- OpenFeign-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
```
#### 2.修改配置文件
```yml
server:
  port: 8001
spring:
  application:
    name: consumer_hystrix8001
eureka:
  client:
    register-with-eureka: true
    service-url:
      #defaultZone: http://localhost:7001/eureka #单机版
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka #集群版
feign:
  hystrix:
    enabled: true
```
#### 3.加入注解
```java
@SpringBootApplication
@EnableFeignClients
//@EnableDiscoveryClient
@EnableEurekaClient
//@EnableHystrix
public class HystrixConsumer8001 {
    public static void main(String[] args) {
        SpringApplication.run(HystrixConsumer8001.class,args);
    }
}
```
#### 4.编写代码
因为在Feign中是面向接口设计的，所以先写个接口
```java
@Component
@FeignClient(value = "provider",fallback = breakImpl.class)
public interface Ibreak {
    @GetMapping("/test")
    public String test();
}
```
使用注解@FeignClient引入Hystrix。fallback，当调用的服务发生异常时，就会调用fallback的值，注意。这里的fallback的值需要
继承feign的接口(这里就是Ibreak)
```java
@Component
public class breakImpl implements Ibreak {
    @Override
    public String test() {
        return "error Code";
    }
}
```
#### 5.测试
首先启动两个Eureka Server，然后启动provider8001和HystrixConsumer8001，调用127.0.0.1:8001/jj，发现调用成功，显示provider8001的数据。
然后关闭provider8001，发现显示的是接口实现类breakImpl的方法。显示"error code"，演示成功。

#### 6.创建新的项目hystrix8001,不使用任何的负载均衡组件，单纯使用hystrix。并且演示延时和服务异常两种情况。
### 六、HyStrixBoard的使用(只是单纯的hystrixdashboard，单机环境下)
#### 1.依赖引入
```xml
 <!--web-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!--hystrix-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
        </dependency>
```
#### 2.配置修改
```yml
server:
  port: 9001
```
#### 3.加入注解
```java
@SpringBootApplication
@EnableHystrixDashboard
public class dashboard {
    public static void main(String[] args) {
        SpringApplication.run(dashboard.class,args);
    }
}
```
#### 4.被监控端的依赖引入
```xml
<!--   actuator     -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```
#### 5.被监控端的配置
在启动类中加入以下代码
```java
    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
```
#### 6.测试
访问127.0.0.1:9001/hystrix,出现监控界面，然后在其中加入被监控服务的url 127.0.0.1:8001/hystrix.stream.最后访问consumer8001的接口，在监控界面中会有调用信息。
### 七、Cloud-Gateway的使用
#### 1.初步使用GateWay
##### 1.step1：引入依赖
由于gateway是由netty+webflux实现的，所以不需要再引入web模块。只需要添加gateway模块。如下所示:
```xml
<!-- gateway-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
    </dependencies>
```
##### 2.step2添加配置文件
```yml
server:
  port: 9527 #服务端口


spring:
  application:
    name: gateway9527  //服务名称
  cloud:
    gateway:
      routes:
        - id: gateway_simple  //自定义的路由ID(唯一,因为可以定义多个路由)，
          uri: http://www.ityouknow.com //匹配的uri
          predicates:
            - Path=/spring-cloud   //路由条件，最终的调用URL为URI+path
```
##### 3.测试：
访问127.0.0.1:9527/spring-cloud(注意这里需要加上spring-cloud)，最终会访问到连接http://www.ityouknow.com/spring-cloud(注意这里的最后也会加上spring-cloud)
### 八、Stream的使用
**1.Stream Provider的使用**
#### step1:依赖引入
```xml
   <!--        引入stream-rabbit-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```
#### step2:配置文件
```yml
server:
  port: 8802

spring:
  application:
    name: cloud-stream-consumer
  cloud:
    stream:
      binders: # 在此处配置要绑定的rabbitmq的服务信息；
        defaultRabbit: # 表示定义的名称，用于于binding整合
          type: rabbit # 消息组件类型
          environment: # 设置rabbitmq的相关的环境配置
            spring:
              rabbitmq:
                host: 127.0.0.1 # 192.168.179.150
                port: 5672
                username: guest
                password: guest
      bindings: # 服务的整合处理
        input: # 这个名字是一个通道的名称
          destination: studyEX # 表示要使用的Exchange名称定义
          content-type: application/json # 设置消息类型，本次为json，文本则设置“text/plain”
          binder: defaultRabbit # 设置要绑定的消息服务的具体设置

eureka:
  client: # 客户端进行Eureka注册的配置
    service-url:
      defaultZone: http://localhost:7001/eureka,http://localhost:7002/eureka
  instance:
    lease-renewal-interval-in-seconds: 2 # 设置心跳的时间间隔（默认是30秒）
    lease-expiration-duration-in-seconds: 5 # 如果现在超过了5秒的间隔（默认是90秒）
    instance-id: cloud-stream-consumer8802  # 在信息列表时显示主机名称
    prefer-ip-address: true     # 访问的路径变为IP地址
```
#### 注解及业务
```java
    @EnableBinding(Source.class)
    public class SendMessage implements IMessageProvider{
    @Resource
    private MessageChannel output;
    @Override
    public String send() {
        String serial = UUID.randomUUID().toString();
        output.send(MessageBuilder.withPayload(serial).build());
        return serial;
    }
```
#### 测试：打开本地的rmq,可以看到配置文件中的 studyEX在rmq中出现。并且调用Provider的Controller之后，可以在rmq界面看到。
**2.Stream Consumer的使用**
#### Step1:引入依赖（和生产者的依赖是一样的）
```xml
  <!--        引入stream-rabbit-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```
#### Step2:加入配置文件
```yml
server:
  port: 8802

spring:
  application:
    name: cloud-stream-consumer
  cloud:
    stream:
      binders: # 在此处配置要绑定的rabbitmq的服务信息；
        defaultRabbit: # 表示定义的名称，用于于binding整合
          type: rabbit # 消息组件类型
          environment: # 设置rabbitmq的相关的环境配置
            spring:
              rabbitmq:
                host: 127.0.0.1 # 192.168.179.150
                port: 5672
                username: guest
                password: guest
      bindings: # 服务的整合处理
        input: # 这个名字是一个通道的名称
          destination: studyEX # 表示要使用的Exchange名称定义
          content-type: application/json # 设置消息类型，本次为json，文本则设置“text/plain”
          binder: defaultRabbit # 设置要绑定的消息服务的具体设置

eureka:
  client: # 客户端进行Eureka注册的配置
    service-url:
      defaultZone: http://localhost:7001/eureka,http://localhost:7002/eureka
  instance:
    lease-renewal-interval-in-seconds: 2 # 设置心跳的时间间隔（默认是30秒）
    lease-expiration-duration-in-seconds: 5 # 如果现在超过了5秒的间隔（默认是90秒）
    instance-id: cloud-stream-consumer8802  # 在信息列表时显示主机名称
    prefer-ip-address: true     # 访问的路径变为IP地址
```
#### Step3:相关注解及业务
```java
@EnableBinding(Sink.class)
public class ConsumerController {
    @Value("${server.port}")
    private String serverPort;
    @StreamListener(Sink.INPUT)
    public void getMessage(Message<String> message){
        System.out.println("***********消费者收到消息:"+message.getPayload());
    }
}
```
#### Step4:测试：调用生产者之后，发现消费者模块控制台输出生产者发送的消息。或者在rmq中发送消息，在消费者中也能收到消息
### Stream的持久化和分组
**分组**

以上案例存在两个消费者，streamConsumer8802和streamConsumer8803。如果没有配置group属性，name，当生产者发送消息的时候，这两个消费者都能够收到消息，这是
发的广播，但是存在这种情况，当两个消费者是同一个服务的集群，那么其实这个服务只需要有一个能收到消息就行了。在配置文件中配置group属性。那么就会遵循这个原则：
属于同一个group的只会有其中一个消费者能收到消息。不同group的都能够收到消息。

**持久化**

这其实叫消息持久化更合理。场景是这样的，当消费者不在线的时候，生产者发送了一条消息。当消费者重新上线的时候希望能拿到这条消息，这就是消息的持久化。
而当在配置文件中配置了group属性的时候，是默认支持持久化的。

注意。以上是否存在其他的持久化方案，以及生产者能否发送消息到指定队列均没有测试。
### 九、zipkin的使用
下载zipkin server jar包之后，运行该jar包，打开127.0.0.1:9411，会有一个监控平台。
#### Step1: 引入依赖
```xml
<!--链路监控包含了sleuth+zipkin-->
        <!--服务端下载地址：https://dl.bintray.com/openzipkin/maven/io/zipkin/java/zipkin-server/-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
        </dependency>
```
#### Step2: 加入配置文件
```yml
spring:
  application:
    name: provider
  zipkin:
    base-url: http://localhost:9411
  sleuth:
    sampler:
    #采样率值介于 0 到 1 之间，1 则表示全部采集
    probability: 1
```
#### Step3: 测试
打开127.0.0.1:9411，之后可以看到消费者调用生产者的链路消息
注意：生产者和消费者的依赖引入和配置都是一样的。不区分生产者和消费者。
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

### 五、使用HystrixCommand注解，发现controller层调用失败。
```java
    @GetMapping("/timeout/{id}")
    public  String time_out(@PathVariable("id")Integer id){
        return breakService.time_out(id);
    }
```
以上为controller层的代码，调用time_out函数的时候，失败。timeout代码如下：
```java
@HystrixCommand(fallbackMethod = "paymentInfo_TimeOutHandler", commandProperties = {
            //超时5s或者异常直接降级
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    })
    public String time_out(Integer id){
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return restTemplate.getForObject("http://provider/test",String.class);
    }
```
检查函数签名没有问题，但是调用失败。其中fallback函数如下：
```java
    public  String paymentInfo_TimeOutHandler(){
        return "yanshi";
    }
```
发现回调函数没有参数，所以是不是这个问题？加上参数之后，代码如下：
```java
    public  String paymentInfo_TimeOutHandler(@PathVariable("id") Integer id){
        return "yanshi";
    }
```
成功。
### 六、使用Git上传失败。
刚开始误操作把一些不相关的文件上传上去了，导致github上文件夹乱七八糟的，之后使用git pull下拉的时候发现显示是最新版本，但是使用git status的时候，又显示很多问题。
最后采用git GUI，将一些Unstaged Chagnes的文件sign off，再commmit和push。github上一下就清爽了，但是还是存在一份文件夹，怎么删除都删不掉，最后在本地进这个文件夹下，
发现有一个.git文件夹，删除掉，再重新上传就正常了。

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
