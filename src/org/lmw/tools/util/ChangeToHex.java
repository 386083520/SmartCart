package org.lmw.tools.util;

import java.io.ByteArrayOutputStream;

public class ChangeToHex {
	
	private static String hexString = "0123456789ABCDEF"; 
	//转化字符串为十六进制编码  
	public static String toHexString(String s) {  
	 String str = "";  
	 for (int i = 0; i < s.length(); i++) {  
	  int ch = (int) s.charAt(i);  
	  String s4 = Integer.toHexString(ch);  
	  str = str + s4+" ";  
	 }  
	
	 return str;  
	}  
	
	 //将数字转化成指定长度的16进制编码
	 public static String intToHexString(int getNum,int num){
		String getHex=Integer.toHexString(getNum);
		if(num==1){
			if(getHex.length()==1){
				getHex="0"+getHex;
			}
			return getHex;
		} 
		if(num==2){
			if(getHex.length()==1){
				getHex="000"+getHex;
			}
			if(getHex.length()==2){
				getHex="00"+getHex;
			}
			if(getHex.length()==3){
				getHex="0"+getHex;
			}
			return getHex.substring(0, 2)+" "+getHex.substring(2, 4);
		} 
		if(num==3){
			if(getHex.length()==1){
				getHex="00000"+getHex;
			}
			if(getHex.length()==2){
				getHex="0000"+getHex;
			}
			if(getHex.length()==3){
				getHex="000"+getHex;
			}
			if(getHex.length()==4){
				getHex="00"+getHex;
			}
			if(getHex.length()==5){
				getHex="0"+getHex;
			}
			return getHex.substring(0, 2)+" "+getHex.substring(2, 4)+" "+getHex.substring(4, 6);
		} 
		return "";
	 }
	 
	 
	// 转化十六进制编码为字符串
	 public static String hexToString(String s)
	 {
	 byte[] baKeyword = new byte[s.length()/2];
	 for(int i = 0; i < baKeyword.length; i++)
	 {
	 try
	 {
	 baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16));
	 }
	 catch(Exception e)
	 {
	 e.printStackTrace();
	 }
	 }
	 try
	 {
	 s = new String(baKeyword, "utf-8");//UTF-16le:Not
	 }
	 catch (Exception e1)
	 {
	 e1.printStackTrace();
	 }
	 return s;
	 } 
}
