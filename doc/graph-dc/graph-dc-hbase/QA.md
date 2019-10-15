- 问题
```
[2019-07-01 09:50:45]-[INFO]-[org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.processPreambleResponse(RpcClientImpl.java:817)]-RPC Server Kerberos principal name for service=MasterService is hbase/hadoop.hadoop.com@HADOOP.COM
[2019-07-01 09:50:45]-[ERROR]-[org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection$1.run(RpcClientImpl.java:681)]-SASL authentication failed. The most likely cause is missing or invalid credentials. Consider 'kinit'.
javax.security.sasl.SaslException: GSS initiate failed
        at com.sun.security.sasl.gsskerb.GssKrb5Client.evaluateChallenge(GssKrb5Client.java:211) ~[?:1.8.0_181]
        at org.apache.hadoop.hbase.security.HBaseSaslRpcClient.saslConnect(HBaseSaslRpcClient.java:169) ~[hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.setupSaslConnection(RpcClientImpl.java:614) ~[hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.access$600(RpcClientImpl.java:159) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection$2.run(RpcClientImpl.java:744) ~[hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection$2.run(RpcClientImpl.java:741) ~[hbase-client-fi-1.0.2.jar!/:?]
        at java.security.AccessController.doPrivileged(Native Method) ~[?:1.8.0_181]
        at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.setupIOstreams(RpcClientImpl.java:741) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.writeRequest(RpcClientImpl.java:938) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.ipc.RpcClientImpl$Connection.tracedWriteRequest(RpcClientImpl.java:905) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.ipc.RpcClientImpl.call(RpcClientImpl.java:1251) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.ipc.AbstractRpcClient.callBlockingMethod(AbstractRpcClient.java:224) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.ipc.AbstractRpcClient$BlockingRpcChannelImplementation.callBlockingMethod(AbstractRpcClient.java:329) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.protobuf.generated.MasterProtos$MasterService$BlockingStub.isMasterRunning(MasterProtos.java:65027) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation$MasterServiceState.isMasterRunning(ConnectionManager.java:1468) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation.isKeepAliveMasterConnectedAndRunning(ConnectionManager.java:2163) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation.getKeepAliveMasterService(ConnectionManager.java:1732) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.client.MasterCallable.prepare(MasterCallable.java:38) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.client.RpcRetryingCaller.callWithRetries(RpcRetryingCaller.java:136) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.client.HBaseAdmin.executeCallable(HBaseAdmin.java:4434) [hbase-client-fi-1.0.2.jar!/:?]
        at org.apache.hadoop.hbase.client.HBaseAdmin.getNamespaceDescriptor(HBaseAdmin.java:3187) [hbase-client-fi-1.0.2.jar!/:?]
        at com.haizhi.graph.server.hbase.admin.HBaseAdminDAOImpl.existsDatabase(HBaseAdminDAOImpl.java:65) [graph-server-hbase-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.server.hbase.admin.HBaseAdminDAOImpl$$FastClassBySpringCGLIB$$ecec3333.invoke(<generated>) [graph-server-hbase-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204) [spring-core-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:738) [spring-aop-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157) [spring-aop-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:136) [spring-tx-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179) [spring-aop-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:673) [spring-aop-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at com.haizhi.graph.server.hbase.admin.HBaseAdminDAOImpl$$EnhancerBySpringCGLIB$$cf816f13.existsDatabase(<generated>) [graph-server-hbase-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.common.service.impl.HBasePersistServiceImpl.createDbIfNotExist(HBasePersistServiceImpl.java:125) [graph-dc-common-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.common.service.impl.HBasePersistServiceImpl.bulkPersist(HBasePersistServiceImpl.java:74) [graph-dc-common-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.hbase.consumer.HBasePersistConsumer.doProcessMessages(HBasePersistConsumer.java:43) [classes!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.common.consumer.AbstractPersistConsumer.tryProcessMessages(AbstractPersistConsumer.java:114) [graph-dc-common-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.common.consumer.AbstractPersistConsumer.processMessages(AbstractPersistConsumer.java:69) [graph-dc-common-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.hbase.consumer.HBasePersistConsumer.listen(HBasePersistConsumer.java:38) [classes!/:3.0.0-SNAPSHOT]
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_181]
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[?:1.8.0_181]
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:1.8.0_181]
        at java.lang.reflect.Method.invoke(Method.java:498) ~[?:1.8.0_181]
        at org.springframework.messaging.handler.invocation.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:180) [spring-messaging-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.messaging.handler.invocation.InvocableHandlerMethod.invoke(InvocableHandlerMethod.java:112) [spring-messaging-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.kafka.listener.adapter.HandlerAdapter.invoke(HandlerAdapter.java:48) [spring-kafka-1.1.6.RELEASE.jar!/:?]
        at org.springframework.kafka.listener.adapter.MessagingMessageListenerAdapter.invokeHandler(MessagingMessageListenerAdapter.java:174) [spring-kafka-1.1.6.RELEASE.jar!/:?]
        at org.springframework.kafka.listener.adapter.BatchMessagingMessageListenerAdapter.onMessage(BatchMessagingMessageListenerAdapter.java:118) [spring-kafka-1.1.6.RELEASE.jar!/:?]
        at org.springframework.kafka.listener.adapter.BatchMessagingMessageListenerAdapter.onMessage(BatchMessagingMessageListenerAdapter.java:56) [spring-kafka-1.1.6.RELEASE.jar!/:?]
        at org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.invokeBatchListener(KafkaMessageListenerContainer.java:751) [spring-kafka-1.1.6.RELEASE.jar!/:?]
        at org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.invokeListener(KafkaMessageListenerContainer.java:735) [spring-kafka-1.1.6.RELEASE.jar!/:?]
        at org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.access$2200(KafkaMessageListenerContainer.java:245) [spring-kafka-1.1.6.RELEASE.jar!/:?]
        at org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer$ListenerInvoker.run(KafkaMessageListenerContainer.java:1031) [spring-kafka-1.1.6.RELEASE.jar!/:?]
        at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) [?:1.8.0_181]
        at java.util.concurrent.FutureTask.run(FutureTask.java:266) [?:1.8.0_181]
        at java.lang.Thread.run(Thread.java:748) [?:1.8.0_181]
Caused by: org.ietf.jgss.GSSException: No valid credentials provided (Mechanism level: Failed to find any Kerberos tgt)
        at sun.security.jgss.krb5.Krb5InitCredential.getInstance(Krb5InitCredential.java:147) ~[?:1.8.0_181]
        at sun.security.jgss.krb5.Krb5MechFactory.getCredentialElement(Krb5MechFactory.java:122) ~[?:1.8.0_181]
        at sun.security.jgss.krb5.Krb5MechFactory.getMechanismContext(Krb5MechFactory.java:187) ~[?:1.8.0_181]
        at sun.security.jgss.GSSManagerImpl.getMechanismContext(GSSManagerImpl.java:224) ~[?:1.8.0_181]
        at sun.security.jgss.GSSContextImpl.initSecContext(GSSContextImpl.java:212) ~[?:1.8.0_181]
        at sun.security.jgss.GSSContextImpl.initSecContext(GSSContextImpl.java:179) ~[?:1.8.0_181]
        at com.sun.security.sasl.gsskerb.GssKrb5Client.evaluateChallenge(GssKrb5Client.java:192) ~[?:1.8.0_181]
        ... 54 more
```

- 分析
```
hbase连接超时，重启后问题暂时没有复现，由于没有复现，初步确定是fi重启或者网络延迟导致

```


