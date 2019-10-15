package com.haizhi.graph.sys.auth.service.impl;

import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.sys.auth.constant.AuthStatus;
import com.haizhi.graph.sys.auth.dao.SysUserDao;
import com.haizhi.graph.sys.auth.model.po.QSysRolePo;
import com.haizhi.graph.sys.auth.model.po.QSysUserPo;
import com.haizhi.graph.sys.auth.model.po.QSysUserRolePo;
import com.haizhi.graph.sys.auth.model.po.SysUserPo;
import com.haizhi.graph.sys.auth.model.qo.SysUserPageQo;
import com.haizhi.graph.sys.auth.model.suo.SysUserSuo;
import com.haizhi.graph.sys.auth.model.vo.SysUserSimpleVo;
import com.haizhi.graph.sys.auth.service.DemoService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by chenmo on 2018/1/4.
 */
@Service
public class DemoServiceImpl extends JpaBase implements DemoService {

    @Autowired
    private SysUserDao sysUserDao;

    @Override
    public List<SysUserSimpleVo> find(SysUserPageQo qo) {
        QSysUserPo userPo = QSysUserPo.sysUserPo;
        List<SysUserPo> results = jpa.selectFrom(userPo)
                .where(userPo.userNo.like(qo.getUserNo()))
                .orderBy(userPo.createdDt.desc())
                .fetch();
        List<SysUserSimpleVo> rows = new ArrayList<>();
        for (SysUserPo po : results) {
            rows.add(new SysUserSimpleVo(po));
        }
        return rows;
    }

    @Override
    public PageResponse findPage(SysUserPageQo qo) {
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.isNotBlank(qo.getUserNo())){
            builder.and(QSysUserPo.sysUserPo.userNo.like(qo.getUserNo()));
        }
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "updatedDt"));
        PageRequest pageRequest = new PageRequest(0, 10, sort);
        Page<SysUserPo> page = sysUserDao.findAll(builder, pageRequest);
        return PageResponse.success(page.getContent(), page.getTotalPages(), qo.getPage());
    }

    @Override
    public Response<SysUserSimpleVo> findUserRoles(SysUserPageQo qo) {
        /*
        * select t.*,r.id, r.name from sys_user t
            left join `sys_user_role` ur on ur.`user_id`= t.id
            left join `sys_role` r on r.id = ur.role_id
        * */
        QSysUserPo userPo = QSysUserPo.sysUserPo;
        QSysRolePo rolePo = QSysRolePo.sysRolePo;
        QSysUserRolePo userRolePo = QSysUserRolePo.sysUserRolePo;
        JPAQuery<Tuple> jpaQuery = jpa.select(userPo, rolePo)
                .from(userPo)
                .leftJoin(userRolePo).on(userRolePo.userId.eq(userPo.id))
                .leftJoin(rolePo).on(userRolePo.roleId.eq(rolePo.id));
        Predicate expression = QSysUserPo.sysUserPo.userNo.like(qo.getUserNo());
        jpaQuery.where(expression);
        List<Tuple> tupleList = jpaQuery.fetch();
        List<SysUserSimpleVo> rows = new ArrayList<>();
        for (Tuple row : tupleList) {
            /*SysUserPo userPo0 = row.get(userPo);
            SysUserPo userPo1 = row.get(0, SysUserPo.class);
            SysRolePo rolePo1 = row.get(1, SysRolePo.class);
            System.out.println(JSON.toJSONString(userPo0));
            System.out.println(JSON.toJSONString(userPo1));
            System.out.println(JSON.toJSONString(rolePo1));*/
            SysUserPo po = row.get(userPo);
            if (Objects.nonNull(po)){
                rows.add(new SysUserSimpleVo(po));
            }
        }
        return Response.success(rows);
    }

    @Override
    public PageResponse<SysUserSimpleVo> findPageUserRoles(SysUserPageQo qo) {
        /*
        * select t.*,r.id, r.name from sys_user t
            left join `sys_user_role` ur on ur.`user_id`= t.id
            left join `sys_role` r on r.id = ur.role_id
        * */
        QSysUserPo userPo = QSysUserPo.sysUserPo;
        QSysRolePo rolePo = QSysRolePo.sysRolePo;
        QSysUserRolePo userRolePo = QSysUserRolePo.sysUserRolePo;
        JPAQuery<Tuple> jpaQuery = jpa.select(userPo, rolePo)
                .from(userPo)
                .leftJoin(userRolePo).on(userRolePo.userId.eq(userPo.id))
                .leftJoin(rolePo).on(userRolePo.roleId.eq(rolePo.id));
        Predicate expression = QSysUserPo.sysUserPo.userNo.like(qo.getUserNo());
        jpaQuery.where(expression);
        QueryResults<Tuple> results = jpaQuery.fetchResults();
        List<SysUserSimpleVo> rows = new ArrayList<>();
        for (Tuple row : results.getResults()) {
            /*SysUserPo userPo0 = row.get(userPo);
            SysUserPo userPo1 = row.get(0, SysUserPo.class);
            SysRolePo rolePo1 = row.get(1, SysRolePo.class);
            System.out.println(JSON.toJSONString(userPo0));
            System.out.println(JSON.toJSONString(userPo1));
            System.out.println(JSON.toJSONString(rolePo1));*/
            SysUserPo po = row.get(userPo);
            if (Objects.nonNull(po)){
                rows.add(new SysUserSimpleVo(po));
            }
        }
        return PageResponse.success(rows, results.getTotal(), qo.getPage());
    }

    @Override
    public Response saveOrUpdate(SysUserSuo suo) {
        try {
            SysUserPo po = JSONUtils.copy(suo, SysUserPo.class);
            this.setUpdateBy(po);
            sysUserDao.save(po);
        } catch (Exception e) {
            throw new UnexpectedStatusException(AuthStatus.USER_SAVE_ERROR, e);
        }
        return Response.success();
    }

    @Override
    public Response delete(Long userId) {
        try {
            sysUserDao.delete(userId);
        } catch (Exception e) {
            throw new UnexpectedStatusException(AuthStatus.USER_DELETE_ERROR, e);
        }
        return Response.success();
    }
}
