package com.tengjiao.tool.third.security.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * AES安全编码组件
 * @author kangtengjiao
 */
public final class AesCoder {

  /**
   * 密钥算法
   */
  private static final String KEY_ALGORITHM = "AES";

  /**
   * eg. "AES/CBC/PKCS5Padding"
   * <p>
   * 加密解密算法 / 工作模式ECB,CBC,CFB,OFB / 填充方式<br/>
   * Java 6支持PKCS5Padding填充方式 <br/>
   * BouncyCastle支持PKCS7Padding填充方式
   */
  public static final String AES_ECB_PKCS5Padding = "AES/ECB/PKCS5Padding";
  public static final String AES_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding";
  public static final String AES_CBC_PKCS7Padding = "AES/CBC/PKCS7Padding";
  public static final String AES_CFB_PKCS7Padding = "AES/CFB/PKCS7Padding";

  static {
    // 注册PKCS7Padding支持，在Cipher.getInstance(CIPHER_ALGORITHM, "BC")时避免找不到支持库
    Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * 转换密钥
   *
   * @param key 二进制密钥
   * @return Key 密钥
   * @throws Exception
   */
  private static Key toKey(byte[] key) {

    // 实例化AES密钥材料
    SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
    return secretKey;
  }
  /**
   * 转换密钥
   *
   * @param key 字符串密钥
   * @return Key 密钥
   * @throws Exception
   */
  private static Key toKey(String key) {

    // 实例化AES密钥材料
    SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
    return secretKey;
  }
  private static byte[] codec(final String cipherAlgorithm, byte[] data, byte[] key, byte[] initVector, final int MODE) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
    IvParameterSpec iv = (initVector == null ? null : new IvParameterSpec(initVector));

    // 还原密钥
    Key k = toKey(key);

    /**
     * 实例化 使用PKCS7Padding填充方式，按如下方式实现 Cipher.getInstance(CIPHER_ALGORITHM, "BC");
     */
    Cipher cipher = cipherAlgorithm.contains("/PKCS7Padding") ? Cipher.getInstance(cipherAlgorithm, "BC") : Cipher.getInstance(cipherAlgorithm);

    if (cipher.getAlgorithm().contains("/CBC/") && initVector == null) {
      throw new RuntimeException("CBC工作模式需要指定偏移向量");
    }

    // 初始化
    if (initVector == null) {
      cipher.init(MODE, k);
    } else {
      cipher.init(MODE, k, iv);
    }

    // 执行操作
    return cipher.doFinal(data);
  }

  /**
   * 解密（带偏移向量）
   *
   * @param cipherAlgorithm 算法工作模式与填充方式
   * @param data            待解密数据
   * @param key             密钥
   * @param initVector      偏移向量
   * @return byte[] 解密数据
   * @throws NoSuchPaddingException
   * @throws NoSuchProviderException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws InvalidAlgorithmParameterException
   * @throws Exception
   */
  public static byte[] decrypt(final String cipherAlgorithm, byte[] data, byte[] key, byte[] initVector) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
    return codec(cipherAlgorithm, data, key, initVector, Cipher.DECRYPT_MODE);
  }

  /**
   * 解密（无偏移向量）
   *
   * @param cipherAlgorithm 算法工作模式与填充方式
   * @param data            待解密数据
   * @param key             密钥
   * @return byte[]         解密结果数据
   * @throws InvalidAlgorithmParameterException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws NoSuchPaddingException
   * @throws NoSuchProviderException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws Exception
   */
  public static byte[] decrypt(final String cipherAlgorithm, byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
    return codec(cipherAlgorithm, data, key, null, Cipher.DECRYPT_MODE);
  }

  /**
   * 加密（带偏移向量）
   *
   * @param cipherAlgorithm 算法工作模式与填充方式
   * @param data            待加密数据
   * @param key             密钥
   * @param initVector      偏移向量
   * @return byte[]         加密结果数据
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws NoSuchPaddingException
   * @throws NoSuchProviderException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws InvalidAlgorithmParameterException
   * @throws Exception
   */
  public static byte[] encrypt(final String cipherAlgorithm, byte[] data, byte[] key, byte[] initVector) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
    return codec(cipherAlgorithm, data, key, initVector, Cipher.ENCRYPT_MODE);
  }

  /**
   * 加密（无偏移向量）
   *
   * @param cipherAlgorithm 算法工作模式与填充方式
   * @param data            待加密数据
   * @param key             密钥
   * @return byte[]         加密结果数据
   * @throws InvalidAlgorithmParameterException
   * @throws NoSuchPaddingException
   * @throws NoSuchProviderException
   * @throws NoSuchAlgorithmException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws InvalidKeyException
   * @throws Exception
   */
  public static byte[] encrypt(final String cipherAlgorithm, byte[] data, byte[] key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException {
    return codec(cipherAlgorithm, data, key, null, Cipher.ENCRYPT_MODE);
  }

  /**
   * 生成密钥 <br>
   *
   * @return byte[] 二进制密钥
   * @throws NoSuchAlgorithmException
   * @throws Exception
   */
  public static byte[] initKey() throws NoSuchAlgorithmException {

    // 实例化
    KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);

    /*
     * AES 要求密钥长度为 128位、192位或 256位
     */
    kg.init(256);

    // 生成秘密密钥
    SecretKey secretKey = kg.generateKey();

    // 获得密钥的二进制编码形式
    return secretKey.getEncoded();
  }

}
