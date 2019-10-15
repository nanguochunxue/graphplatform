> 该目录为工程安装的输出目录

### 打包命令

- cdh/hdp

mvn clean install -Dmaven.test.skip=true -Pdeploy

- fic60/fic70/fic80

mvn clean install -Dmaven.test.skip=true -Pdeploy-fi

- tdh

mvn clean install -Dmaven.test.skip=true -Pdeploy-tdh