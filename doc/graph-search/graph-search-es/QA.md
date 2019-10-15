## graph-search-es模块启动报错,连接es

- 问题分析：
初步判断是netty版本兼容问题
因为之前打包时,pom文件中允许打包高版本的netty-all导致当时就无法连接es
解决方案:
hbase-client对应依赖中包含高版本的netty导致,由于这个依赖完全多余,没有exclude,直接去掉了

删掉多余pom依赖后,依然有这个问题,因为jekins打包时没有清理build目录,导致不兼容的jar依然没有删除干净,解决方式:
rm -rf /root/.jenkins/workspace/graph-2.1.0-dev/build/release/graph/graph-search-es-3.0.0-SNAPSHOT/lib/netty-all-4.0.23.Final.jar


- 问题描述
Exception in thread "elasticsearch[_client_][generic][T#2]" java.lang.NoSuchMethodError: io.netty.buffer.CompositeByteBuf.addComponents(ZLjava/lang/Iterable;)Lio/netty/buffer/CompositeByteBuf;
        at org.elasticsearch.transport.netty4.Netty4Utils.toByteBuf(Netty4Utils.java:117)
        at org.elasticsearch.transport.netty4.Netty4Transport.sendMessage(Netty4Transport.java:395)
        at org.elasticsearch.transport.netty4.Netty4Transport.sendMessage(Netty4Transport.java:94)
        at org.elasticsearch.transport.TcpTransport.internalSendMessage(TcpTransport.java:1125)
        at org.elasticsearch.transport.TcpTransport.sendRequestToChannel(TcpTransport.java:1107)
        at org.elasticsearch.transport.TcpTransport.executeHandshake(TcpTransport.java:1622)
        at org.elasticsearch.transport.TcpTransport.openConnection(TcpTransport.java:556)
        at org.elasticsearch.transport.TcpTransport.openConnection(TcpTransport.java:117)
        at org.elasticsearch.transport.TransportService.openConnection(TransportService.java:334)
        at org.elasticsearch.client.transport.TransportClientNodesService$SimpleNodeSampler.doSample(TransportClientNodesService.java:408)
        at org.elasticsearch.client.transport.TransportClientNodesService$NodeSampler.sample(TransportClientNodesService.java:358)
        at org.elasticsearch.client.transport.TransportClientNodesService$ScheduledNodeSampler.run(TransportClientNodesService.java:391)
        at org.elasticsearch.common.util.concurrent.ThreadContext$ContextPreservingRunnable.run(ThreadContext.java:569)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
        at java.lang.Thread.run(Thread.java:748)