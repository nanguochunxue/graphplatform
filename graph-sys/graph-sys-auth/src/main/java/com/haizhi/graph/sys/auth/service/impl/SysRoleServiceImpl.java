package com.haizhi.graph.sys.auth.service.impl;

import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.core.login.constant.Constants;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.model.VoTreeBuilder;
import com.haizhi.graph.sys.auth.constant.AuthGroup;
import com.haizhi.graph.sys.auth.constant.AuthStatus;
import com.haizhi.graph.sys.auth.constant.SysRole;
import com.haizhi.graph.sys.auth.constant.SysType;
import com.haizhi.graph.sys.auth.dao.SysResourceDao;
import com.haizhi.graph.sys.auth.dao.SysRoleDao;
import com.haizhi.graph.sys.auth.dao.SysRoleResourceDao;
import com.haizhi.graph.sys.auth.dao.SysUserRoleDao;
import com.haizhi.graph.sys.auth.model.po.*;
import com.haizhi.graph.sys.auth.model.qo.SysCheckRoleCodeQo;
import com.haizhi.graph.sys.auth.model.qo.SysCheckRoleNameQo;
import com.haizhi.graph.sys.auth.model.qo.SysRoleResourceQo;
import com.haizhi.graph.sys.auth.model.qo.SysRoleResourceTreeQo;
import com.haizhi.graph.sys.auth.model.suo.SysRoleSuo;
import com.haizhi.graph.sys.auth.model.vo.*;
import com.haizhi.graph.sys.auth.service.SysRoleService;
import com.haizhi.graph.sys.auth.service.SysUserService;
import com.haizhi.graph.sys.auth.shiro.redis.SysLoginUserPubService;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tanghaiyang on 2019/2/26.
 */
@Service
public class SysRoleServiceImpl extends JpaBase implements SysRoleService {

    private static final GLog LOG = LogFactory.getLogger(SysRoleServiceImpl.class);

    @Autowired
    private SysRoleResourceDao sysRoleResourceDao;

    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    @Autowired
    private SysResourceDao sysResourceDao;

    @Autowired
    private SysLoginUserPubService sysLoginUserPubService;

    @Autowired
    private SysUserService sysUserService;

