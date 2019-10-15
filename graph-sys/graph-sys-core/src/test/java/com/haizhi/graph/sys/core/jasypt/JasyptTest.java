package com.haizhi.graph.sys.core.jasypt;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;

/**
 * Created by chengmo on 2019/6/11.
 */
public class JasyptTest {

    @Test
    public void encryptAndDecrypt() {
        String encryptPwd = "Haizhi@2018";

        // encrypt
        BasicTextEncryptor bte = new BasicTextEncryptor();
        bte.setPassword(encryptPwd);
        String newPassword = bte.encrypt("Haizhi@2018");
        System.out.println("newPassword: " + newPassword);

        // decrypt
        BasicTextEncryptor bte2 = new BasicTextEncryptor();
        bte2.setPassword(encryptPwd);
        String oldPassword = bte2.decrypt(newPassword);
        System.out.println("oldPassword: " + oldPassword);
    }
}
