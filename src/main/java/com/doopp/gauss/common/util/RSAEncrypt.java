package com.doopp.gauss.common.util;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RSAEncrypt {

    // private final static Logger logger = LoggerFactory.getLogger(RSAEncrypt.class);

    /**
     * 私钥
     */
    private RSAPrivateKey privateKey;

    /**
     * 公钥
     */
    private RSAPublicKey publicKey;

    /**
     * 获取私钥
     */
    public RSAPrivateKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * 获取公钥
     */
    public RSAPublicKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * 随机生成密钥对
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen= null;
        try {
            keyPairGen=KeyPairGenerator.getInstance("RSA");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Map<String, Object> keyMap = new HashMap<>();
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair=keyPairGen.generateKeyPair();

        // private key
        PrivateKey privateKey = keyPair.getPrivate();
        keyMap.put("privateKey", Base64.getEncoder().encodeToString(privateKey.getEncoded()));

        // public key
        PublicKey publicKey = keyPair.getPublic();
        keyMap.put("publicKey", Base64.getEncoder().encodeToString(publicKey.getEncoded()));

        return keyMap;
    }

    /**
     * 私钥加密
     *
     * @param privateKey 私钥
     * @param plainTextData 待加密的源数据
     * @return byte[]
     * @throws Exception 异常
     */
    public static byte[] encryptByPrivateKey(String privateKey, byte[] plainTextData) throws Exception {
        RSAEncrypt rsaEncrypt = new RSAEncrypt();
        rsaEncrypt.loadPrivateKey(privateKey);
        return rsaEncrypt.encrypt(rsaEncrypt.getPrivateKey(), plainTextData);
    }
    /**
     * 公钥解密
     *
     * @param publicKey 公钥
     * @param cipherData 加密的数据
     * @return byte[]
     * @throws Exception 异常
     */
    public static byte[] decryptByPublicKey(String publicKey, byte[] cipherData) throws Exception {
        RSAEncrypt rsaEncrypt = new RSAEncrypt();
        rsaEncrypt.loadPublicKey(publicKey);
        return rsaEncrypt.decrypt(rsaEncrypt.getPublicKey(), cipherData);
    }
    /**
     * 公钥加密
     *
     * @param publicKey 公钥
     * @param plainTextData 待加密的源数据
     * @return byte[]
     * @throws Exception 异常
     */
    public static byte[] encryptByPublicKey(String publicKey, byte[] plainTextData) throws Exception {
        RSAEncrypt rsaEncrypt = new RSAEncrypt();
        rsaEncrypt.loadPublicKey(publicKey);
        return rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), plainTextData);
    }
    /**
     * 私钥解密
     *
     * @param privateKey 私钥
     * @param cipherData 加密的数据
     * @return byte[]
     * @throws Exception 异常
     */
    public static byte[] decryptByPrivateKey(String privateKey, byte[] cipherData) throws Exception {
        RSAEncrypt rsaEncrypt = new RSAEncrypt();
        rsaEncrypt.loadPrivateKey(privateKey);
        return rsaEncrypt.decrypt(rsaEncrypt.getPrivateKey(), cipherData);
    }

    /**
     * 从文件中输入流中加载公钥
     *
     * @param in 公钥输入流
     * @throws Exception 加载公钥时产生的异常
     */
    public void loadPublicKey(InputStream in) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) != '-') {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            loadPublicKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }


    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public void loadPublicKey(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.getDecoder().decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        }
        catch (NoSuchAlgorithmException e) {
            throw new Exception("No Such Algorithm");
        }
        catch (InvalidKeySpecException e) {
            throw new Exception("Invalid Key Spec");
        }
        catch (NullPointerException e) {
            throw new Exception("Null Pointer");
        }
    }

    /**
     * 从文件中加载私钥
     *
     * @param in 私钥文件名
     */
    public void loadPrivateKey(InputStream in) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) != '-') {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            this.loadPrivateKey(sb.toString());
        }
        catch (IOException e) {
            throw new Exception("io error");
        }
        catch (NullPointerException e) {
            throw new Exception("Null Pointer");
        }
    }

    public void loadPrivateKey(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        }
        catch (NoSuchAlgorithmException e) {
            throw new Exception("No Such Algorithm");
        }
        catch (InvalidKeySpecException e) {
            throw new Exception("Invalid Key Spec");
        }
        catch (NullPointerException e) {
            throw new Exception("Null Pointer");
        }
    }

    /**
     * 加密过程
     *
     * @param privateKey    私钥
     * @param plainTextData 明文数据
     * @return byte[]
     * @throws Exception 加密过程中的异常信息
     */
    private byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws Exception {
        if (privateKey == null) {
            throw new Exception("private key can not null");
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            return cipher.doFinal(plainTextData);
        }
        catch (NoSuchAlgorithmException e) {
            throw new Exception("No Such Algorithm");
        }
        catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }
        catch (InvalidKeyException e) {
            throw new Exception("invalid key");
        }
        catch (IllegalBlockSizeException e) {
            throw new Exception("illegal block size");
        }
        catch (BadPaddingException e) {
            throw new Exception("bad padding");
        }
    }

    /**
     * 加密过程
     *
     * @param publicKey    私钥
     * @param plainTextData 明文数据
     * @return byte[]
     * @throws Exception 加密过程中的异常信息
     */
    public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plainTextData);
        }
        catch (NoSuchAlgorithmException e) {
            throw new Exception("No Such Algorithm");
        }
        catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }
        catch (InvalidKeyException e) {
            throw new Exception("Invalid Key");
        }
        catch (IllegalBlockSizeException e) {
            throw new Exception("Illegal Block Size");
        }
        catch (BadPaddingException e) {
            throw new Exception("Bad Padding");
        }
    }

    /**
     * 解密过程
     *
     * @param publicKey 私钥
     * @param cipherData 密文数据
     * @throws Exception 解密过程中的异常信息
     * @return 明文
     */
    public byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception {
        if (publicKey == null) {
            throw new Exception("解密公钥为空, 请设置");
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(cipherData);
        }
        catch (NoSuchAlgorithmException e) {
            throw new Exception("No Such Algorithm");
        }
        catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }
        catch (InvalidKeyException e) {
            throw new Exception("Invalid Key");
        }
        catch (IllegalBlockSizeException e) {
            throw new Exception("Illegal Block Size");
        }
        catch (BadPaddingException e) {
            throw new Exception("Bad Padding");
        }
    }



    /**
     * 解密过程
     *
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @throws Exception 解密过程中的异常信息
     * @return 明文
     */
    public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception {
        if (privateKey == null) {
            throw new Exception("解密私钥为空, 请设置");
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(cipherData);
        }
        catch (NoSuchAlgorithmException e) {
            throw new Exception("No SuchAlgorithm");
        }
        catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }
        catch (InvalidKeyException e) {
            throw new Exception("Invalid Key");
        }
        catch (IllegalBlockSizeException e) {
            throw new Exception("Illegal Block Size");
        }
        catch (BadPaddingException e) {
            throw new Exception("Bad Padding");
        }
    }
}