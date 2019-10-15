## Fi es不能创建索引问题

- 问题分析：
华为fi环境的es，在做数据修改的操作时需要用到zookeeper，查询不需要用到zookeeper

- 解决建议：
重启zookeeper单个节点、不行
重启zookeeper集群并重启相关依赖的集群环境，如hive ，hbase, elasticsearch、不行

最终解决方案：
停止elasticsearch集群，等5秒后，启动elasticsearch集群，fi控制面板出现1分钟左右卡顿后，elasticsearch正常。



- 问题描述
提示zookeeper service unavailable
		Suppressed: org.elasticsearch.client.ResponseException: method [DELETE], host [https://192.168.1.224:24148], URI [/es6_test16?&pretty=true], status line [HTTP/1.1 503 Service Unavailable]
Zookeeper service is unavailable
			at org.elasticsearch.client.RestClient$1.completed(RestClient.java:431) ~[elasticsearch-rest-client-6.1.3.jar:6.1.3]
			at org.elasticsearch.client.RestClient$1.completed(RestClient.java:395) ~[elasticsearch-rest-client-6.1.3.jar:6.1.3]
			at org.apache.http.concurrent.BasicFuture.completed(BasicFuture.java:119) ~[httpcore-4.4.6.jar:4.4.6]
			at org.apache.http.impl.nio.client.DefaultClientExchangeHandlerImpl.responseCompleted(DefaultClientExchangeHandlerImpl.java:177) ~[httpasyncclient-4.1.2.jar:4.1.2]
			at org.apache.http.nio.protocol.HttpAsyncRequestExecutor.processResponse(HttpAsyncRequestExecutor.java:436) ~[httpcore-nio-4.4.5.jar:4.4.5]
			at org.apache.http.nio.protocol.HttpAsyncRequestExecutor.inputReady(HttpAsyncRequestExecutor.java:326) ~[httpcore-nio-4.4.5.jar:4.4.5]
			at org.apache.http.impl.nio.DefaultNHttpClientConnection.consumeInput(DefaultNHttpClientConnection.java:265) ~[httpcore-nio-4.4.5.jar:4.4.5]
			at org.apache.http.impl.nio.client.InternalIODispatch.onInputReady(InternalIODispatch.java:81) ~[httpasyncclient-4.1.2.jar:4.1.2]
			at org.apache.http.impl.nio.client.InternalIODispatch.onInputReady(InternalIODispatch.java:39) ~[httpasyncclient-4.1.2.jar:4.1.2]
			at org.apache.http.impl.nio.reactor.AbstractIODispatch.inputReady(AbstractIODispatch.java:121) ~[httpcore-nio-4.4.5.jar:4.4.5]
			at org.apache.http.impl.nio.reactor.BaseIOReactor.readable(BaseIOReactor.java:162) ~[httpcore-nio-4.4.5.jar:4.4.5]
			at org.apache.http.impl.nio.reactor.AbstractIOReactor.processEvent(AbstractIOReactor.java:337) ~[httpcore-nio-4.4.5.jar:4.4.5]
			at org.apache.http.impl.nio.reactor.AbstractIOReactor.processEvents(AbstractIOReactor.java:315) ~[httpcore-nio-4.4.5.jar:4.4.5]
			at org.apache.http.impl.nio.reactor.AbstractIOReactor.execute(AbstractIOReactor.java:276) ~[httpcore-nio-4.4.5.jar:4.4.5]
			at org.apache.http.impl.nio.reactor.BaseIOReactor.execute(BaseIOReactor.java:104) ~[httpcore-nio-4.4.5.jar:4.4.5]
			at org.apache.http.impl.nio.reactor.AbstractMultiworkerIOReactor$Worker.run(AbstractMultiworkerIOReactor.java:588) ~[httpcore-nio-4.4.5.jar:4.4.5]
			at java.lang.Thread.run(Thread.java:745) ~[?:1.8.0_91]



