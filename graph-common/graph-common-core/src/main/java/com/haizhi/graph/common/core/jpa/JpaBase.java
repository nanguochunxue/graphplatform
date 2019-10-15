package com.haizhi.graph.common.core.jpa;

import com.haizhi.graph.common.core.login.utils.ShiroUtils;
import com.haizhi.graph.common.model.BasePo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.NoRepositoryBean;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Objects;

/**
 * Created by chengmo on 2018/12/25.
 */
@NoRepositoryBean
public class JpaBase {

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    protected JPAQueryFactory jpa;

    @PostConstruct
    public void initFactory() {
        jpa = new JPAQueryFactory(entityManager);
    }

    protected <T extends BasePo> void setUpdateBy(T bean) {
        String userId = String.valueOf(ShiroUtils.getUserID());
        if (Objects.isNull(bean.getId()) || StringUtils.isBlank(bean.getCreatedById())) {
            bean.setCreatedById(userId);
        }
        bean.setUpdateById(userId);
    }
}
