package com.haizhi.graph.sys.auth.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.haizhi.graph.common.core.jpa.JQL;
import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.core.login.constant.Constants;
import com.haizhi.graph.common.core.login.model.RoleVo;
import com.haizhi.graph.common.core.login.model.UserSysRoleVo;
import com.haizhi.graph.common.core.login.utils.ShiroUtils;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.PageQo;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.model.VoTreeBuilder;
import com.haizhi.graph.common.util.AESUtils;
import com.haizhi.graph.sys.auth.constant.*;
import com.haizhi.graph.sys.auth.dao.SysRoleDao;
import com.haizhi.graph.sys.auth.dao.SysUserDao;
import com.haizhi.graph.sys.auth.dao.SysUserRoleDao;
import com.haizhi.graph.sys.auth.model.po.*;
import com.haizhi.graph.sys.auth.model.qo.*;
import com.haizhi.graph.sys.auth.model.suo.SysUserSuo;
import com.haizhi.graph.sys.auth.model.uo.SysUserPwdUo;
import com.haizhi.graph.sys.auth.model.vo.*;
import com.haizhi.graph.sys.auth.service.SysUserService;
import com.haizhi.graph.sys.auth.shiro.redis.SysLoginUserPubService;
import com.haizhi.graph.sys.auth.util.AuthUtils;
import com.haizhi.sys.sso.common.model.SSOLoginUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Service
public class SysUserServiceImpl extends JpaBase implements SysUserService {
    private static final GLog LOG = LogFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    @Autowired
    private SysLoginUserPubService sysLoginUserPubService;

    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    private AuthServiceImpl authService;

    @Override
    public Response<List<SysUserSimpleVo>> find(SysUserQo qo) {
        try {
            QSysUserPo qSysUserPo = QSysUserPo.sysUserPo;
            Predicate predicate = buildUserNameOrUserNoCondition(qo.getName(), qo.getUserNo());
            JPAQuery<SysUserPo> jpaQuery = jpa.select(qSysUserPo)
                    .from(qSysUserPo).where(predicate)
                    .orderBy(QSysUserPo.sysUserPo.updatedDt.desc());
            List<SysUserPo> results = jpaQuery.fetch();
            List<SysUserSimpleVo> ret = new ArrayList<>();
            if (!CollectionUtils.isEmpty(results)) {
                results.forEach(po -> ret.add(new SysUserSimpleVo(po)));
            }
            return Response.success(ret);
        } catch (Exception e) {
            throw new UnexpectedStatusException(AuthStatus.USER_FINDPAGE_ERROR, e);
        }
    }

    @Override
    public Response<List<SysUserSimpleVo>> findUserList(SysUserIdsQo qo) {
        try {
            QSysUserPo qSysUserPo = QSysUserPo.sysUserPo;
            JPAQuery<SysUserPo> jpaQuery = jpa.select(qSysUserPo)
                    .from(qSysUserPo)
                    .where(qSysUserPo.id.in(qo.getUserIdList()))
                    .orderBy(QSysUserPo.sysUserPo.updatedDt.desc());
            List<SysUserPo> results = jpaQuery.fetch();
            List<SysUserSimpleVo> ret = new ArrayList<>();
            if (!CollectionUtils.isEmpty(results)) {
                results.forEach(po -> ret.add(new SysUserSimpleVo(po)));
            }
            return Response.success(ret);
        } catch (Exception e) {
            throw new UnexpectedStatusException(AuthStatus.USER_FINDPAGE_ERROR, e);
        }
    }

