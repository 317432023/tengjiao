package com.tengjiao.tool.third.test.security.crypto;


import com.tengjiao.tool.third.security.crypto.AesCoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * AES安全编码组件校验
 *
 * @version 1.0
 */
public class AesCoderTest {

	/**
	 * 测试
	 *
	 * @throws Exception
	 */
	@Test
	public final void test() throws Exception {
		String inputStr = "AES_CBC加密    123456789 abcdef  By:hmlyn";
		byte[] inputData = inputStr.getBytes("gbk");
		System.err.println("原文:\t" + inputStr);

		// 密钥，256位，32字节
		//byte[] key = AesCoder.initKey();
		byte[] key = "31323334353637383132333435363738".getBytes();//

		// 偏移量(固定为16字节，即128位)
		//byte[] iv = null;//new byte[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		//byte[] iv = "3838383838383838".getBytes();

		System.err.println("Base64String(密钥):\t" + Base64.encodeBase64String(key));
		System.err.println("HexString(密钥):\t" + Hex.encodeHexString(key));

		// 加密
		inputData = AesCoder.encrypt(AesCoder.AES_ECB_PKCS5Padding, inputData, key/*, iv*/);

		System.err.println("Base64String(加密结果):\t" + Base64.encodeBase64String(inputData));
		System.err.println("HexString(加密结果):\t" + Hex.encodeHexString(inputData));

		// 解密
		byte[] outputData = AesCoder.decrypt(AesCoder.AES_ECB_PKCS5Padding, inputData, key/*, iv*/);

		String outputStr = new String(outputData,"gbk");
		System.err.println("解密后原文:\t" + outputStr);

		// 校验
		assertEquals(inputStr, outputStr);
	}
	@Test
	public final void testDev() throws Exception {
	    byte[] data  = /*"2018112901;01234567890123456789012345678901;1"*/"伟大光荣正确的党，掌握宇宙真理的党".getBytes("UTF8");
	    byte[] key = "4B35CFB9092DFD6F3DC23B0DE026FFA1".getBytes();
	    byte[] iv = "abcdefghijklmnop".getBytes();/*new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};*/
	    byte[] output = AesCoder.encrypt(AesCoder.AES_CBC_PKCS5Padding, data, key, iv);
	    String encoded = Base64.encodeBase64String(output);
	    System.out.println(encoded);
	    data = AesCoder.decrypt(AesCoder.AES_CBC_PKCS5Padding, output, key, iv);
	    System.out.println(new String(data, "UTF8"));

	    //key="581c34114b6d4c2b845b63531e3ffd19".getBytes();
	    data = "{\"playerId\":\"55\",\"chatRoomId\":\"17\",\"msgContent\":\"历史\"}".getBytes("UTF-8");
	    iv = "0000000000000000".getBytes();
			output = AesCoder.encrypt(AesCoder.AES_CBC_PKCS5Padding, data, key, iv);
			encoded = Base64.encodeBase64String(output);
			System.out.println(encoded);

		output = Base64.decodeBase64(encoded);
		data = AesCoder.decrypt(AesCoder.AES_CBC_PKCS5Padding, output, key, iv);
		System.out.println(new String(data, "UTF8"));


	}
	@Test
	public final void testPKCS7Padding_BC() throws Exception {
		byte[] data  = /*"2018112901;01234567890123456789012345678901;1"*/"伟大光荣正确的党，掌握宇宙真理的党".getBytes("UTF8");
		byte[] key = "4B35CFB9092DFD6F3DC23B0DE026FFA1".getBytes();
		byte[] iv = "abcdefghijklmnop".getBytes();/*new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};*/
		byte[] output = AesCoder.encrypt(AesCoder.AES_CBC_PKCS7Padding, data, key, iv);
		String encoded = Base64.encodeBase64String(output);
		System.out.println(encoded);
		data = AesCoder.decrypt(AesCoder.AES_CBC_PKCS7Padding, output, key, iv);
		System.out.println(new String(data, "UTF8"));

		//key="581c34114b6d4c2b845b63531e3ffd19".getBytes();
		data = "{\"playerId\":\"55\",\"chatRoomId\":\"17\",\"msgContent\":\"历史\"}".getBytes("UTF-8");
		iv = "0000000000000000".getBytes();
		output = AesCoder.encrypt(AesCoder.AES_CBC_PKCS7Padding, data, key, iv);
		encoded = Base64.encodeBase64String(output);
		System.out.println(encoded);

		output = Base64.decodeBase64(encoded);
		data = AesCoder.decrypt(AesCoder.AES_CBC_PKCS7Padding, output, key, iv);
		System.out.println(new String(data, "UTF8"));


	}

}
