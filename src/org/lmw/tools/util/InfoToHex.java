package org.lmw.tools.util;

import java.util.List;

import org.lmw.tools.qr.bean.GoodsBean;

public class InfoToHex {
	//用户名转化成5位16进制数。
public static byte[] userNameToHex(String username){
	int i;
	String nameHex=ChangeToHex.toHexString(username);
	String[] nametemp = nameHex.split(" ");
	byte[] nameArr = new byte[5];
	for (i = 0; i < nametemp.length; i++) {
		nameArr[i] = (byte) Integer.parseInt(nametemp[i], 16);
	}
	if(i<5){
		for(int j=0;j<5-nametemp.length;j++){
			nameArr[nametemp.length+j]=(byte) Integer.parseInt("0", 16);
			
		}
		
	}
	return nameArr;
}

public static byte[] numberToHex(int number){
	int i;
	
	String numberHex=ChangeToHex.intToHexString(number, 1);
	
	byte[] numberArr = new byte[1];
	
	  numberArr[0] = (byte) Integer.parseInt(numberHex, 16);
	
	
	return numberArr;
}

public static byte[] goodsListToHex(List<GoodsBean> goodsList){
	int i;
	String goodsHex = "";
	for(int j=0;j<goodsList.size();j++){
		 GoodsBean goods=goodsList.get(j);
		 goodsHex=goodsHex+ChangeToHex.intToHexString(Integer.parseInt(goods.getGetid()), 2)+" ";
		 goodsHex=goodsHex+ChangeToHex.intToHexString(goods.getNumber(), 1)+" ";
		 
	}
	
	String[] goodstemp = goodsHex.split(" ");
	byte[] goodsArr = new byte[30];
	for (i = 0; i < goodstemp.length; i++) {
		goodsArr[i] = (byte) Integer.parseInt(goodstemp[i], 16);
	}
	
	return goodsArr;
}


}