    @Override
    public PageResponse<SysUserVo> findPage(SysUserPageQo qo) {
        try {
            PageQo pageQo = qo.getPage();
            PageRequest pageRequest = new PageRequest(pageQo.getPageNo() - 1, pageQo.getPageSize());
            QSysUserPo qSysUserPo = QSysUserPo.sysUserPo;
            QSysUserRolePo qSysUserRolePo = QSysUserRolePo.sysUserRolePo;
            Predicate predicate = buildUserPageCondition(qo);
            //sort
            List<OrderSpecifier> orderSpecifiers = qo.getPage().buildQueryDslOrder(qSysUserPo);
            JPAQuery<SysUserPo> jpaQuery = jpa.select(qSysUserPo)
                    .from(qSysUserPo);
            if (Objects.nonNull(qo.getRoleId())) {
                jpaQuery = jpaQuery.innerJoin(qSysUserRolePo).on(qSysUserRolePo.userId.eq(qSysUserPo.id).and(qSysUserRolePo.roleId.eq(qo.getRoleId())));
            }
            jpaQuery = jpaQuery.where(predicate)
                    .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                    .offset(pageRequest.getOffset())
                    .limit(pageRequest.getPageSize());
            List<SysUserPo> results = jpaQuery.fetch();
            List<SysUserVo> ret = new ArrayList<>();
            if (!CollectionUtils.isEmpty(results)) {
                results.forEach(po -> ret.add(new SysUserVo(po)));
            }
            this.completeUserSysRoles(ret);
            return PageResponse.success(ret, jpaQuery.fetchCount(), pageQo);
        } catch (Exception e) {
            throw new UnexpectedStatusException(AuthStatus.USER_FINDPAGE_ERROR, e);
        }
    }

