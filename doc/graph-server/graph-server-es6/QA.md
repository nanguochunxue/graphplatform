- 错误
```
[2019-06-26 12:19:25]-[ERROR]-[com.haizhi.graph.server.es6.index.EsIndexDaoImpl.existsIndex(EsIndexDaoImpl.java:63)]-null
javax.net.ssl.SSLHandshakeException: General SSLEngine problem
	at sun.security.ssl.Handshaker.checkThrown(Handshaker.java:1431) ~[?:1.8.0_91]
	at sun.security.ssl.SSLEngineImpl.checkTaskThrown(SSLEngineImpl.java:535) ~[?:1.8.0_91]
	at sun.security.ssl.SSLEngineImpl.writeAppRecord(SSLEngineImpl.java:1214) ~[?:1.8.0_91]
	at sun.security.ssl.SSLEngineImpl.wrap(SSLEngineImpl.java:1186) ~[?:1.8.0_91]
	at javax.net.ssl.SSLEngine.wrap(SSLEngine.java:469) ~[?:1.8.0_91]
	at org.apache.http.nio.reactor.ssl.SSLIOSession.doWrap(SSLIOSession.java:265) ~[httpcore-nio-4.4.5.jar:4.4.5]
	at org.apache.http.nio.reactor.ssl.SSLIOSession.doHandshake(SSLIOSession.java:305) ~[httpcore-nio-4.4.5.jar:4.4.5]
	at org.apache.http.nio.reactor.ssl.SSLIOSession.isAppInputReady(SSLIOSession.java:509) ~[httpcore-nio-4.4.5.jar:4.4.5]
	at org.apache.http.impl.nio.reactor.AbstractIODispatch.inputReady(AbstractIODispatch.java:120) ~[httpcore-nio-4.4.5.jar:4.4.5]
	at org.apache.http.impl.nio.reactor.BaseIOReactor.readable(BaseIOReactor.java:162) ~[httpcore-nio-4.4.5.jar:4.4.5]
	at org.apache.http.impl.nio.reactor.AbstractIOReactor.processEvent(AbstractIOReactor.java:337) ~[httpcore-nio-4.4.5.jar:4.4.5]
	at org.apache.http.impl.nio.reactor.AbstractIOReactor.processEvents(AbstractIOReactor.java:315) ~[httpcore-nio-4.4.5.jar:4.4.5]
	at org.apache.http.impl.nio.reactor.AbstractIOReactor.execute(AbstractIOReactor.java:276) ~[httpcore-nio-4.4.5.jar:4.4.5]
	at org.apache.http.impl.nio.reactor.BaseIOReactor.execute(BaseIOReactor.java:104) ~[httpcore-nio-4.4.5.jar:4.4.5]
	at org.apache.http.impl.nio.reactor.AbstractMultiworkerIOReactor$Worker.run(AbstractMultiworkerIOReactor.java:588) ~[httpcore-nio-4.4.5.jar:4.4.5]
	at java.lang.Thread.run(Thread.java:745) ~[?:1.8.0_91]
	Suppressed: javax.net.ssl.SSLHandshakeException: General SSLEngine problem
		at sun.security.ssl.Handshaker.checkThrown(Handshaker.java:1431) ~[?:1.8.0_91]
		at sun.security.ssl.SSLEngineImpl.checkTaskThrown(SSLEngineImpl.java:535) ~[?:1.8.0_91]
		at sun.security.ssl.SSLEngineImpl.writeAppRecord(SSLEngineImpl.java:1214) ~[?:1.8.0_91]
		at sun.security.ssl.SSLEngineImpl.wrap(SSLEngineImpl.java:1186) ~[?:1.8.0_91]
		at javax.net.ssl.SSLEngine.wrap(SSLEngine.java:469) ~[?:1.8.0_91]
		at org.apache.http.nio.reactor.ssl.SSLIOSession.doWrap(SSLIOSession.java:265) ~[httpcore-nio-4.4.5.jar:4.4.5]
		at org.apache.http.nio.reactor.ssl.SSLIOSession.doHandshake(SSLIOSession.java:305) ~[httpcore-nio-4.4.5.jar:4.4.5]
		at org.apache.http.nio.reactor.ssl.SSLIOSession.isAppInputReady(SSLIOSession.java:509) ~[httpcore-nio-4.4.5.jar:4.4.5]
		at org.apache.http.impl.nio.reactor.AbstractIODispatch.inputReady(AbstractIODispatch.java:120) ~[httpcore-nio-4.4.5.jar:4.4.5]
		at org.apache.http.impl.nio.reactor.BaseIOReactor.readable(BaseIOReactor.java:162) ~[httpcore-nio-4.4.5.jar:4.4.5]
		at org.apache.http.impl.nio.reactor.AbstractIOReactor.processEvent(AbstractIOReactor.java:337) ~[httpcore-nio-4.4.5.jar:4.4.5]
		at org.apache.http.impl.nio.reactor.AbstractIOReactor.processEvents(AbstractIOReactor.java:315) ~[httpcore-nio-4.4.5.jar:4.4.5]
		at org.apache.http.impl.nio.reactor.AbstractIOReactor.execute(AbstractIOReactor.java:276) ~[httpcore-nio-4.4.5.jar:4.4.5]
		at org.apache.http.impl.nio.reactor.BaseIOReactor.execute(BaseIOReactor.java:104) ~[httpcore-nio-4.4.5.jar:4.4.5]
		at org.apache.http.impl.nio.reactor.AbstractMultiworkerIOReactor$Worker.run(AbstractMultiworkerIOReactor.java:588) ~[httpcore-nio-4.4.5.jar:4.4.5]
		at java.lang.Thread.run(Thread.java:745) ~[?:1.8.0_91]
		Suppressed: javax.net.ssl.SSLHandshakeException: General SSLEngine problem
```

- 分析
```
用来CDH的代码连fi环境，或者用fi的代码连cdh环境导致
```