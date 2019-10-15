package com.haizhi.graph.sys.auth.constant;


import com.haizhi.graph.common.constant.Status;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
public enum AuthStatus implements Status {

    // login
    USER_UNAUTH(                10001, "用户未授权"),
    USER_NOT_LOGINED(           10002, "用户未登录"),
    USER_NOT_EXISTS(            10003, "用户不存在"),
    USER_LOGOUT_FAIL(           10004, "用户登出失败"),
    USER_RELOAD_PERMISSION_FAIL(10005, "用户重新加载权限失败"),
    USER_NOT_ADDED(             10200, "当前登录用户尚未被添加到图平台"),
    USER_SAVE_ERROR(            10006, "保存用户信息异常"),
    USER_IS_EXISTS(             10007, "用户已存在"),
    USER_DELETE_ERROR(          10008,"删除用户异常"),
    USER_UPDATE_ERROR(          10009,"修改用户异常"),
    USER_ROLE_IS_EXISTS(        10010,"用户已有该角色"),
    USER_SEARCH_ERROR(          10011,"用户查询异常"),
    USER_LOGIN_NOT_EXISTS(      10012,"登录用户不存在"),
    USER_SWITCH_ERROR(          10013, "用户切换失败"),
    USER_FINDPAGE_ERROR(        10014, "查询用户异常"),
    USER_FIND_ERROR(            10015, "查询用户异常"),
    USER_RESET_PSW_WRONG(       10016,"旧密码不匹配,请重新输入"),
    USER_RESET_ERROR(           10017,"修改用户异常"),
    USER_NAME_OR_PASSWORD_ERROR(10018,"用户名或密码不正确,请重新输入"),
    INVALID_PASSWORD_ERROR(10019,"非法的用户密码"),
    USER_NO_EXISTS(             10020,"用户名已存在"),
    USER_ID_MISS(               10021,"用户id不能为空"),
    USER_DELETE_FORBID(         10022,"用户禁止删除自身"),
    BUILTIN_USER_DELETE_FORBID(10023,"禁止删除内置用户"),
    BUILTIN_USER_READ_ONLY       (10024,"禁止操作内置用户"),
    SUPER_ADMIN_CAN_NOT_GRANT   (10025,"禁止将超级管理员授权给用户"),

    ROLE_SEARCH_ERROR(          10101, "查询角色信息异常"),
    ROLE_NOT_EXISTS(            10102, "角色不存在"),
    ROLE_NAME_EXIST(            10103,"角色名称已存在"),
    ROLE_CODE_EXIST(            10104,"角色代码已存在"),
    ROLE_ID_IS_NULL(            10105, "角色ID为空"),
    ROLE_SAVE_ERROR(            10106, "查询角色信息异常"),
    ROLE_UPDATE_ERROR(          10107, "查询角色信息异常"),
    ROLE_LEVEL_TYPE_ERROR(      10108, "角色层级类型不正确"),
    ROLE_FIND_ERROR(            10109, "查询角色信息异常"),
    ROLE_NOT_URL_AUTH(          10110,"该登录角色没有访问URL的权限"),
    ROLE_DELETE_ERROR(          10111,"删除角色异常"),
    SYS_ROLE_FORBIDDEN_DELETE(  10113,"系统角色无法删除"),

    ROLERESOURCE_SAVE_ERROR(    10501,"角色资源保存失败"),
    ROLERESOURCE_FIND_ERROR(    10502,"角色资源查找失败"),
    ROLERESOURCE_TREE_ERROR(    10503,"角色资源树构建失败"),

    USERROLE_LIST_SEARCH_ERROR( 10301, "查询用户角色列表异常"),
    USERROLE_LOGIN_NOT_EXISTS(  10302,"登录用户所属角色不存在"),
    USERROLE_SAVE_ERROR(        10303, "新增用户角色关系失败"),
    USERROLE_NOT_MATCH(         10304,"选择的角色不属于该用户"),

    PASSWD_MISMATCH(            10607, "密码错误"),
    BLANK_NAME_OR_PWD(          10608, "用户名或密码为空"),
    SSO_USER_IS_NULL(          10609, "单点登录用户信息不能为空"),
    SHIRO_SSO_LOGIN_FAIL(      10610, "内部单点登录失败:{0}"),
    ;

    private int code;
    private String desc;

    AuthStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
