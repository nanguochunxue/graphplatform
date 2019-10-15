## 1，安装nvm

- 1.1 下载 

```
git clone https://github.com/cnpm/nvm.git
cd nvm
vim install.sh

#找到如下内容：
nvm_latest_version() {
  echo "v0.26.1"
}
修改为：
nvm_latest_version() {
  echo "v0.23.0"
}
```

- 1.2 安装nvm

```
sh install.sh
. ~/.bash_profile
nvm install 8.11.3

#查看已安装的版本：
nvm ls

#使用选定的版本：
nvm use v8.11.3
```

## 2，安装npm

```
# 查看是否安装，如果已安装则不需要重新安装
npm -v

#安装npm(root账户下运行)：
curl http://npmjs.org/install.sh | sh
```

## 3，部署前端DMP

```
进入当前部署路径，以work用户为例：
cd /home/work
mkdir deploy
cd deploy
git clone http://git.sz.haizhi.com/product/gp/gp-data.git -b dev-1.0.0.DMP
cd gp-data

运行（首次运行需要下载依赖大约10分钟，并构建）：
sh run.sh

修改配置文件：
vim config.js
找到如下内容，并修改对应的模块地址为当前实际DMP平台后端模块地址：
const newAPI = 'http://192.168.1.57:10030'      // 元数据、数据接入模块接口地址(graph-api)
const dcStoreAPI = 'http://192.168.1.57:10017'  // 数据源模块接口地址(graph-dc-store)

重载配置并启动：
sh serve.sh

netstat -antp |grep LIST |grep 5010
确认前端进程已启动后，结束

维护命令：
查看当前运行了哪些nodejs服务
./node_modules/.bin/pm2 list

删除当前运行的服务
./node_modules/.bin/pm2 delete gp-data-5010
```