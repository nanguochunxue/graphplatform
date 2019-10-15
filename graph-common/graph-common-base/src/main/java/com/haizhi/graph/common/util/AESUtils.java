package com.haizhi.graph.common.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


/**
 * Created by liulu on 2019/7/22.
 */
public class AESUtils {

    private static final String KEY = "hzAtlasProdution";
    private static final String IV = "hzAtlasProdution";
    private static final String ENCODE  = "UTF-8";
    private static final String ALGORITHM = "AES";

    //参数分别代表 算法名称/加密模式/数据填充方式
    private static final String ALGORITHMSTR = "AES/CBC/NoPadding";

    public static String encrypt(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
        kgen.init(128);
        String ivString = IV;
        byte[] iv = ivString.getBytes(ENCODE);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), ALGORITHM),ivSpec);
        int blockSize = cipher.getBlockSize();
        byte[] dataBytes = content.getBytes(ENCODE);
        int length = dataBytes.length;
        //计算需填充长度
        if (length % blockSize != 0) {
            length = length + (blockSize - (length % blockSize));
        }
        byte[] plaintext = new byte[length];
        //填充
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
        byte[] b = cipher.doFinal(plaintext);
        // 采用base64算法进行转码,避免出现中文乱码
        return Base64.getEncoder().encodeToString(b);

    }

    public static String decrypt(String encryptStr, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
        kgen.init(128);
        String ivString = IV;
        byte[] iv = ivString.getBytes(ENCODE);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), ALGORITHM),ivSpec);
        // 采用base64算法进行转码,避免出现中文乱码
        byte[] encryptBytes = Base64.getDecoder().decode(encryptStr);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return byteToStr(decryptBytes);
    }

    private static String byteToStr(byte[] buffer) {
        try {
            int length = 0;
            for (int i = 0; i < buffer.length; ++i) {
                if (buffer[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(buffer, 0, length, ENCODE);
        } catch (Exception e) {
            return "";
        }
    }


    public static String encrypt(String content)  throws Exception {
        return encrypt(content, KEY);
    }
    public static String decrypt(String encryptStr) throws Exception {
        return decrypt(encryptStr, KEY);
    }
}
