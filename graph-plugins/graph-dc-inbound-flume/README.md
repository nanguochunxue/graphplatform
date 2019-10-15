- 提供两种使用方式
- 安装运行方式，长驻进程
- 一次运行方式

- 代码
- com.haizhi.graph.plugins.flume.embedded 从flume-embedded拷贝过来，修改了EmbeddedAgentConfiguration.ALLOWED_SINKS 对sink的限制。
- com.haizhi.graph.plugins.flume.source.taildir 从flume-source-taildir拷贝过来，修改了TailFile处理csv文件，需要读取第一行并放到header。
- interceptor 使用flume提供的扩展方式的自定义扩展，在配置文件中定义
- 修改taildir支持目录目录的模糊匹配，如搜集/data/logs/.*/log-.*的日志，
    这时需要指定扫描目录为/data/logs/；默认情况下/data/logs/.*/log-.*会设定扫描目录为/data/logs/.*，但该目录明显不存在，
    故添加配置点scandir，对应对象为FileFinder，修改文件识别方式为Files.walk。源方式为Files.newDirectoryStream。
    也就是说，旧方式将filePattern的上一层目录假定为实际目录，使用newDirectoryStream来扫描文件，并用filePattern匹配文件
    新方式为，指定scandir，使用Files.walk扫描文件，并用filePattern来匹配文件，即支持目录的正则模糊


- 安装方式
- 从taildirsource读取数据后，经过interceptor处理，将数据定义为正常/异常，并在header标记。channelSelector根据header标记选择正常channel/异常channel。
- 正常channel由BatchGapSink消费，take的Event为文件的单行数据，在取出一个batch后，整理为若干个cuo对象，依次发送
- 异常channel由pollFileSink消费后，写入指定的文件

- 依次运行方式
- 该方式需要自定义写入agent的方式。这里从文件读取的一个cuo的row包含多行数据，SimpleGapSink取出后直接发网http端口

