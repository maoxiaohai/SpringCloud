## lombok
在父工程的pom.xml中添加lombok插件，发现在子工程api中不能正常使用。必须在子工程中使用
解答：dependencyManagement节点只是用来版本管理，实际上并不会引入依赖，而dependencies则会真实的引入依赖。
## entities序列化
使用逆向工程实体类必须序列化么？
## 問題記錄
####1. 当启动项目的时候，发现报错，报不能创建bean的错误，检查发现是mapper.xml中的返回对象类型不对。改掉之后，发现还是报同样的错误。
之后在yml文件中注释掉
```yml
config-location: classpath:mybatis.cfg.xml
```
发现，程序正常启动。一般来说，在Springboot项目中mybatis的配置文件基本上不需要的，但是在这里添加上报错了？

解答：原因是在全局配置文件application.yml中已经使用了configuration节点配置mybatis的属性，然而在mabatis.cfg.xml中也有configuration节点，发生冲突，导致创建bean失败。

#### 2.在provider-dept-8001中配置eureca服务端地址时
```yml
eureka:
  client:
    service-url:
      defaultZone: localhost:7001/eureka
```
而在eureca服务端(euraca7001)中配置如下：
```yml
eureka:
  instance:
    hostname: localhost #eureka服务端的实例名称(这里使用了虚拟主机映射，修改hosts文件即可实现)
  client:
    register-with-eureka: false     #false表示不向注册中心注册自己。
    fetch-registry: false     #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

启动之后报错。euraca也没有收到8001的注册信息。
之后发现在7001中的defaultZone含有http起头。，更改8001中defaultZone之后一切正常。配置如下：
```yml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:7001/eureka
```

#### 3.布置eureca集群环境的时候，没有修改hosts文件中的地址映射。发现，访问http://localhost:7001/
http://localhost:7002/、http://localhost:7003/的时候，DS Replicas没有显示出来，并且，只有一个eureca 显示出有8001注册成功。其他两台没有显示。，
而关掉其中显示注册成功的eureca的时候，剩下的两台eureca中就会有一台显示注册8001成功。
解答：改变hosts文件之后发现确实是没有问题的。
#### 4.在完成zuul初步的配置后，测试调用 http://localhost:8001/dept/findAll发现成功，但是调用http://myzuul.com:9527/STUDY-SPRINGCLOUD-DEPT/dept/findAll失败。
解决：将STUDY-SPRINGCLOUD-DEPT小写后正常调用。
#### 5.zuul拦截路径
```yml
zuul:
  routes:
    mydept.serviceId: study-springcloud-dept
    #正确的路径是/mydept/**，/mydept/*是不对的
    mydept.path: /mydept/**
  prefix: /maomao
```
## 新知识点：
#### 1.负载均衡
负载均衡分为客户端负载均衡和服务端负载均衡。这两者之间最大的区别在于服务清单的存储位置。

服务端负载均衡：服务清单存储在服务端，也就是当请求方到达之后，通过负载均衡算法，得到服务地址。
客户端负载均衡：请求在客户端就通过负载均衡算法得到服务地址。当然，客户端需要维护一张服务清单。而这个清单一般需要注册中心共同完成维护。
常见的服务端负载均衡为ngnix，而ribbon是客户端负载均衡。
