//package com.haizhi.graph.sys.auth.shiro.util;
//
//import com.alibaba.fastjson.JSON;
//import com.haizhi.graph.common.core.login.constant.Constants;
//import com.haizhi.graph.common.core.login.model.RoleVo;
//import com.haizhi.graph.common.core.login.model.UserSysRoleVo;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.subject.Subject;
//import org.springframework.util.CollectionUtils;
//
//import java.util.Objects;
//
///**
// * Created by chengmo on 2018/1/4.
// */
//public class ShiroUtils {
//
//    public static Long getUserID() {
//        try {
//            Subject currentUser = SecurityUtils.getSubject();
//            if (Objects.isNull(currentUser)) {
//                return null;
//            }
//            UserSysRoleVo roleVo = JSON.parseObject(((String) currentUser.getPrincipal()), UserSysRoleVo.class);
//            if (Objects.isNull(roleVo)) {
//                return null;
//            }
//            return roleVo.getId();
//        } catch (Exception e) {
//            // do noting
//        }
//        return null;
//    }
//
//    public static UserSysRoleVo getCurrentUser() {
//        Subject subject = SecurityUtils.getSubject();
//        if (Objects.isNull(subject)) {
//            throw new IllegalArgumentException();
//        }
//        return JSON.parseObject((String) subject.getPrincipal(), UserSysRoleVo.class);
//    }
//
//
//    public static boolean isSupperAdminLogin(){
//        UserSysRoleVo userSysRoleVo = getCurrentUser();
//        if (Objects.isNull(userSysRoleVo) || (CollectionUtils.isEmpty(userSysRoleVo.getRoles()))){
//            return false;
//        }
//        RoleVo superAdminRole = userSysRoleVo.getRoles().stream().filter(roleVo ->
//                Objects.equals(roleVo.getId(), Constants.SUPER_ADMIN_USER_ID)).findFirst().orElse(null);
//        return Objects.nonNull(superAdminRole);
//    }
//}
