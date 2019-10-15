## 需求&目标
> 提供统一登录入口，适配底层的所有登录服务</br>
如：改入口适用gap(分析平台),dap(搜索门户),dmp(数据平台)的登录


## 实现思路
> 平台层(graph)提供两个登录接口：
1、/login  返回在行内是否已登录(boolean)，我们内部系统的接口
2、/ssoLogin (仅适用于参数中带有st的实现方式，如建行实现方式，协调行方的处理)返回cookie供后台解析获取用户信息

```
document.cookie='haizhiJSESSIONID=9e788bac-58f2-4f46-a03f-6c683ae3cb72'
登录信息包含sessionid, 用户名, 角色id列表
应用服务（分析平台，搜索门户）从http请求的head里面获取token，使用token从redis里面获取用户信息（如角色id列表，）
```


## 接口设计

```
sys，grpah，gap，dap，dip
sys-sso
    |-sys-sso-common
        |-com.haizhi.sys.sso.common
            |-log
            |-util
            |-model
                |-SSOLoginUser
                    |-String userNo
                    |-String name
                    |-String phone
                    |-String email
                    |-Set<String> roleCodes;
                    |-Map<String, Object> principal;
                    |-List<Map<String, Object>> roles;
            |-service
                |-SSOUserService
                    |-void register(SSOUserSuo suo) throw Exception
    |-sys-sso-ccb
        |-com.haizhi.sys.sso.ccb
            |-filter
                |-SSOAuthFilter
                    |-SSOUserService
    |-sys-sso-cmb
        |-com.haizhi.sys.sso.cmb
    |-sys-sso-jsyun
        |-com.haizhi.sys.sso.ksyun

graph
    |-graph-sys-auth
        |-com.haizhi.graph.sys.auth.service.impl
            |-SSOUserServiceImpl
                 
```


```
- 1）通用登录 - GAP,DAP,DMP


graph-api
|-com.haizhi.graph.search.api
    |-controller
        |-auth
            |-AuthController.java

graph-sys-auth
|-com.haizhi.graph.sys.auth
    |-service
        |-SysAuthService
            |-impl
                |-SysAuthServiceImpl.java



{
	"userNo": "admin",
	"password": "123456",//hgr4PVo3NuF6G0GmF3f/hg==
	"autoLogin": "N"
}

```