    @Override
    @SuppressWarnings("unchecked")
    public Response<SysRoleListVo> findSysRoleList() {
        List<SysRolePo> sysRolePos = sysRoleDao.findAll();
        List<SysRoleVo> roleVos = new ArrayList<>();
        if(Objects.isNull(sysRolePos)){
            return Response.error("sys role find failed");
        }
        sysRolePos.forEach(rolePo -> roleVos.add(new SysRoleVo(rolePo)));
        SysRoleListVo sysRoleListVo = new SysRoleListVo();
        roleVos.forEach(sysRoleVo -> {
            SysRole sysRole = SysRole.fromCode(sysRoleVo.getId());
            if (Objects.nonNull(sysRole)) {
                //remove superadmin
                if (!Objects.equals(SysRole.SUPPER_MANAGER, sysRole)) {
                    sysRoleListVo.getPredefineRoles().add(0, sysRoleVo);
                }
            } else {
                sysRoleListVo.getCustomizeRoles().add(0, sysRoleVo);
            }
        });
        return Response.success(sysRoleListVo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<SysRoleResourceVo> findRoleResourceTree(SysRoleResourceTreeQo qo) {
        SysRolePo sysRolePo;
        if (Objects.nonNull(qo.getRoleId())) {
            sysRolePo = sysRoleDao.findOne(qo.getRoleId());
            if (Objects.isNull(sysRolePo)) {
                return Response.error(AuthStatus.ROLE_NOT_EXISTS);
            }
        } else {
            sysRolePo = new SysRolePo();
        }
        SysRoleResourceVo sysRoleResourceVo = new SysRoleResourceVo(sysRolePo);
        QSysResourcePo qSysResourcePo = QSysResourcePo.sysResourcePo;
        QSysRoleResourcePo qSysRoleResourcePo = QSysRoleResourcePo.sysRoleResourcePo;
        BooleanExpression leftJoinExpression = qSysRoleResourcePo.resourceId.eq(qSysResourcePo.id);
        if (Objects.nonNull(qo.getRoleId())) {
            leftJoinExpression = leftJoinExpression.and(qSysRoleResourcePo.roleId.eq(qo.getRoleId()));
        } else {
            leftJoinExpression = leftJoinExpression.and(qSysRoleResourcePo.roleId.isNull());
        }
        List<Tuple> tuples = jpa.select(
                qSysResourcePo.id,
                qSysResourcePo.parentId,
                qSysResourcePo.name,
                qSysResourcePo.remark,
                qSysResourcePo.url,
                qSysResourcePo.type,
                qSysResourcePo.subType,
                qSysResourcePo.group,
                qSysResourcePo.code,
                qSysRoleResourcePo.roleId
        ).from(qSysResourcePo)
                .leftJoin(qSysRoleResourcePo).on(leftJoinExpression)
                .where(qSysResourcePo.type.eq(qo.getSysType().getCode()))
                .orderBy(qSysResourcePo.id.asc())
                .fetch();
        if (CollectionUtils.isEmpty(tuples)) {
            return Response.success();
        }
        List<SysResourceVo> roleResourceTreeVos = tuples.stream().map(tuple -> {
            String authGroup = tuple.get(qSysResourcePo.group);
            String type = tuple.get(qSysResourcePo.type);
            if (Objects.equals(type, SysType.GAP.getCode())
                    && !Objects.equals(AuthGroup.S, AuthGroup.fromCode(authGroup))) {
                // just get GAP resource with AuthGroup.S
                return null;
            }
            SysResourceVo resourceVo = new SysResourceVo();
            resourceVo.setId(tuple.get(qSysResourcePo.id));
            resourceVo.setParentId(tuple.get(qSysResourcePo.parentId));
            resourceVo.setName(tuple.get(qSysResourcePo.name));
            resourceVo.setRemark(tuple.get(qSysResourcePo.remark));
            resourceVo.setUrl(tuple.get(qSysResourcePo.url));
            resourceVo.setType(tuple.get(qSysResourcePo.type));
            resourceVo.setSubType(tuple.get(qSysResourcePo.subType));
            resourceVo.setGroup(tuple.get(qSysResourcePo.group));
            resourceVo.setCode(tuple.get(qSysResourcePo.code));
            resourceVo.setChecked(Objects.nonNull(tuple.get(qSysRoleResourcePo.roleId)));
            return resourceVo;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List<SysResourceVo> roleResourceTree;
        try {
            roleResourceTree = VoTreeBuilder.createTreeList(roleResourceTreeVos, SysResourceVo.class);
            sysRoleResourceVo.setResources(roleResourceTree);
            return Response.success(sysRoleResourceVo);
        } catch (Exception e) {
            return Response.error(AuthStatus.ROLERESOURCE_TREE_ERROR, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<CheckExistVo> checkRoleName(SysCheckRoleNameQo qo) {
        CheckExistVo checkExistVo = new CheckExistVo();
        boolean isExist = checkNameExists(qo.getRoleId(), qo.getRoleName());
        checkExistVo.setExist(isExist);
        return Response.success(checkExistVo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<CheckExistVo> checkRoleCode(SysCheckRoleCodeQo qo) {
        CheckExistVo checkExistVo = new CheckExistVo();
        boolean isExist = checkCodeExists(qo.getRoleId(), qo.getCode());
        checkExistVo.setExist(isExist);
        return Response.success(checkExistVo);
    }

    @Override
    @Transactional
    public Response saveOrUpdate(SysRoleSuo suo) {
        boolean isNameExist = checkNameExists(suo.getId(), suo.getName());
        if (isNameExist) {
            return Response.error(AuthStatus.ROLE_NAME_EXIST);
        }
        boolean isCodeExist = checkCodeExists(suo.getId(), suo.getCode());
        if (isCodeExist) {
            return Response.error(AuthStatus.ROLE_CODE_EXIST);
        }
        SysRolePo sysRolePo;
        SysRolePo savedSysRolePo;
        if (Objects.isNull(suo.getId())) {
            //save
            sysRolePo = new SysRolePo();
            sysRolePo.setName(suo.getName());
            sysRolePo.setRemark(suo.getRemark());
            sysRolePo.setCode(suo.getCode());
            this.setUpdateBy(sysRolePo);
            savedSysRolePo = sysRoleDao.save(sysRolePo);
        } else {
            //update
            sysRolePo = sysRoleDao.findOne(suo.getId());
            if (Objects.isNull(sysRolePo)) {
                return Response.error(AuthStatus.ROLE_NOT_EXISTS);
            }
            this.setUpdateBy(sysRolePo);
            sysRolePo.setCode(suo.getCode());
            if (Objects.nonNull(SysRole.fromCode(suo.getId()))) {
                savedSysRolePo = sysRolePo;
                sysRoleDao.updateRoleCode(sysRolePo.getId(), sysRolePo.getCode(), sysRolePo.getUpdateById());
            } else {
                sysRolePo.setName(suo.getName());
                sysRolePo.setRemark(suo.getRemark());
                savedSysRolePo = sysRoleDao.save(sysRolePo);
            }

        }
        if (Objects.isNull(suo.getId())) {
            suo.setId(savedSysRolePo.getId());
        }
        this.saveOrUpdateRoleResource(suo);
        sysLoginUserPubService.publishByRoleId(savedSysRolePo.getId());
        return Response.success();
    }

    @Override
    @Transactional
    public Response delete(Long roleId) {
        List<Long> userIds;
        try {
            if (Objects.nonNull(SysRole.fromCode(roleId))) {
                return Response.error(AuthStatus.SYS_ROLE_FORBIDDEN_DELETE);
            }
            userIds = sysUserService.getUserIdByRoleId(roleId);
            SysRolePo sysRolePo = sysRoleDao.findOne(roleId);
            if (Objects.nonNull(sysRolePo)) {
                sysRoleDao.delete(roleId);
                this.handleUserRoleWhenDeleteRole(roleId);
            }
        } catch (Exception e) {
            throw new UnexpectedStatusException(AuthStatus.ROLE_DELETE_ERROR, e);
        }
        sysLoginUserPubService.publishByUserIds(userIds);
        return Response.success();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<List<SysResourceVo>> findRoleResources(SysRoleResourceQo qo) {
        try {
            Long roleId = qo.getRoleId();
            SysType type = qo.getType();
            AuthGroup authGroup = qo.getGroup();
            if (Objects.isNull(roleId) || Objects.isNull(type)) {
                String msg = "roleId is null or type is null";
                LOG.error(msg);
                return Response.error(msg);
            }
            QSysRoleResourcePo qSysRoleResourcePo = QSysRoleResourcePo.sysRoleResourcePo;
            QSysResourcePo qSysResourcePo = QSysResourcePo.sysResourcePo;
            JPAQuery<Tuple> jpaQuery = jpa.select(qSysResourcePo, qSysRoleResourcePo.roleId)
                    .from(qSysResourcePo)
                    .leftJoin(qSysRoleResourcePo).on(qSysRoleResourcePo.resourceId.eq(qSysResourcePo.id)
                            .and(qSysRoleResourcePo.roleId.eq(qo.getRoleId())));
            BooleanExpression whereExpression = qSysResourcePo.type.eq(type.getCode())
                    .and(qSysResourcePo.group.notEqualsIgnoreCase(AuthGroup.S.getCode()));
            if (Objects.nonNull(authGroup)) {
                whereExpression = whereExpression.and(qSysResourcePo.group.eq(authGroup.getCode()));
            }
            jpaQuery.where(whereExpression);
            List<Tuple> jpaResultList = jpaQuery.fetch();
            List<SysResourceVo> result = jpaResultList.stream().map(tuple -> {
                SysResourceVo resourceVo = new SysResourceVo(tuple.get(qSysResourcePo));
                resourceVo.setChecked(Objects.nonNull(tuple.get(qSysRoleResourcePo.roleId)));
                return resourceVo;
            }).filter(sysResourceVo -> Objects.isNull(qo.getChecked())
                    || (Objects.nonNull(qo.getChecked()) && Objects.equals(qo.getChecked(), sysResourceVo.getChecked())
            )).collect(Collectors.toList());

            return Response.success(result);
        } catch (Exception e) {
            throw new UnexpectedStatusException(AuthStatus.ROLERESOURCE_FIND_ERROR, e, e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> findResourceByRoleIds(Collection<String> roleIds) {
        Set<Long> longRoleIds = roleIds.stream().map(Long::valueOf).collect(Collectors.toSet());
        QSysResourcePo sysResourcePo = QSysResourcePo.sysResourcePo;
        QSysRoleResourcePo sysRoleResourcePo = QSysRoleResourcePo.sysRoleResourcePo;
        return jpa.select(sysResourcePo.url)
                .from(sysResourcePo)
                .leftJoin(sysRoleResourcePo).on(sysResourcePo.id.eq(sysRoleResourcePo.resourceId))
                .where(sysRoleResourcePo.roleId.in(longRoleIds))
                .fetch();
    }

    ////////////////////////////
    //// private function
    ////////////////////////////

    private void saveOrUpdateRoleResource(SysRoleSuo suo) {
        try {
            if (Objects.nonNull(SysRole.fromCode(suo.getId()))) {
                return;
            }
            List<SysRoleResourcePo> list = parseToSysRoleResourcePo(suo);
            this.deleteRoleResourcesByRoleIdAndType(suo.getId(), suo.getType());
            if (!CollectionUtils.isEmpty(list)) {
                //有项目分配权限的系统角色，需要授权可分配的分析平台的项目资源权限
                this.handleProjectGrant(suo);
                sysRoleResourceDao.save(list);
            }
        } catch (Exception e) {
            throw new UnexpectedStatusException(AuthStatus.ROLERESOURCE_SAVE_ERROR, e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<SysRoleResourcePo> parseToSysRoleResourcePo(SysRoleSuo suo) {
        List<SysResourceVo> roleResourceNodeTree = suo.getResources();
        if (CollectionUtils.isEmpty(roleResourceNodeTree)) {
            return Collections.emptyList();
        }
        List<SysResourceVo> roleResourceNodeList = VoTreeBuilder.parseTreeList(roleResourceNodeTree);
        List<SysRoleResourcePo> sysRoleResourcePos = roleResourceNodeList.stream()
                .filter(SysResourceVo::getChecked).map(node -> {
                    SysRoleResourcePo sysRoleResourcePo = new SysRoleResourcePo();
                    sysRoleResourcePo.setRoleId(suo.getId());
                    sysRoleResourcePo.setResourceId(node.getId());
                    return sysRoleResourcePo;
                }).collect(Collectors.toList());
        return sysRoleResourcePos;
    }

    private void handleProjectGrant(SysRoleSuo suo) {
        //有项目分配权限的系统角色，需要授权可分配的分析平台的项目资源权限
        if (!Objects.equals(suo.getType(), SysType.GAP)) {
            return;
        }
        List<SysResourceVo> roleResourceNodeTree = suo.getResources();
        if (CollectionUtils.isEmpty(roleResourceNodeTree)) {
            return;
        }
        List<SysResourceVo> roleResourceNodeList = VoTreeBuilder.parseTreeList(roleResourceNodeTree);
        SysResourceVo addProjectAuthNode = roleResourceNodeList.stream().filter(roleResourceTreeVo ->
                !Objects.isNull(roleResourceTreeVo.getUrl()) && roleResourceTreeVo.getUrl().contains(Constants.ADD_PROJECT_AUTH_URL_PATH_REGEX)
        ).findFirst().orElse(null);
        if (Objects.isNull(addProjectAuthNode) || (!addProjectAuthNode.getChecked())) {
            return;
        }
        List<SysResourcePo> sysResourcePos = sysResourceDao.findByTypeAndGroup(SysType.GAP.getCode(), AuthGroup.P.getCode());
        if (CollectionUtils.isEmpty(sysResourcePos)) {
            return;
        }
        List<SysRoleResourcePo> sysRoleResourcePos = sysResourcePos.stream().filter(sysResourcePo ->
                StringUtils.isBlank(sysResourcePo.getSubType())).map(sysResourcePo -> {
            SysRoleResourcePo sysRoleResourcePo = new SysRoleResourcePo();
            sysRoleResourcePo.setRoleId(suo.getId());
            sysRoleResourcePo.setResourceId(sysResourcePo.getId());
            return sysRoleResourcePo;
        }).collect(Collectors.toList());
        sysRoleResourceDao.save(sysRoleResourcePos);
    }

    private void deleteRoleResourcesByRoleIdAndType(Long roleId, SysType type) {
        QSysRoleResourcePo qSysRoleResourcePo = QSysRoleResourcePo.sysRoleResourcePo;
        QSysResourcePo qSysResourcePo = QSysResourcePo.sysResourcePo;
        BooleanExpression deleteCondition = qSysResourcePo.type.eq(type.getCode());
        JPADeleteClause deleteClause = jpa.delete(qSysRoleResourcePo)
                .where(qSysRoleResourcePo.roleId.eq(roleId)
                        .and(qSysRoleResourcePo.resourceId.in(jpa.select(qSysResourcePo.id)
                                .from(qSysResourcePo).where(deleteCondition))));
        deleteClause.execute();
    }

    private boolean checkNameExists(Long roleId, String roleName) {
        List<SysRolePo> sysRolePos = sysRoleDao.findByName(roleName);
        if (CollectionUtils.isEmpty(sysRolePos)) {
            return false;
        }
        if (Objects.isNull(roleId)) {
            //insert role
            return !CollectionUtils.isEmpty(sysRolePos);
        }
        //modify role
        if ((sysRolePos.size()) == 1) {
            SysRolePo rolePo = sysRolePos.get(0);
            //已存在的角色信息Id与待保存的角色信息Id不一致
            if (!Objects.equals(rolePo.getId(), roleId)) {
                return true;
            }
        }
        if (sysRolePos.size() > 1) {
            return true;
        }
        return false;
    }

    private boolean checkCodeExists(Long roleId, String roleCode) {
        if (Strings.isEmpty(roleCode)) {
            return false;
        }
        List<SysRolePo> sysRolePos = sysRoleDao.findByCode(roleCode);
        if (CollectionUtils.isEmpty(sysRolePos)) {
            return false;
        }
        if (Objects.isNull(roleId)) {
            //insert role
            return !CollectionUtils.isEmpty(sysRolePos);
        }
        //modify role
        if ((sysRolePos.size()) == 1) {
            SysRolePo rolePo = sysRolePos.get(0);
            //已存在的角色信息Id与待保存的角色信息Id不一致
            if (!Objects.equals(rolePo.getId(), roleId)) {
                return true;
            }
        }
        if (sysRolePos.size() > 1) {
            return true;
        }
        return false;
    }

    private void handleUserRoleWhenDeleteRole(Long deleteRoleId) {
        QSysUserRolePo qSysUserRolePo = QSysUserRolePo.sysUserRolePo;
        List<Tuple> tuples = jpa.select(
                qSysUserRolePo.id.count(),
                qSysUserRolePo.userId
        ).from(qSysUserRolePo)
                .where(qSysUserRolePo.userId.in(jpa.select(qSysUserRolePo.userId).from(qSysUserRolePo).where(qSysUserRolePo.roleId.eq(deleteRoleId))))
                .groupBy(qSysUserRolePo.userId).fetch();
        if (CollectionUtils.isEmpty(tuples)) {
            return;
        }
        List<SysUserRolePo> singleRoleUserIds = tuples.stream().map(tuple -> {
            Long roleCount = tuple.get(qSysUserRolePo.id.count());
            Long userId = tuple.get(qSysUserRolePo.userId);
            if (1 != roleCount) {
                return null;
            }
            SysUserRolePo sysUserRolePo = new SysUserRolePo();
            sysUserRolePo.setUserId(userId);
            sysUserRolePo.setRoleId(SysRole.GENERAL_USER.getCode());
            return sysUserRolePo;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        sysUserRoleDao.deleteByRoleId(deleteRoleId);
        if (!CollectionUtils.isEmpty(singleRoleUserIds)) {
            sysUserRoleDao.save(singleRoleUserIds);
        }
    }

}
