/**   
 * <p>本文件仅为内部使用，请勿外传</p>
 */
package com.ebrain.api.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.ebrain.api.exceptions.BaseException;

/** 
 * <p>
 * Description: 
 * AES的长度必须为16，即16*8=128，
 * 如果要把密钥定为大于128时（即192或256）时，就会出现这个错误：Illegal key size or default parameters 
 * 这是因为Java默认不能处理这么长的key。
 * 解决办法：下载JCE，把解压后的local_policy.jar文件和US_export_policy.jar放到jre的安全目录下
 * </p>
 * 时间: 2017年6月5日 下午5:24:45
 *
 * @author peisong
 * @since v1.4.2 
 */
public class AESUtils {
	//初始化AES向量
	private final static byte[] iv="EBRAIN_COM_AES_IV".getBytes();
	private final static String algorithm="AES/CBC/PKCS5PADDING";// "算法/模式/补码方式"
	
	public static byte[] decrypt(byte[] key,byte[] aesCipher) throws BaseException{
		if(key.length!=16&&key.length!=24&&key.length!=32){
			throw new BaseException(1,"key长度错误");
		}
		try {
			Cipher cipher = Cipher.getInstance(algorithm);
			SecretKeySpec key_spec = new SecretKeySpec(key, "AES");
			IvParameterSpec ivs = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, key_spec, ivs);
			return cipher.doFinal(aesCipher);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(1,"解码出错");
		}
	}
	public static byte[] encrypt(byte[] key,byte[] aesCipher) throws BaseException{
		if(key.length!=16&&key.length!=24&&key.length!=32){
			throw new BaseException(1,"key长度错误");
		}
		try {
			Cipher cipher = Cipher.getInstance(algorithm);
			SecretKeySpec key_spec = new SecretKeySpec(key, "AES");
			IvParameterSpec ivs = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key_spec, ivs);
			return cipher.doFinal(aesCipher);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(1,"加密出错");
		}
	}
	public static String decryptBase64(String key,String aesBase64Cipher) throws BaseException{
		return new String(decrypt(key.getBytes(),Base64.decodeBase64(aesBase64Cipher)));
	}
	public static String encryptBase64(String key,String aesCipher) throws BaseException{
		return Base64.encodeBase64URLSafeString(encrypt(key.getBytes(),aesCipher.getBytes()));
	}
	
	public static void main(String[] args){
		String key="dvt2SQra9RHcrcZFdvt2SQra";
		String aesCipher="基本原则功功用膛";
		String base="uJHKd-T0ddvt2SQra9RHcrcZFyB14HWz6lg6g03Jxlc";
		try {
			System.out.println(iv.length);
			base=AESUtils.encryptBase64(key, aesCipher);
			System.out.println(base);
			System.out.println(AESUtils.decryptBase64(key, base));
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
