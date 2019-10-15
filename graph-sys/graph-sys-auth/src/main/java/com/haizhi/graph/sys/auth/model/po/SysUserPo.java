package com.haizhi.graph.sys.auth.model.po;

import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.model.BasePo;
import com.haizhi.graph.common.util.AESUtils;
import com.haizhi.graph.sys.auth.constant.AuthStatus;
import com.haizhi.graph.sys.auth.constant.UserSource;
import com.haizhi.graph.sys.auth.model.suo.SysUserSuo;
import com.haizhi.graph.sys.auth.util.AuthUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "sys_user")
public class SysUserPo extends BasePo {

    @Column(name = "user_no")
    private String userNo;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "user_source")
    private String userSource;

    @Column(name = "status")
    private int status;

    public void update(SysUserSuo suo, boolean isAdd) {
        if (isAdd) {
            this.userNo = suo.getUserNo();
            this.name = suo.getName();
            this.phone = suo.getPhone();
            this.email = suo.getEmail();
            this.status = Constants.AVAILABLE;
            this.userSource = UserSource.SYS_CREATION.getCode();
            String password = suo.getPassword();
            if (StringUtils.isBlank(password)){
                password = com.haizhi.graph.common.core.login.constant.Constants.DEFAULT_USER_PWD;
                this.password = AuthUtils.getEncryptedPwd(password);
            } else {
                try {
                    this.password = AuthUtils.getEncryptedPwd(AESUtils.decrypt(password));
                } catch (Exception e) {
                    throw new UnexpectedStatusException(AuthStatus.INVALID_PASSWORD_ERROR);
                }
            }

        } else {
            this.id = suo.getId();
            this.userNo = suo.getUserNo();
            this.name = suo.getName();
            this.phone = suo.getPhone();
            this.email = suo.getEmail();
            String password = suo.getPassword();
            if (!StringUtils.isBlank(password)){
                try {
                    this.password = AuthUtils.getEncryptedPwd(AESUtils.decrypt(password));
                } catch (Exception e) {
                    throw new UnexpectedStatusException(AuthStatus.INVALID_PASSWORD_ERROR);
                }
            };
        }
    }

}
