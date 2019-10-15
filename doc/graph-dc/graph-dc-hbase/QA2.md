场景：
数据平台界面测试hbase连接报错失败

报错：
Caused by: java.util.concurrent.ExecutionException: java.lang.RuntimeException: java.net.SocketTimeoutException: callTimeout=10000, callDuration=18275: com.google.protobuf.ServiceException: org.apache.hadoop.hbase.exceptions.ConnectionClosingException: Call id=7 on server hadoop01.sz.haizhi.com/192.168.1.16:60000 aborted: connection is closing
        at java.util.concurrent.FutureTask.report(FutureTask.java:122) ~[?:1.8.0_161]
        at java.util.concurrent.FutureTask.get(FutureTask.java:206) ~[?:1.8.0_161]
        at com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.submitAndWaitTimeout(ConnectTestServiceImpl.java:228) ~[classes!/:3.0.0-SNAPSHOT]
        ... 77 more
Caused by: java.lang.RuntimeException: java.net.SocketTimeoutException: callTimeout=10000, callDuration=18275: com.google.protobuf.ServiceException: org.apache.hadoop.hbase.exceptions.ConnectionClosingException: Call id=7 on server hadoop01.sz.haizhi.com/192.168.1.16:60000 aborted: connection is closing
        at com.haizhi.graph.server.hbase.client.HBaseClient.testConnect(HBaseClient.java:59) ~[graph-server-hbase-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.server.hbase.client.HBaseClient$$FastClassBySpringCGLIB$$370c54b.invoke(<generated>) ~[graph-server-hbase-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204) ~[spring-core-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:738) ~[spring-aop-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157) ~[spring-aop-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:136) ~[spring-tx-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179) ~[spring-aop-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:673) ~[spring-aop-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at com.haizhi.graph.server.hbase.client.HBaseClient$$EnhancerBySpringCGLIB$$92ba9d54.testConnect(<generated>) ~[graph-server-hbase-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.testHBase(ConnectTestServiceImpl.java:222) ~[classes!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.lambda$testHadoop$2(ConnectTestServiceImpl.java:186) ~[classes!/:3.0.0-SNAPSHOT]
        at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) ~[?:1.8.0_161]
        at java.util.concurrent.FutureTask.run(FutureTask.java:266) ~[?:1.8.0_161]
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) ~[?:1.8.0_161]
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) ~[?:1.8.0_161]
        ... 1 more

解决：
HBaseConf的bug，设置环境变量HADOOP_USER_NAME时，要么是有数据字符串，要么不设置，不能是""或null