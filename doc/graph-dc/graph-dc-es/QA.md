- 问题
```
[2019-06-27 09:33:50]-[INFO]-[com.haizhi.graph.dc.common.consumer.AbstractPersistConsumer.afterProcessMessages(AbstractPersistConsumer.java:96)]-metric store after bulk insert data, taskInstanceId=12, storeType:ES
[2019-06-27 09:33:51]-[INFO]-[com.haizhi.graph.dc.es.service.impl.ErrorInfoServiceImpl.doRecord(ErrorInfoServiceImpl.java:84)]-record error data , taskInstanceId=12 , data size=3 ,graph=demo_graph
[2019-06-27 09:33:51]-[ERROR]-[com.haizhi.graph.dc.es.service.impl.ErrorInfoServiceImpl.doRecord(ErrorInfoServiceImpl.java:100)]-null
java.lang.RuntimeException: error while performing request
        at org.elasticsearch.client.RestClient$SyncResponseListener.get(RestClient.java:825) ~[elasticsearch-rest-client-fi-6.1.3.jar!/:6.1.3]
        at org.elasticsearch.client.RestClient.performRequest(RestClient.java:259) ~[elasticsearch-rest-client-fi-6.1.3.jar!/:6.1.3]
        at org.elasticsearch.client.RestClient.performRequest(RestClient.java:231) ~[elasticsearch-rest-client-fi-6.1.3.jar!/:6.1.3]
        at org.elasticsearch.client.RestClient.performRequest(RestClient.java:193) ~[elasticsearch-rest-client-fi-6.1.3.jar!/:6.1.3]
        at com.haizhi.graph.server.es6.index.EsIndexDaoImpl.existsIndex(EsIndexDaoImpl.java:60) ~[graph-server-es6-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.server.es6.index.EsIndexDaoImpl$$FastClassBySpringCGLIB$$7bfa6ac5.invoke(<generated>) ~[graph-server-es6-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204) ~[spring-core-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:738) ~[spring-aop-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157) ~[spring-aop-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:136) ~[spring-tx-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179) ~[spring-aop-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:673) ~[spring-aop-4.3.9.RELEASE.jar!/:4.3.9.RELEASE]
        at com.haizhi.graph.server.es6.index.EsIndexDaoImpl$$EnhancerBySpringCGLIB$$d917c34c.existsIndex(<generated>) ~[graph-server-es6-3.0.0-SNAPSHOT.jar!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.es.service.impl.ErrorInfoServiceImpl.createIndexIfNotExist(ErrorInfoServiceImpl.java:194) ~[classes!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.es.service.impl.ErrorInfoServiceImpl.doRecord(ErrorInfoServiceImpl.java:91) ~[classes!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.es.service.impl.ErrorInfoServiceImpl.recordInfo(ErrorInfoServiceImpl.java:79) ~[classes!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.es.service.impl.ErrorInfoServiceImpl.lambda$recordInfo$0(ErrorInfoServiceImpl.java:73) ~[classes!/:3.0.0-SNAPSHOT]
        at java.util.LinkedList$LLSpliterator.forEachRemaining(LinkedList.java:1235) [?:1.8.0_181]
        at java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:580) [?:1.8.0_181]
        at com.haizhi.graph.dc.es.service.impl.ErrorInfoServiceImpl.recordInfo(ErrorInfoServiceImpl.java:73) [classes!/:3.0.0-SNAPSHOT]
        at com.haizhi.graph.dc.es.consumer.ErrorInfoConsumer.listen(ErrorInfoConsumer.java:64) [classes!/:3.0.0-SNAPSHOT]
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
Caused by: org.apache.http.ProtocolException: Not a valid protocol version: AuthenticationToken expired
        at org.apache.http.impl.nio.codecs.AbstractMessageParser.parse(AbstractMessageParser.java:209) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.DefaultNHttpClientConnection.consumeInput(DefaultNHttpClientConnection.java:245) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.client.InternalIODispatch.onInputReady(InternalIODispatch.java:81) ~[httpasyncclient-4.1.3.jar!/:4.1.3]
        at org.apache.http.impl.nio.client.InternalIODispatch.onInputReady(InternalIODispatch.java:39) ~[httpasyncclient-4.1.3.jar!/:4.1.3]
        at org.apache.http.impl.nio.reactor.AbstractIODispatch.inputReady(AbstractIODispatch.java:121) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.reactor.BaseIOReactor.readable(BaseIOReactor.java:162) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.reactor.AbstractIOReactor.processEvent(AbstractIOReactor.java:337) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.reactor.AbstractIOReactor.processEvents(AbstractIOReactor.java:315) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.reactor.AbstractIOReactor.execute(AbstractIOReactor.java:276) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.reactor.BaseIOReactor.execute(BaseIOReactor.java:104) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.reactor.AbstractMultiworkerIOReactor$Worker.run(AbstractMultiworkerIOReactor.java:588) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        ... 1 more
Caused by: org.apache.http.ParseException: Not a valid protocol version: AuthenticationToken expired
        at org.apache.http.message.BasicLineParser.parseProtocolVersion(BasicLineParser.java:148) ~[httpcore-4.4.6.jar!/:4.4.6]
        at org.apache.http.message.BasicLineParser.parseStatusLine(BasicLineParser.java:366) ~[httpcore-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.codecs.DefaultHttpResponseParser.createMessage(DefaultHttpResponseParser.java:112) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.codecs.DefaultHttpResponseParser.createMessage(DefaultHttpResponseParser.java:50) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.codecs.AbstractMessageParser.parseHeadLine(AbstractMessageParser.java:156) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.codecs.AbstractMessageParser.parse(AbstractMessageParser.java:207) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.DefaultNHttpClientConnection.consumeInput(DefaultNHttpClientConnection.java:245) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.client.InternalIODispatch.onInputReady(InternalIODispatch.java:81) ~[httpasyncclient-4.1.3.jar!/:4.1.3]
        at org.apache.http.impl.nio.client.InternalIODispatch.onInputReady(InternalIODispatch.java:39) ~[httpasyncclient-4.1.3.jar!/:4.1.3]
        at org.apache.http.impl.nio.reactor.AbstractIODispatch.inputReady(AbstractIODispatch.java:121) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.reactor.BaseIOReactor.readable(BaseIOReactor.java:162) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.reactor.AbstractIOReactor.processEvent(AbstractIOReactor.java:337) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.reactor.AbstractIOReactor.processEvents(AbstractIOReactor.java:315) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.reactor.AbstractIOReactor.execute(AbstractIOReactor.java:276) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.reactor.BaseIOReactor.execute(BaseIOReactor.java:104) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        at org.apache.http.impl.nio.reactor.AbstractMultiworkerIOReactor$Worker.run(AbstractMultiworkerIOReactor.java:588) ~[httpcore-nio-4.4.6.jar!/:4.4.6]
        ... 1 more
[2019-06-27 09:42:02]-[INFO]-[com.haizhi.graph.dc.core.redis.DcSubListener.receiveMessage(DcSubListener.java:35)]-Success to receive [channel="gap_fi_test", message="gap_fi_test"]
[2019-06-27 09:42:02]-[INFO]-[com.haizhi.graph.dc.core.service.impl.DcMetadataCacheImpl.refresh(DcMetadataCacheImpl.java:54)]-Success to refresh metaDataCache by key=gap_fi_test
```

- 分析
```
原因，es的fi连接有token机制，通过http的cookie控制，这个时间是在建立es的客户端连接后开始计时，
以后每次http请求都要检验超时时间
fi token示例：
set-cookie: solr.auth="u=dmp&p=dmp@HADOOP.COM&t=kerberos&e=1561619897591&s=PO9EjWXO0FD4hKRcAejAxHswUhQ="; Expires=Thu, 27-Jun-2019 07:18:17 GMT; HttpOnly
```

- 解决
```
原因：es连接时，client没有超时限制，但是华为fi环境服务端会分配一个token口令，这个口令默认有效期是10个小时，超过10个小时client就会失效，这样的话，必须加入client失效机制，
否则就会出现失效时间到了后，无法发起连接诶，再过10分中就抛出token expired异常

最终通过重新实现连接池解决(模块graph-server-es6)：
EsRestClient.java
    private LoadingCache<StoreURL, RestClient> POOL;
    private LoadingCache<StoreURL, RestHighLevelClient> HIGH_POOL;

```