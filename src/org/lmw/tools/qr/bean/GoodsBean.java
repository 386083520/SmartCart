package org.lmw.tools.qr.bean;

public class GoodsBean {
	
public static final String ID = "_id";
public static final String GETID = "getid";
public static final String NAME = "name";
public static final String PRICE = "price";
public static final String NUMBER = "number";
private String id;
private String getid;
private String name;
private String price;
private Integer number;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getPrice() {
	return price;
}
public void setPrice(String price) {
	this.price = price;
}
public Integer getNumber() {
	return number;
}
public void setNumber(Integer number) {
	this.number = number;
}
public String getGetid() {
	return getid;
}
public void setGetid(String getid) {
	this.getid = getid;
}

}
