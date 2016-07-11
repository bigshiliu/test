package com.hutong.supersdk.sdk.utils.jinli;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.lang.CharEncoding;

import com.hutong.supersdk.sdk.utils.Base64;


/**
 * RSA签名验签类
 * 
 * @author QINZH
 * @since 2.0.0
 */
public class JLRSASignature {

	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	// 跑通demo后，替换成商户自己的private_key
	@SuppressWarnings("unused")
	private static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJZJoM3tJzf96lejbCsQsCgnMAdLtyvf9ZkflExjEUuQ1O7g+OuWc4ZrvtE+coWZmUt2IjYkws63XAbFt/aMEHu/qerp3g/Mn1Y+tVFVbsME+hqhTVknOtt7kv+ZyVymHXZEBvzlTQXiybUDXkRqvvu+hO/+4BhS6fNmuggYpbTnAgMBAAECgYBhIABOVT+NTgnOzYywYD6YFItTi7k7H6fnZ6M9oqCgx171ams0Ra0vaB6Pt93nPZb2T8hYWXWUhqdwsQLs0SQs9+91881Nu1JE1nPAyeQKWv0t6Ol8BavecvEGUcJPXJ1/zZFU7r+s3pdz7OnQV2b77VhZiLsaQsmrxLPALdeTkQJBAMSxDuQymonH0q9KbMAgZ/c7ZraQhc1erf8eb6ni+3eXoJkJBlYsFmp+zFxo6gfj8N8nl0sV1sD3g2HwcUTOimsCQQDDmpG0uqTmAGJryfZOp8YB3VqhTZfFf1xcXQGNkUsaNXFpLMd5O4haRlL4FiROenVucYd0VU4eZIOi33CkYdZ1AkEAjxxUvWykTIN7o9b+8XuiqZwqy8Kz2A1/hBRdIrroRMeqLi8G0UQauzmu773WKg+LfpKL3jHxo01z5prPj0TIKQJBAJl5BMv2Cf4A3ThPnXd3gg/iewLG28d1N6Wsv9Qw5Olqd1KbdON1R3X1aZIH5XLB+LOwViR77jBAk1xOzpXbwiUCQDX4cj4IWy6brwCFFOkcqY23veDTIB8vXC2Kg//Kv1vUJ41DM82/Ga7fBAqCgTHSow3QlByoMFokAxmkU6Zc5EE=";
	// 网游类型接入时固定值

	/**
	 * RSA签名
	 * 
	 * @param content
	 *            待签名数据
	 * @param privateKey
	 *            商户私钥
	 * @param encode
	 *            字符集编码
	 * @return 签名值
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 */
	public static String sign(String content, String privateKey, String encode) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
		String charset = CharEncoding.UTF_8;
		if (!encode.equals("")) {
			charset = encode;
		}
		PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
		KeyFactory keyf = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyf.generatePrivate(priPKCS8);

		java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
		signature.initSign(priKey);
		signature.update(content.getBytes(charset));
		byte[] signed = signature.sign();
		return Base64.encode(signed);

	}

	/**
	 * <pre>
	 * <p>函数功能说明:RSA验签名检查</p>
	 * <p>修改者名字:guocl</p>
	 * <p>修改日期:2012-11-30</p>
	 * <p>修改内容:抛异常</p>
	 * </pre>
	 * 
	 * @param content
	 *            待签名数据
	 * @param sign
	 *            签名值
	 * @param publicKey
	 *            支付宝公钥
	 * @param encode
	 *            字符集编码
	 * @return 布尔值
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public static boolean doCheck(String content, String sign, String publicKey, String encode)
			throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException,
			SignatureException {
		String charset = CharEncoding.UTF_8;
		if (!encode.equals("")) {
			charset = encode;
		}
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] encodedKey = Base64.decode(publicKey);
		PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

		java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

		signature.initVerify(pubKey);
		signature.update(content.getBytes(charset));

		boolean bverify = signature.verify(Base64.decode(sign));
		return bverify;

	}
}
