# 问题
对hbase fi环境进行测试连接时发现hbase连接失败，之前没有出现过，最近才出现。


# 报错
```
[2019-07-26 09:58:25]-[INFO]-[org.apache.zookeeper.ClientCnxn$SendThread.getServerPrincipalName(ClientCnxn.java:1184)]-Got server principal from the server and it is zookeeper/hadoop.hadoop.com
[2019-07-26 09:58:25]-[INFO]-[org.apache.zookeeper.ClientCnxn$SendThread.getServerPrincipalName(ClientCnxn.java:1222)]-Using server principal zookeeper/hadoop.hadoop.com
Debug is  true storeKey true useTicketCache false useKeyTab true doNotPrompt false ticketCache is null isInitiator true KeyTab is /D:/CodeGraph/graph/graph-dc/graph-dc-store/target/classes/user.keytab refreshKrb5Config is false principal is dmp tryFirstPass is false useFirstPass is false storePass is false clearPass is false
principal is dmp@HADOOP.COM
Will use keytab
Commit Succeeded

[2019-07-26 09:58:25]-[INFO]-[org.apache.zookeeper.Login.login(Login.java:310)]-successfully logged in.
[2019-07-26 09:58:25]-[INFO]-[org.apache.zookeeper.Login$1.run(Login.java:139)]-TGT refresh thread started.
[2019-07-26 09:58:25]-[INFO]-[org.apache.zookeeper.Login.getRefreshTime(Login.java:328)]-TGT valid starting at:        Fri Jul 26 09:57:56 CST 2019
[2019-07-26 09:58:25]-[INFO]-[org.apache.zookeeper.Login.getRefreshTime(Login.java:329)]-TGT expires:                  Sat Jul 27 09:57:56 CST 2019
[2019-07-26 09:58:25]-[INFO]-[org.apache.zookeeper.Login$1.run(Login.java:193)]-TGT refresh sleeping until: Sat Jul 27 05:31:06 CST 2019
[2019-07-26 09:58:25]-[INFO]-[org.apache.zookeeper.client.ZooKeeperSaslClient$1.run(ZooKeeperSaslClient.java:327)]-Client will use GSSAPI as SASL mechanism.
[2019-07-26 09:58:25]-[INFO]-[org.apache.zookeeper.ClientCnxn$SendThread.logStartConnect(ClientCnxn.java:1325)]-Opening socket connection to server fies03/192.168.1.225:24002. Will attempt to SASL-authenticate using Login Context section 'Client'
[2019-07-26 09:58:25]-[INFO]-[org.apache.zookeeper.ClientCnxn$SendThread.primeConnection(ClientCnxn.java:1053)]-Socket connection established, initiating session, client: /10.10.10.209:58809, server: fies03/192.168.1.225:24002
[2019-07-26 09:58:25]-[INFO]-[org.apache.zookeeper.ClientCnxn$SendThread.onConnected(ClientCnxn.java:1599)]-Session establishment complete on server fies03/192.168.1.225:24002, sessionid = 0x140052959e487752, negotiated timeout = 90000
[2019-07-26 09:58:26]-[INFO]-[com.haizhi.graph.server.hbase.client.HBaseClient.create(HBaseClient.java:100)]-Success to create connection with HBase server url[fies02,fies03,fies01:24002]
[2019-07-26 09:58:27]-[INFO]-[org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.processPreambleResponse(RpcClientImpl.java:817)]-RPC Server Kerberos principal name for service=MasterService is hbase/hadoop.hadoop.com@HADOOP.COM
[2019-07-26 09:58:27]-[WARN]-[org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection$1.run(RpcClientImpl.java:671)]-Exception encountered while connecting to the server : javax.security.sasl.SaslException: GSS initiate failed [Caused by GSSException: No valid credentials provided (Mechanism level: Failed to find any Kerberos tgt)]
[2019-07-26 09:58:27]-[ERROR]-[org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection$1.run(RpcClientImpl.java:681)]-SASL authentication failed. The most likely cause is missing or invalid credentials. Consider 'kinit'.
javax.security.sasl.SaslException: GSS initiate failed
	at com.sun.security.sasl.gsskerb.GssKrb5Client.evaluateChallenge(GssKrb5Client.java:211) ~[?:1.8.0_91]
	at org.apache.hadoop.hbase.security.HBaseSaslRpcClient.saslConnect(HBaseSaslRpcClient.java:169) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.setupSaslConnection(RpcClientImpl.java:614) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.access$600(RpcClientImpl.java:159) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection$2.run(RpcClientImpl.java:744) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection$2.run(RpcClientImpl.java:741) ~[hbase-client-fi-1.0.2.jar:?]
	at java.security.AccessController.doPrivileged(Native Method) ~[?:1.8.0_91]
	at javax.security.auth.Subject.doAs(Subject.java:422) ~[?:1.8.0_91]
	at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1769) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.setupIOstreams(RpcClientImpl.java:741) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.writeRequest(RpcClientImpl.java:938) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.tracedWriteRequest(RpcClientImpl.java:905) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl.call(RpcClientImpl.java:1251) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.AbstractRpcClient.callBlockingMethod(AbstractRpcClient.java:224) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.AbstractRpcClient$BlockingRpcChannelImplementation.callBlockingMethod(AbstractRpcClient.java:329) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.protobuf.generated.MasterProtos$MasterService$BlockingStub.isMasterRunning(MasterProtos.java:65027) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation$MasterServiceStubMaker.isMasterRunning(ConnectionManager.java:1595) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation$StubMaker.makeStubNoRetries(ConnectionManager.java:1533) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation$StubMaker.makeStub(ConnectionManager.java:1555) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation$MasterServiceStubMaker.makeStub(ConnectionManager.java:1584) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation.getKeepAliveMasterService(ConnectionManager.java:1735) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.MasterCallable.prepare(MasterCallable.java:38) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.RpcRetryingCaller.callWithRetries(RpcRetryingCaller.java:136) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.HBaseAdmin.executeCallable(HBaseAdmin.java:4434) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.HBaseAdmin.listNamespaceDescriptors(HBaseAdmin.java:3207) ~[hbase-client-fi-1.0.2.jar:?]
	at com.haizhi.graph.server.hbase.client.HBaseClient.testConnect(HBaseClient.java:57) ~[classes/:?]
	at com.haizhi.graph.server.hbase.client.HBaseClient$$FastClassBySpringCGLIB$$370c54b.invoke(<generated>) ~[classes/:?]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204) ~[spring-core-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:738) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:136) ~[spring-tx-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:673) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at com.haizhi.graph.server.hbase.client.HBaseClient$$EnhancerBySpringCGLIB$$d1e8cbd6.testConnect(<generated>) ~[classes/:?]
	at com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.testHBase(ConnectTestServiceImpl.java:226) ~[classes/:?]
	at com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.lambda$testHadoop$2(ConnectTestServiceImpl.java:190) ~[classes/:?]
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) [?:1.8.0_91]
	at java.util.concurrent.FutureTask.run(FutureTask.java:266) [?:1.8.0_91]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142) [?:1.8.0_91]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617) [?:1.8.0_91]
	at java.lang.Thread.run(Thread.java:745) [?:1.8.0_91]
Caused by: org.ietf.jgss.GSSException: No valid credentials provided (Mechanism level: Failed to find any Kerberos tgt)
	at sun.security.jgss.krb5.Krb5InitCredential.getInstance(Krb5InitCredential.java:147) ~[?:1.8.0_91]
	at sun.security.jgss.krb5.Krb5MechFactory.getCredentialElement(Krb5MechFactory.java:122) ~[?:1.8.0_91]
	at sun.security.jgss.krb5.Krb5MechFactory.getMechanismContext(Krb5MechFactory.java:187) ~[?:1.8.0_91]
	at sun.security.jgss.GSSManagerImpl.getMechanismContext(GSSManagerImpl.java:224) ~[?:1.8.0_91]
	at sun.security.jgss.GSSContextImpl.initSecContext(GSSContextImpl.java:212) ~[?:1.8.0_91]
	at sun.security.jgss.GSSContextImpl.initSecContext(GSSContextImpl.java:179) ~[?:1.8.0_91]
	at com.sun.security.sasl.gsskerb.GssKrb5Client.evaluateChallenge(GssKrb5Client.java:192) ~[?:1.8.0_91]
	... 40 more
[2019-07-26 09:58:27]-[INFO]-[org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation.closeZooKeeperWatcher(ConnectionManager.java:1707)]-Closing zookeeper sessionid=0x140052959e487752
[2019-07-26 09:58:27]-[INFO]-[org.apache.zookeeper.ClientCnxn$EventThread.run(ClientCnxn.java:614)]-EventThread shut down for session: 0x140052959e487752
[2019-07-26 09:58:27]-[INFO]-[org.apache.zookeeper.ZooKeeper.close(ZooKeeper.java:1320)]-Session: 0x140052959e487752 closed
[2019-07-26 09:58:27]-[ERROR]-[com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.submitAndWaitTimeout(ConnectTestServiceImpl.java:235)]-null
java.util.concurrent.ExecutionException: java.lang.RuntimeException: org.apache.hadoop.hbase.DoNotRetryIOException: GSSException: No valid credentials provided (Mechanism level: Failed to find any Kerberos tgt)
	at java.util.concurrent.FutureTask.report(FutureTask.java:122) ~[?:1.8.0_91]
	at java.util.concurrent.FutureTask.get(FutureTask.java:206) ~[?:1.8.0_91]
	at com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.submitAndWaitTimeout(ConnectTestServiceImpl.java:232) [classes/:?]
	at com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.testHadoop(ConnectTestServiceImpl.java:205) [classes/:?]
	at com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.process(ConnectTestServiceImpl.java:81) [classes/:?]
	at com.haizhi.graph.dc.store.service.impl.StoreManageServiceImpl.testConnect(StoreManageServiceImpl.java:108) [classes/:?]
	at com.haizhi.graph.dc.store.controller.DcStoreController.testConnect(DcStoreController.java:83) [classes/:?]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_91]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[?:1.8.0_91]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:1.8.0_91]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[?:1.8.0_91]
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:205) [spring-web-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:133) [spring-web-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:97) [spring-webmvc-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:827) [spring-webmvc-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:738) [spring-webmvc-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:85) [spring-webmvc-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:967) [spring-webmvc-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:901) [spring-webmvc-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:970) [spring-webmvc-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:872) [spring-webmvc-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:661) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:846) [spring-webmvc-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:742) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:52) [tomcat-embed-websocket-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.shiro.web.servlet.AdviceFilter.executeChain(AdviceFilter.java:108) [shiro-web-1.3.2.jar:1.3.2]
	at org.apache.shiro.web.servlet.AdviceFilter.doFilterInternal(AdviceFilter.java:137) [shiro-web-1.3.2.jar:1.3.2]
	at org.apache.shiro.web.servlet.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:125) [shiro-web-1.3.2.jar:1.3.2]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.shiro.web.servlet.ProxiedFilterChain.doFilter(ProxiedFilterChain.java:61) [shiro-web-1.3.2.jar:1.3.2]
	at org.apache.shiro.web.servlet.AdviceFilter.executeChain(AdviceFilter.java:108) [shiro-web-1.3.2.jar:1.3.2]
	at org.apache.shiro.web.servlet.AdviceFilter.doFilterInternal(AdviceFilter.java:137) [shiro-web-1.3.2.jar:1.3.2]
	at org.apache.shiro.web.servlet.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:125) [shiro-web-1.3.2.jar:1.3.2]
	at org.apache.shiro.web.servlet.ProxiedFilterChain.doFilter(ProxiedFilterChain.java:66) [shiro-web-1.3.2.jar:1.3.2]
	at org.apache.shiro.web.servlet.AbstractShiroFilter.executeChain(AbstractShiroFilter.java:449) [shiro-web-1.3.2.jar:1.3.2]
	at org.apache.shiro.web.servlet.AbstractShiroFilter$1.call(AbstractShiroFilter.java:365) [shiro-web-1.3.2.jar:1.3.2]
	at org.apache.shiro.subject.support.SubjectCallable.doCall(SubjectCallable.java:90) [shiro-core-1.3.2.jar:1.3.2]
	at org.apache.shiro.subject.support.SubjectCallable.call(SubjectCallable.java:83) [shiro-core-1.3.2.jar:1.3.2]
	at org.apache.shiro.subject.support.DelegatingSubject.execute(DelegatingSubject.java:383) [shiro-core-1.3.2.jar:1.3.2]
	at org.apache.shiro.web.servlet.AbstractShiroFilter.doFilterInternal(AbstractShiroFilter.java:362) [shiro-web-1.3.2.jar:1.3.2]
	at org.apache.shiro.web.servlet.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:125) [shiro-web-1.3.2.jar:1.3.2]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:99) [spring-web-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107) [spring-web-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.springframework.web.filter.HttpPutFormContentFilter.doFilterInternal(HttpPutFormContentFilter.java:105) [spring-web-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107) [spring-web-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.springframework.web.filter.HiddenHttpMethodFilter.doFilterInternal(HiddenHttpMethodFilter.java:81) [spring-web-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107) [spring-web-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:197) [spring-web-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107) [spring-web-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:198) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:478) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:140) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:80) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:87) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:342) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:799) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:861) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1455) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142) [?:1.8.0_91]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617) [?:1.8.0_91]
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) [tomcat-embed-core-8.5.15.jar:8.5.15]
	at java.lang.Thread.run(Thread.java:745) [?:1.8.0_91]
Caused by: java.lang.RuntimeException: org.apache.hadoop.hbase.DoNotRetryIOException: GSSException: No valid credentials provided (Mechanism level: Failed to find any Kerberos tgt)
	at com.haizhi.graph.server.hbase.client.HBaseClient.testConnect(HBaseClient.java:59) ~[classes/:?]
	at com.haizhi.graph.server.hbase.client.HBaseClient$$FastClassBySpringCGLIB$$370c54b.invoke(<generated>) ~[classes/:?]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204) ~[spring-core-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:738) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:136) ~[spring-tx-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:673) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at com.haizhi.graph.server.hbase.client.HBaseClient$$EnhancerBySpringCGLIB$$d1e8cbd6.testConnect(<generated>) ~[classes/:?]
	at com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.testHBase(ConnectTestServiceImpl.java:226) ~[classes/:?]
	at com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.lambda$testHadoop$2(ConnectTestServiceImpl.java:190) ~[classes/:?]
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) ~[?:1.8.0_91]
	at java.util.concurrent.FutureTask.run(FutureTask.java:266) ~[?:1.8.0_91]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142) ~[?:1.8.0_91]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617) ~[?:1.8.0_91]
	... 1 more
Caused by: org.apache.hadoop.hbase.DoNotRetryIOException: GSSException: No valid credentials provided (Mechanism level: Failed to find any Kerberos tgt)
	at org.apache.hadoop.hbase.client.RpcRetryingCaller.callWithRetries(RpcRetryingCaller.java:144) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.HBaseAdmin.executeCallable(HBaseAdmin.java:4434) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.HBaseAdmin.listNamespaceDescriptors(HBaseAdmin.java:3207) ~[hbase-client-fi-1.0.2.jar:?]
	at com.haizhi.graph.server.hbase.client.HBaseClient.testConnect(HBaseClient.java:57) ~[classes/:?]
	at com.haizhi.graph.server.hbase.client.HBaseClient$$FastClassBySpringCGLIB$$370c54b.invoke(<generated>) ~[classes/:?]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204) ~[spring-core-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:738) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:136) ~[spring-tx-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:673) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at com.haizhi.graph.server.hbase.client.HBaseClient$$EnhancerBySpringCGLIB$$d1e8cbd6.testConnect(<generated>) ~[classes/:?]
	at com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.testHBase(ConnectTestServiceImpl.java:226) ~[classes/:?]
	at com.haizhi.graph.dc.store.service.impl.ConnectTestServiceImpl.lambda$testHadoop$2(ConnectTestServiceImpl.java:190) ~[classes/:?]
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) ~[?:1.8.0_91]
	at java.util.concurrent.FutureTask.run(FutureTask.java:266) ~[?:1.8.0_91]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142) ~[?:1.8.0_91]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617) ~[?:1.8.0_91]
	... 1 more
Caused by: org.ietf.jgss.GSSException: No valid credentials provided (Mechanism level: Failed to find any Kerberos tgt)
	at sun.security.jgss.krb5.Krb5InitCredential.getInstance(Krb5InitCredential.java:147) ~[?:1.8.0_91]
	at sun.security.jgss.krb5.Krb5MechFactory.getCredentialElement(Krb5MechFactory.java:122) ~[?:1.8.0_91]
	at sun.security.jgss.krb5.Krb5MechFactory.getMechanismContext(Krb5MechFactory.java:187) ~[?:1.8.0_91]
	at sun.security.jgss.GSSManagerImpl.getMechanismContext(GSSManagerImpl.java:224) ~[?:1.8.0_91]
	at sun.security.jgss.GSSContextImpl.initSecContext(GSSContextImpl.java:212) ~[?:1.8.0_91]
	at sun.security.jgss.GSSContextImpl.initSecContext(GSSContextImpl.java:179) ~[?:1.8.0_91]
	at com.sun.security.sasl.gsskerb.GssKrb5Client.evaluateChallenge(GssKrb5Client.java:192) ~[?:1.8.0_91]
	at org.apache.hadoop.hbase.security.HBaseSaslRpcClient.saslConnect(HBaseSaslRpcClient.java:169) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.setupSaslConnection(RpcClientImpl.java:614) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.access$600(RpcClientImpl.java:159) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection$2.run(RpcClientImpl.java:744) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection$2.run(RpcClientImpl.java:741) ~[hbase-client-fi-1.0.2.jar:?]
	at java.security.AccessController.doPrivileged(Native Method) ~[?:1.8.0_91]
	at javax.security.auth.Subject.doAs(Subject.java:422) ~[?:1.8.0_91]
	at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1769) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.setupIOstreams(RpcClientImpl.java:741) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.writeRequest(RpcClientImpl.java:938) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.tracedWriteRequest(RpcClientImpl.java:905) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.RpcClientImpl.call(RpcClientImpl.java:1251) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.AbstractRpcClient.callBlockingMethod(AbstractRpcClient.java:224) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.ipc.AbstractRpcClient$BlockingRpcChannelImplementation.callBlockingMethod(AbstractRpcClient.java:329) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.protobuf.generated.MasterProtos$MasterService$BlockingStub.isMasterRunning(MasterProtos.java:65027) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation$MasterServiceStubMaker.isMasterRunning(ConnectionManager.java:1595) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation$StubMaker.makeStubNoRetries(ConnectionManager.java:1533) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation$StubMaker.makeStub(ConnectionManager.java:1555) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation$MasterServiceStubMaker.makeStub(ConnectionManager.java:1584) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation.getKeepAliveMasterService(ConnectionManager.java:1735) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.MasterCallable.prepare(MasterCallable.java:38) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.RpcRetryingCaller.callWithRetries(RpcRetryingCaller.java:136) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.HBaseAdmin.executeCallable(HBaseAdmin.java:4434) ~[hbase-client-fi-1.0.2.jar:?]
	at org.apache.hadoop.hbase.client.HBaseAdmin.listNamespaceDescriptors(HBaseAdmin.java:3207) ~[hbase-client-fi-1.0.2.jar:?]
	at com.haizhi.graph.server.hbase.client.HBaseClient.testConnect(HBaseClient.java:57) ~[classes/:?]
	at com.haizhi.graph.server.hbase.client.HBaseClient$$FastClassBySpringCGLIB$$370c54b.invoke(<generated>) ~[classes/:?]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204) ~[spring-core-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:738) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157) ~[spring-aop-4.3.9.RELEASE.jar:4.3.9.RELEASE]
	at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:136) ~[spring-tx-4.3.9.RELEASE.jar:4.3.9.RELEASE]
```


# 分析
没有下载成功xml配置文件
原因是HbaseConf类代码逻辑bug

# 解决

if (envVersion.startsWith(EnvVersion.FI.name())) {
这句有bug，EnvVersion.FI.name()的值是FI
但是envVersion是Fic80，判断失效
修改为：
if (envVersion.toUpperCase().startsWith(EnvVersion.FI.name())) {
