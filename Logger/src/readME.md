[http://logback.qos.ch/manual/index.html][logback官网]
## 1. logback体系
## 2. 日志继承树
logback中logger是名称敏感的，通过名称去区分上下级
A logger is said to be an ancestor of another logger if its name followed by a dot is a prefix of the descendant logger name.
A logger is said to be a parent of a child logger if there are no ancestors between itself and the descendant logger.
eg：
如果一个loggerA的名称为:com.foo,另外一个loggerB的名称为:com.foo.Bar,那么，loggerB就是loggerA的子类
如果没有指定的logger，那么就会使用其父类以及其祖宗类的logger打印日志
## 3. 日志级别
  TRACE < DEBUG < INFO <  WARN < ERROR.
  比如某一个logger配置的级别为WARN，那么只会打印WARN和ERROR级别的日志
## 4. 获取logger
 如果没有配置logback.xml等配置文件，logback就会使用默认配置。
## 5. Appenders and Layouts

Each enabled logging request for a given logger will be forwarded to all the appenders in that logger as well as the appenders higher in the hierarchy. 
In other words, appenders are inherited additively from the logger hierarchy. For example,
if a console appender is added to the root logger, then all enabled logging requests will at least print on the console.
If in addition a file appender is added to a logger, say L, then enabled logging requests for L and L's children will print on a file and on the console.
It is possible to override this default behavior so that appender accumulation is no longer additive by setting the additivity flag of a logger to false.
**translate->**

每个使能的logger请求都会触发该logger绑定的所有appender以及该appender的父appender。[appender的继承关系是从logger的继承关系过来的。]
eg:如果root绑定了一个console appender,那么，所有的使能logger至少会有一个控制台打印[root是logger继承体系中根],如果logger L绑定了一个file appender，那么，L以及L的子logger
   会有一个控制台和file的输出。但是如果在一个logger中设置属性additivity="false",就会从该logger开始打断向上传输。

