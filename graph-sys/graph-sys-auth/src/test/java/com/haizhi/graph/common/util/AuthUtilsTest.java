package com.haizhi.graph.common.util;

import com.haizhi.graph.sys.auth.util.AuthUtils;
import org.junit.Test;

/**
 * Created by tanghaiyang on 2019/1/7.
 */
public class AuthUtilsTest {

    @Test
    public void generatePassword(){
        String psw = "72TFIa9w8fcO";
        String password = AuthUtils.getEncryptedPwd(psw);
        System.out.println(password);

    }
}
