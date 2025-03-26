package com.devops.util;


import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;

/**
 * AES对称加密算法,组件类
 *
 * @author yux
 */
public class AESUtil {

    /**
     * 服务器加密密钥
     */
    public static final String SERVER_AES_KEY = "4P6ZAoKammX6IhZUd+w8vw==";
    public static final String ALGORITHM = "AES";
    public static final int KEY_SIZE = 128;

    /**
     * 转换密钥
     *
     * @param key 二进制密钥
     * @return Key 密钥
     */
    private static Key toKey(byte[] key) {
        // 实例化AES密钥材料
        return new SecretKeySpec(key, ALGORITHM);
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 还原密钥
        Key k = toKey(key);
        // 实例化
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 初始化,设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, k);
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @param iv   初始化向量
     * @return 解密数据
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        // 还原密钥
        Key k = toKey(key);
        // 实例化
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 初始化,设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, k, new SecureRandom(iv));
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static byte[] decrypt(byte[] data, String key) throws Exception {
        return decrypt(data, getKey(key));
    }

    /**
     * 解密
     *
     * @param base64data 待解密BASE64编码数据
     * @param key        密钥
     * @return byte[] 解密数据
     */
    public static byte[] decrypt(String base64data, String key, String iv) throws Exception {
        return decrypt(Base64.decodeBase64(base64data), getKey(key), iv.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 解密
     *
     * @param base64data 待解密BASE64编码数据
     * @param key        密钥
     * @return byte[] 解密数据
     */
    public static byte[] decrypt(String base64data, String key) throws Exception {
        return decrypt(Base64.decodeBase64(base64data), getKey(key));
    }

    /**
     * 解密
     *
     * @param base64data 待解密BASE64编码数据
     * @param key        密钥
     * @return 解密数据
     */
    public static String decryptString(String base64data, String key) throws Exception {
        return new String(decrypt(base64data, key));
    }

    /**
     * 解密
     *
     * @param base64data 待解密BASE64编码数据
     * @param key        密钥
     * @param iv         初始化向量
     * @return 解密数据
     */
    public static String decryptString(String base64data, String key, String iv) throws Exception {
        return new String(decrypt(base64data, key));
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return string 加密数据
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        return Base64.encodeBase64String(encrypt(data.getBytes(StandardCharsets.UTF_8), getKey(key), iv.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] randomKey) throws Exception {
        // 还原密钥
        Key k = toKey(key);
        // 实例化
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 初始化,设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k, new SecureRandom(randomKey));
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 还原密钥
        Key k = toKey(key);
        // 实例化
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 初始化,设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k);
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     */
    public static byte[] encrypt(byte[] data, String key) throws Exception {
        return encrypt(data, getKey(key));
    }


    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return string 加密数据
     */
    public static String encrypt(String data, String key) throws Exception {
        return Base64.encodeBase64String(encrypt(data.getBytes(StandardCharsets.UTF_8), getKey(key)));
    }


    /**
     * 生成密钥
     *
     * @return byte[] 二进制密钥
     */
    public static byte[] initKey() throws Exception {
        // 实例化
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
        // 初始化密钥长度
        kg.init(KEY_SIZE);
        // 生成秘密密钥
        SecretKey secretKey = kg.generateKey();
        // 获得密钥的二进制编码形式
        return secretKey.getEncoded();
    }

    /**
     * 初始化密钥
     *
     * @return String Base64编码密钥
     */
    public static String initKeyString() throws Exception {
        return Base64.encodeBase64String(initKey());
    }


    /**
     * 获取密钥
     *
     * @param key Base64编码密钥
     * @return byte[] 密钥
     */
    public static byte[] getKey(String key) {
        return Base64.decodeBase64(key);
    }

}
