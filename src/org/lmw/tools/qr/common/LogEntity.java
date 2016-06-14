package org.lmw.tools.qr.common;

import java.util.Date;

public class LogEntity {
	private String res;	//扫描结果
	private String time;		//扫描时间
	private String pic;		//扫描截图
	
	public String getRes() {
		return res;
	}
	public void setRes(String res) {
		this.res = res;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public LogEntity() {
	}
	
	public LogEntity(String res,String pic) {
		this.res=res;
		this.pic=pic;
		this.time=DateUtils.dateToStr("yyyy-MM-dd HH:mm", new Date());
	}
}
