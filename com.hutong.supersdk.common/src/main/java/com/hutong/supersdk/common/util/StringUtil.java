package com.hutong.supersdk.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * String操作工具类
 * 
 * @author QINZH
 *
 */
public final class StringUtil {

	private StringUtil() {}
	
	
	/**
	 * 随机生成UUID并且去掉'-'连接符
	 * 示例:ABCDEFGHIJKLMNOP
	 * @return
	 */
	public static String randomUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 如果字符串为null,为空字符串则输出"";否则不变
	 * 
	 * @param str
	 * @return str or ""
	 */
	public static String isNull(String str) {
		if (null != str && !"".equals(str) && str.length() > 0)
			return str;
		return "";
	}

	/**
	 * 重写String.valueOf 为null时返回空字符串,否则调用toString()
	 * @param obj
	 * @return
	 */
	public static String valueOf(Object obj) {
		return (obj == null) ? "" : obj.toString();
    }

	/**
	 * 字符串对比 仅当str1,str2不为null,不为空字符串并且相同的情况下,返回true 否则返回false
	 *
	 * @param str1
	 * @param str2
	 * @param ignoreCase 是否大小写敏感
	 * @return boolean
	 */
	public static boolean equalsString(String str1, String str2, boolean ignoreCase) {
		if (null != str1 && !"".equals(str1) && null != str2 && !"".equals(str2))
			return ignoreCase ? str1.equalsIgnoreCase(str2) : str1.equals(str2);
		return false;
	}
	
	/**
	 * 字符串对比(大小写不敏感) 仅当str1,str2不为null,不为空字符串并且相同的情况下,返回true 否则返回false
	 * 
	 * @param str1
	 * @param str2
	 * @return boolean
	 */
	public static boolean equalsString(String str1, String str2) {
		return equalsString(str1, str2, false);
	}

	/**
	 * 字符串对比(忽略大小写) 仅当str1,str2不为null,不为空字符串并且相同的情况下,返回true 否则返回false
	 * 
	 * @param str1
	 * @param str2
	 * @return boolean
	 */
	public static boolean equalsStringIgnoreCase(String str1, String str2) {
        return equalsString(str1, str2, true);
	}
	
	/**
	 * 时间戳转换
	 * 将Linux时间戳转换为Date格式
	 * @param timestampString
	 * @return
	 */
	public static String timeStamp2Date(String timestampString) {
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(timestamp));
		return date;
	}
	/**
	 * 验证邮箱格式正确性
	 * @param email
	 * @return
	 */
	public static boolean emailFormat(String email) 
    { 
        boolean tag = true;   
        final String pattern1 = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$"; 
        final Pattern pattern = Pattern.compile(pattern1); 
        final Matcher mat = pattern.matcher(email); 
        if (!mat.find()) { 
            tag = false; 
        } 
        return tag; 
    }
	
	/**
	 * 生成8位UUID
	 * 利用62个可打印字符,通过随机生成32位UUID,
	 * 由于UUID都为十六进制,所以将UUID分成8组,每4个为一组,
	 * 然后通过模62操作,结果作为索引取出字符,
	 * 这样重复率大大降低.经测试,在生成一千万个数据也没有出现重复,完全满足大部分需求.
	 */
	public static String randomShortUUID() {
		String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
				"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
				"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
				"W", "X", "Y", "Z" };
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = randomUUID();
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0x3E]);
		}
		return shortBuffer.toString();
	}
	
	/**
	 * 生成指定长度随机字符串
	 * @param length
	 * @return
	 */
	public static String randomString(int length) { 
		//取值范围
	    String englishUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    String englishLower = englishUpper.toLowerCase();
	    String numberStr = "0123456789";
	    String signStr = "`~!@#$%^&*()_+-=[]{}|;':,./<>?";
	    //取值总结果
	    StringBuffer resultStr = new StringBuffer();
	    resultStr.append(englishUpper);
	    resultStr.append(englishLower);
	    resultStr.append(numberStr);
	    resultStr.append(signStr);
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(resultStr.length());   
	        sb.append(resultStr.charAt(number));   
	    }   
	    return sb.toString();   
	 } 
	
	/**
	 * 字符数组格式转换
	 * @param strings
	 * @param formatSpilt
	 * @return
	 */
	public static String stringArrayFormat(String[] strings, String formatSpilt){
		if(null == strings || 1 > strings.length)
			return "";
		StringBuffer sb = new StringBuffer();
		for(String temp: strings){
			sb.append(temp);
			sb.append(formatSpilt);
		}
		return sb.substring(0, sb.length() - 1);
	}
	
	/**
	 * 表单格式字符串转换
	 * @param str
	 * @return
	 */
	public static Map<String, String> stringFormFormat(String str){
		String[] strTotal = str.split("&");
		if(strTotal.length < 1)
			return null;
		Map<String, String> resMap = new HashMap<String, String>();
		for(String strTemp : strTotal){
			String[] strSingle = strTemp.split("=");
			if(1 == strSingle.length)
				resMap.put(strSingle[0], "");
			if(2 == strSingle.length)
				resMap.put(strSingle[0], strSingle[1]);
		}
		return resMap;
	}
	
	/**
	 * Base64转换String
	 * @param encodeStr
	 * @return
	 * @throws Exception
	 */
	public static String decodeBase64(String encodeStr) throws Exception{  
		String condeStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        StringBuilder sb = new StringBuilder("");  
        for (int i = 0; i < encodeStr.length(); i++){  
              
            char c = encodeStr.charAt(i);       //把"1tC5sg=="字符串一个个分拆  
            int k = condeStr.indexOf(c);        //分拆后的字符在CODE_STR中的位置,从0开始,如果是'=',返回-1  
            if(k != -1){                        //如果该字符不是'='  
                String tmpStr = Integer.toBinaryString(k);  
                int n = 0;  
                while(tmpStr.length() + n < 6){  
                    n ++;  
                    sb.append("0");  
                }  
                sb.append(tmpStr);  
            }  
        }  
          
        /** 
         * 8个字节分拆一次，得到总的字符数 
         * 余数是加密的时候补的，舍去 
         */  
        int newByteLen = sb.length() / 8;           
          
        /** 
         * 二进制转成字节数组 
         */  
        byte[] b = new byte[newByteLen];  
        for(int j = 0; j < newByteLen; j++){  
            b[j] = (byte)Integer.parseInt(sb.substring(j * 8, (j+1) * 8),2);  
        }  
          
        /** 
         * 字节数组还原成String 
         */  
        return new String(b, "gb2312");  
    }
	
	public static void main(String[] args) {
//		String aaa = "user_name=&password=12414113&nmld__dasdsadd=5555555&kdmasdmsadad=ed1e1e1e";
//		Map<String, String> tt = stringFormFormat(aaa);
//		System.out.println(tt.toString());
		
		
//		String aaa = "ndjasnddndasd@qq.com";
//		try {
//			System.out.println(URLEncoder.encode(aaa,"UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	} 
}
