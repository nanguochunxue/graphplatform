package com.haizhi.graph.sys.auth.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Sha1Hash;
import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * Created by chengmo on 18/2/2.
 */
public class AuthUtils {

    private static final int DEFAULT_SALT_LEN = 8;
    private static final int DEFAULT_HASH_ITERATIONS = 1000;
    private static final String SEPARATOR = "$";
    private static final String SALT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final String DEFAULT_PWD = "123456";

    public static String getEncryptedPwd(String password) {
        return getEncryptedPwd(password, randomSalt(), DEFAULT_HASH_ITERATIONS);
    }

    public static boolean validPassword(String pwd, String encryptedPwd) {
        String[] arr = StringUtils.splitPreserveAllTokens(encryptedPwd, SEPARATOR);
        String str = getEncryptedPwd(pwd, arr[2], Integer.parseInt(arr[1]));
        return encryptedPwd.equals(str);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private static String getEncryptedPwd(String password, String salt, int hashIterations) {
        SimpleHash builder = new Sha1Hash(password, salt, hashIterations);
        StringBuilder sb = new StringBuilder();
        sb.append(builder.getAlgorithmName());
        sb.append(SEPARATOR);
        sb.append(DEFAULT_HASH_ITERATIONS);
        sb.append(SEPARATOR);
        sb.append(salt);
        sb.append(SEPARATOR);
        sb.append(builder.toString());
        return sb.toString();
    }

    private static String randomSalt() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DEFAULT_SALT_LEN; i++) {
            Long random = Math.round(Math.random() * SALT_CHARS.length());
            int index = random.intValue();
            sb.append(SALT_CHARS.charAt(index == SALT_CHARS.length() ? index - 1 : index));
        }
        return sb.toString();
    }
}