    @Override
    public UserSysRoleVo findUserRole(String userNo) {
        SysUserPo user = sysUserDao.findByUserNo(userNo);
        if (Objects.isNull(user)) {
            throw new UnexpectedStatusException(AuthStatus.USER_NOT_EXISTS);
        }
        UserSysRoleVo userSysRoleVo = new UserSysRoleVo();
        userSysRoleVo.setId(user.getId());
        userSysRoleVo.setName(user.getName());
        userSysRoleVo.setUserNo(user.getUserNo());
        userSysRoleVo.setPassword(user.getPassword());
        QSysRolePo qSysRolePo = QSysRolePo.sysRolePo;
        QSysUserRolePo qSysUserRolePo = QSysUserRolePo.sysUserRolePo;
        JPAQuery<Tuple> jpaQuery = jpa.select(qSysRolePo, qSysUserRolePo)
                .from(qSysRolePo).leftJoin(qSysUserRolePo).on(qSysRolePo.id.eq(qSysUserRolePo.roleId))
                .where(qSysUserRolePo.userId.eq(user.getId()));
        List<Tuple> result = jpaQuery.fetch();
        List<RoleVo> roleVos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(result)) {
            result.forEach(tuple -> {
                SysRolePo sysRolePo = tuple.get(qSysRolePo);
                RoleVo roleVo = new RoleVo();
                roleVo.setId(sysRolePo.getId());
                roleVo.setName(sysRolePo.getName());
                roleVo.setUpdateTime(sysRolePo.getUpdatedDt());
                roleVos.add(roleVo);
            });
        }
        userSysRoleVo.setRoles(roleVos);
        return userSysRoleVo;
    }

    @Override
    public Response<CheckExistVo> checkUserNo(SysCheckUserNoQo qo) {
        SysUserPo po = sysUserDao.findByUserNo(qo.getUserNo());
        CheckExistVo checkExistVo = new CheckExistVo();
        checkExistVo.setExist(Objects.nonNull(po));
        return Response.success(checkExistVo);
    }

    @Override
    @Transactional
    public Response saveOrUpdate(SysUserSuo suo) {
        if (Objects.isNull(suo.getId())) {
            return this.save(suo);
        } else {
            Response response = this.update(suo);
            sysLoginUserPubService.publishByUserId(suo.getId());
            return response;
        }
    }

    @Override
    @Transactional
    public void register(SSOLoginUser ssoLoginUser) {
        if (Objects.isNull(ssoLoginUser)){
            throw new UnexpectedStatusException(AuthStatus.SSO_USER_IS_NULL);
        }
        Long userId;
        Boolean isNewUser = false;
        SysUserPo newUserPo = buildSysUserPo(ssoLoginUser);
        SysUserPo oldUserPo = sysUserDao.findByUserNo(ssoLoginUser.getUserNo());
        if (Objects.isNull(oldUserPo)){
            newUserPo.setPassword(AuthUtils.getEncryptedPwd(Constants.DEFAULT_USER_PWD));
            SysUserPo savedUserPo = sysUserDao.save(newUserPo);
            userId = savedUserPo.getId();
            isNewUser = true;
        } else {
            userId = oldUserPo.getId();
            Boolean needUpdate = false;
            if ((!Objects.equals(newUserPo.getName(),ssoLoginUser.getName()))
                    || (!Objects.equals(newUserPo.getEmail(),ssoLoginUser.getEmail()))
                    || (!Objects.equals(newUserPo.getPhone(),ssoLoginUser.getPhone()))){
                needUpdate = true;
            }
            if (needUpdate){
                newUserPo.setId(userId);
                newUserPo.setPassword(oldUserPo.getPassword());
                sysUserDao.save(newUserPo);
            }
        }
        Set<String> codes = ssoLoginUser.getRoleCodes();
        if (!org.apache.commons.collections.CollectionUtils.isEmpty(codes)){
            List<SysRolePo> sysRolePos = sysRoleDao.findByCodeIn(codes);
            if (!org.apache.commons.collections.CollectionUtils.isEmpty(sysRolePos)){
                sysUserRoleDao.deleteByUserId(userId);
                List<SysUserRolePo> sysUserRolePos = sysRolePos.stream().map(sysRolePo ->{
                    SysUserRolePo sysUserRolePo =   new SysUserRolePo() ;
                    sysUserRolePo.setUserId(userId);
                    sysUserRolePo.setRoleId(sysRolePo.getId());
                    return sysUserRolePo;
                } ).collect(Collectors.toList());
                sysUserRoleDao.save(sysUserRolePos);
            }
        } else {
            if (isNewUser){
                SysUserRolePo sysUserRolePo =   new SysUserRolePo() ;
                sysUserRolePo.setUserId(userId);
                sysUserRolePo.setRoleId(SysRole.GENERAL_USER.getCode());
                sysUserRoleDao.save(sysUserRolePo);
            }
        }

    }

    @Override
    @Transactional
    public Response updatePassword(SysUserPwdUo uo) {
        SysUserPo sysUserPo = sysUserDao.findByUserNo(uo.getUserNo());
        if (Objects.isNull(sysUserPo)) {
            return Response.error(AuthStatus.USER_NOT_EXISTS);
        }
        try {
            String encryptedPwd = sysUserPo.getPassword();
            String passwordOld = AESUtils.decrypt(uo.getPasswordOld());
            String passwordNew = AESUtils.decrypt(uo.getPassword());
            if (!AuthUtils.validPassword(passwordOld, encryptedPwd)) {
                return Response.error(AuthStatus.USER_RESET_PSW_WRONG);
            }

            sysUserPo.setPassword(AuthUtils.getEncryptedPwd(passwordNew));
            this.setUpdateBy(sysUserPo);
            sysUserDao.save(sysUserPo);
        } catch (Exception e) {
            throw new UnexpectedStatusException(AuthStatus.USER_RESET_ERROR, e);
        }
        //logout current user
        authService.logout();
        return Response.success();
    }

    @Override
    @Transactional
    public Response resetPassword(Long userId) {
        SysUserPo sysUserPo = sysUserDao.findOne(userId);
        if (Objects.isNull(sysUserPo)) {
            return Response.error(AuthStatus.USER_NOT_EXISTS);
        }
        String password = AuthUtils.getEncryptedPwd(AuthUtils.DEFAULT_PWD);
        sysUserPo.setPassword(password);
        this.setUpdateBy(sysUserPo);
        sysUserDao.save(sysUserPo);
        return Response.success();
    }

    @Override
    @Transactional
    public Response delete(Long userId) {
        try {
            if (Objects.equals(userId, ShiroUtils.getUserID())) {
                return Response.error(AuthStatus.USER_DELETE_FORBID);
            }
            if (userId <= Constants.SUPER_ADMIN_USER_ID) {
                return Response.error(AuthStatus.BUILTIN_USER_DELETE_FORBID);
            }
            SysUserPo sysUserPo = sysUserDao.findOne(userId);
            if (Objects.nonNull(sysUserPo)) {
                sysUserDao.delete(userId);
                sysUserRoleDao.deleteByUserId(userId);
            }
        } catch (Exception e) {
            throw new UnexpectedStatusException(AuthStatus.USER_DELETE_ERROR, e);
        }
        return Response.success();
    }

    @Override
    public Response<List<SysResourceVo>> findUserResources(SysUserResourceQo qo) {
        QSysResourcePo qSysResourcePo = QSysResourcePo.sysResourcePo;
        QSysUserRolePo qSysUserRolePo = QSysUserRolePo.sysUserRolePo;
        QSysRoleResourcePo qSysRoleResourcePo = QSysRoleResourcePo.sysRoleResourcePo;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (Objects.nonNull(qo.getType())) {
            booleanBuilder = booleanBuilder.and(qSysResourcePo.type.eq(qo.getType().getCode()));
        }
        if (Objects.nonNull(qo.getType())) {
            booleanBuilder = booleanBuilder.and(qSysResourcePo.group.eq(qo.getGroup().getCode()));
        }
        List<SysResourcePo> sysResourcePos = jpa.select(qSysResourcePo)
                .from(qSysResourcePo)
                .where(booleanBuilder.getValue()).fetch();

        List<Long> resourceIds = jpa.select(qSysRoleResourcePo.resourceId)
                .distinct().from(qSysRoleResourcePo)
                .leftJoin(qSysUserRolePo).on(qSysRoleResourcePo.roleId.eq(qSysUserRolePo.roleId))
                .where(qSysUserRolePo.userId.eq(qo.getUserId()))
                .fetch();

        List<SysResourceVo> sysResourceVos = sysResourcePos.stream().map(sysResourcePo -> {
            SysResourceVo resourceVo = new SysResourceVo(sysResourcePo);
            resourceVo.setChecked(resourceIds.contains(sysResourcePo.getId()));
            return resourceVo;
        }).collect(Collectors.toList());
        return Response.success(sysResourceVos);
    }

    @Override
    public List<Long> getUserIdByRoleId(Long roleId) {
        QSysUserRolePo sysUserRolePo = QSysUserRolePo.sysUserRolePo;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(sysUserRolePo.roleId.eq(roleId));
        return jpa.select(sysUserRolePo.userId).from(sysUserRolePo).where(builder).fetch();
    }

    @Override
    public Set<String> getUserResourceURL(Long userId) {
        if (Objects.isNull(userId)) {
            return Sets.newHashSet();
        }
        List<String> resourceUrlList = getResourceByUserId(userId);
        return resourceUrlList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    }

    @Override
    public SysUserPo findOne(Long userId) {
        return sysUserDao.findOne(userId);
    }


    @Override
    public LoginUserVo getLoginUserVo(Long userId) {
        SysUserPo userPo = findOne(userId);
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(userId)
                .setUserNo(userPo.getUserNo())
                .setUserName(userPo.getName())
                .setPhone(userPo.getPhone())
                .setEmail(userPo.getEmail())
                .setStatus(userPo.getStatus());
        // load role and resource
        this.loadResource(loginUserVo);
        // build resource tree group by type
        this.buildResourceTree(loginUserVo);
        return loginUserVo;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private Predicate andAdminAndBultInUsersFilter(Predicate predicate) {
        Predicate newPredicate;
        if (ShiroUtils.isSupperAdminLogin()) {
            newPredicate = QSysUserPo.sysUserPo.id.goe(Constants.SUPER_ADMIN_USER_ID);
        } else {
            newPredicate = QSysUserPo.sysUserPo.id.gt(Constants.SUPER_ADMIN_USER_ID);
        }
        return ((BooleanExpression) newPredicate).and(predicate);
    }

    private Predicate buildUserPageCondition(SysUserPageQo qo) {
        QSysUserPo sysUserPo = QSysUserPo.sysUserPo;
        Predicate predicate = buildUserNameOrUserNoCondition(qo.getName(), qo.getUserNo());
        if (StringUtils.isNotBlank(qo.getUserSource())) {
            if (Objects.isNull(predicate)) {
                predicate = sysUserPo.userSource.eq(qo.getUserSource());
            } else {
                predicate = sysUserPo.userSource.eq(qo.getUserSource()).and(predicate);
            }
        }
        return predicate;
    }


    private Predicate buildUserNameOrUserNoCondition(String userName, String userNo) {
        BooleanExpression booleanExpression = null;
        if (StringUtils.isNoneBlank(userName)) {
            booleanExpression = QSysUserPo.sysUserPo.name.like(JQL.likeWrap(userName));
        }
        if (StringUtils.isNoneBlank(userNo)) {
            if (Objects.isNull(booleanExpression)) {
                booleanExpression = QSysUserPo.sysUserPo.userNo.like(JQL.likeWrap(userNo));
            } else {
                booleanExpression = booleanExpression.or(QSysUserPo.sysUserPo.userNo.like(JQL.likeWrap(userNo)));
            }
        }
        return andAdminAndBultInUsersFilter(booleanExpression);
    }

    private void completeUserSysRoles(List<SysUserVo> sysUsers) {
        if (CollectionUtils.isEmpty(sysUsers)) {
            return;
        }
        QSysUserRolePo qSysUserRolePo = QSysUserRolePo.sysUserRolePo;
        QSysRolePo qSysRolePo = QSysRolePo.sysRolePo;
        Set<Long> userIds = sysUsers.stream().map(SysUserVo::getId).collect(Collectors.toSet());
        BooleanBuilder builder = buildUserRoleCondition(userIds);
        JPAQuery<SysUserRoleVo> jpaQuery = jpa.select(
                Projections.bean(SysUserRoleVo.class,
                        qSysUserRolePo.userId.as("userId"),
                        qSysUserRolePo.roleId.as("roleId"),
                        qSysUserRolePo.id.as("id"),
                        qSysRolePo.remark.as("remark"),
                        qSysRolePo.name.as("roleName")))
                .from(qSysUserRolePo)
                .leftJoin(qSysRolePo).on(qSysUserRolePo.roleId.eq(qSysRolePo.id))
                .where(builder);
        List<SysUserRoleVo> sysUserRoles = jpaQuery.fetch();
        sysUsers.forEach(sysUserVo -> {
            List<SysUserRoleVo> userRoleVos = sysUserRoles.stream().filter(sysUserRoleVo ->
                    Objects.equals(sysUserVo.getId(), sysUserRoleVo.getUserId())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(userRoleVos)) {
                userRoleVos.forEach(sysUserRoleVo -> {
                    SysRoleVo roleVo = new SysRoleVo();
                    roleVo.setId(sysUserRoleVo.getRoleId());
                    roleVo.setName(sysUserRoleVo.getRoleName());
                    sysUserVo.getRoles().add(roleVo);
                });
            }
        });
    }

    private BooleanBuilder buildUserRoleCondition(Set<Long> userIds) {
        BooleanBuilder builder = new BooleanBuilder();
        QSysUserRolePo qSysUserRolePo = QSysUserRolePo.sysUserRolePo;
        if (!CollectionUtils.isEmpty(userIds)) {
            builder.and(qSysUserRolePo.userId.in(userIds));
        }
        return builder;
    }

    private Response save(SysUserSuo suo) {
        try {
            if (suo.getRoleIds().contains(SysRole.SUPPER_MANAGER.getCode())) {
                return Response.error(AuthStatus.SUPER_ADMIN_CAN_NOT_GRANT);
            }
            SysUserPo sysUserPo = sysUserDao.findByUserNo(suo.getUserNo());
            if (Objects.nonNull(sysUserPo)) {
                return Response.error(AuthStatus.USER_NO_EXISTS);
            }
            SysUserPo po = new SysUserPo();
            po.update(suo, true);
            this.setUpdateBy(po);
            SysUserPo result = sysUserDao.save(po);
            if (Objects.isNull(result)) {
                return Response.error(AuthStatus.USER_SAVE_ERROR);
            }
            if (!CollectionUtils.isEmpty(suo.getRoleIds())) {
                List<SysUserRolePo> sysUserRolePos = suo.getRoleIds().stream().map(sysRoleId -> {
                    SysUserRolePo sysUserRolePo = new SysUserRolePo();
                    sysUserRolePo.setUserId(result.getId());
                    sysUserRolePo.setRoleId(sysRoleId);
                    return sysUserRolePo;
                }).collect(Collectors.toList());
                sysUserRoleDao.save(sysUserRolePos);
            }
        } catch (Exception e) {
            throw new UnexpectedStatusException(AuthStatus.USER_SAVE_ERROR, e);
        }
        return Response.success();
    }

    private Response update(SysUserSuo suo) {
        try {
            Long userId = suo.getId();
            if (Objects.isNull(userId)) {
                return Response.error(AuthStatus.USER_ID_MISS);
            }
            if (suo.getId() <= Constants.SUPER_ADMIN_USER_ID) {
                return Response.error(AuthStatus.BUILTIN_USER_READ_ONLY);
            }
            if (suo.getRoleIds().contains(SysRole.SUPPER_MANAGER.getCode())) {
                return Response.error(AuthStatus.SUPER_ADMIN_CAN_NOT_GRANT);
            }
            SysUserPo sysUserPo = sysUserDao.findOne(userId);
            if (Objects.isNull(sysUserPo)) {
                return Response.error(AuthStatus.USER_NOT_EXISTS);
            }
            SysUserPo tmpSysUserPo = sysUserDao.findByUserNo(suo.getUserNo());
            if (Objects.nonNull(tmpSysUserPo)) {
                if (!Objects.equals(suo.getId(), tmpSysUserPo.getId())
                        && (Objects.equals(suo.getUserNo(), tmpSysUserPo.getUserNo()))) {
                    return Response.error(AuthStatus.USER_NO_EXISTS);
                }
            }
            sysUserPo.update(suo, false);
            this.setUpdateBy(sysUserPo);
            sysUserDao.save(sysUserPo);
            sysUserRoleDao.deleteByUserId(userId);
            if (!CollectionUtils.isEmpty(suo.getRoleIds())) {
                List<SysUserRolePo> sysUserRolePos = suo.getRoleIds().stream()
                        .filter(roleId -> !Objects.equals(roleId, SysRole.SUPPER_MANAGER.getCode())).map(sysRoleId -> {
                            SysUserRolePo sysUserRolePo = new SysUserRolePo();
                            sysUserRolePo.setUserId(userId);
                            sysUserRolePo.setRoleId(sysRoleId);
                            return sysUserRolePo;
                        }).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(sysUserRolePos)) {
                    sysUserRoleDao.save(sysUserRolePos);
                }
            }
        } catch (Exception e) {
            throw new UnexpectedStatusException(AuthStatus.USER_SAVE_ERROR, e);
        }
        return Response.success();
    }

    private List<String> getResourceByUserId(Long userId) {
        QSysUserRolePo sysUserRolePo = QSysUserRolePo.sysUserRolePo;
        QSysRoleResourcePo sysRoleResourcePo = QSysRoleResourcePo.sysRoleResourcePo;
        QSysResourcePo sysResourcePo = QSysResourcePo.sysResourcePo;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(sysUserRolePo.userId.eq(userId));
        builder.and(sysResourcePo.type.in(ImmutableList.of(SysType.SYS.getCode(), SysType.DMP.getCode())));
        List<String> result = jpa.select(sysResourcePo.url).from(sysResourcePo)
                .leftJoin(sysRoleResourcePo).on(sysRoleResourcePo.resourceId.eq(sysResourcePo.id))
                .leftJoin(sysUserRolePo).on(sysRoleResourcePo.roleId.eq(sysUserRolePo.roleId))
                .where(builder)
                .fetch();
        return result;
    }

    private void loadResource(LoginUserVo loginUserVo) {
        QSysUserRolePo sysUserRolePo = QSysUserRolePo.sysUserRolePo;
        QSysRolePo sysRolePo = QSysRolePo.sysRolePo;
        QSysResourcePo sysResourcePo = QSysResourcePo.sysResourcePo;
        QSysRoleResourcePo sysRoleResourcePo = QSysRoleResourcePo.sysRoleResourcePo;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(sysUserRolePo.userId.eq(loginUserVo.getId()));
        builder.and(sysResourcePo.name.isNotNull());
        builder.and(sysRolePo.name.isNotNull());
        List<Tuple> tuples = jpa.select(
                sysRolePo.id,
                sysRolePo.name,
                sysRolePo.remark,
                sysResourcePo.id,
                sysResourcePo.name,
                sysResourcePo.parentId,
                sysResourcePo.url,
                sysResourcePo.remark,
                sysResourcePo.type,
                sysResourcePo.subType,
                sysResourcePo.group,
                sysResourcePo.code
        ).from(sysUserRolePo)
                .leftJoin(sysRolePo).on(sysUserRolePo.roleId.eq(sysRolePo.id))
                .leftJoin(sysRoleResourcePo).on(sysRolePo.id.eq(sysRoleResourcePo.roleId))
                .leftJoin(sysResourcePo).on(sysRoleResourcePo.resourceId.eq(sysResourcePo.id))
                .where(builder)
                .fetch();
        Map<String, SysRoleVo> roleVoMap = new HashMap<>(16);
        Map<String, List<SysResourceVo>> platResources = new HashMap<>();
        Set<Long> resourceIdSet = new HashSet<>();
        tuples.forEach(tuple -> {
            roleVoMap.putIfAbsent(tuple.get(sysRolePo.name),
                    new SysRoleVo().setId(tuple.get(sysRolePo.id))
                            .setName(tuple.get(sysRolePo.name)));
            String sysType = tuple.get(sysResourcePo.type);
            if (Objects.equals(SysType.GAP, SysType.fromCode(sysType))) {
                String authGroup = tuple.get(sysResourcePo.group);
                if (!Objects.equals(AuthGroup.S, AuthGroup.fromCode(authGroup))) {
                    // just get GAP resource with AuthGroup.S
                    return;
                }
            }
            Long resourceId = tuple.get(sysResourcePo.id);
            if (resourceIdSet.contains(resourceId)) {
                //do not add repeat resource
                return;
            }
            resourceIdSet.add(resourceId);
            SysResourceVo resourceVo = new SysResourceVo();
            resourceVo.setName(tuple.get(sysResourcePo.name));
            resourceVo.setRemark(tuple.get(sysResourcePo.remark));
            resourceVo.setUrl(tuple.get(sysResourcePo.url));
            resourceVo.setType(tuple.get(sysResourcePo.type));
            resourceVo.setSubType(tuple.get(sysResourcePo.subType));
            resourceVo.setId(resourceId);
            resourceVo.setParentId(tuple.get(sysResourcePo.parentId));
            resourceVo.setGroup(tuple.get(sysResourcePo.group));
            resourceVo.setCode(tuple.get(sysResourcePo.code));
            platResources.computeIfAbsent(tuple.get(sysResourcePo.type), list -> new ArrayList<>())
                    .add(resourceVo);
        });
        loginUserVo.setSysRoles(new ArrayList<>(roleVoMap.values()));
        loginUserVo.setResourceTrees(platResources);
    }

    private void buildResourceTree(LoginUserVo loginUserVo) {
        Map<String, List<SysResourceVo>> resourceTreeVos = loginUserVo.getResourceTrees();
        if (Objects.nonNull(resourceTreeVos)) {
            resourceTreeVos.forEach((key, value) -> {
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(value)) {
                    try {
                        loginUserVo.getResourceTrees().put(key, VoTreeBuilder.createTreeList(value, SysResourceVo.class));
                    } catch (Exception e) {
                        LOG.error("build resource Tree error , userId:" + loginUserVo.getId(), e);
                    }
                }
            });
        }
    }

    private SysUserPo buildSysUserPo(SSOLoginUser ssoLoginUser) {
        SysUserPo sysUserPo = new SysUserPo();
        sysUserPo.setUserNo(ssoLoginUser.getUserNo());
        sysUserPo.setName(ssoLoginUser.getName());
        sysUserPo.setPhone(ssoLoginUser.getPhone());
        sysUserPo.setEmail(ssoLoginUser.getEmail());
        sysUserPo.setUserSource(UserSource.SSO.getCode());
        sysUserPo.setStatus(com.haizhi.graph.common.constant.Constants.AVAILABLE);
        return sysUserPo;
    }
}
