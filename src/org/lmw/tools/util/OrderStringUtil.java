package org.lmw.tools.util;



import java.text.SimpleDateFormat;
import java.util.Date;

import org.lmw.tools.qr.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;





public class OrderStringUtil {
	
	/**
	 * Handler 消息内容列表
	 */
	public static final int LOGIN_ERROR = 0; // 登陆失败
	public static final int LOGIN_SUCCESS = 1; // 登陆成功
	public static final int CODE_ERROR = 0; 
	public static final int CODE_SUCCESS = 1;
	public static final int GETLIST_ERROR = 0; 
	public static final int GETLIST_SUCCESS = 1;
	public static final int PAY_ERROR = 0; 
	public static final int PAY_SUCCESS = 1;
	public static final int SERVER_ERROR = 2; // 服务器异常
	public static final int SERVER_NO_DATA = 3; // 服务器无数据
	public static final int OPS = 4; // 发送通道打开
	public static final int IPS = 5; // 接收通道打开
	public static final int ERROR = 6; // 错误操作
	public static final int OK = 7; // 操作正常
	public static final int NEW_ORDER_FINASH = 8; // 新增更新完成
	public static final int UPDATE_ORDER_FINASH = 9; // 修改更新完成
	public static final int DELETE_ORDER_FINASH = 10; // 删除更新完成
	public static final int ERROR_ORDER_FINASH = 11; // 错误更新完成
	public static final int BASE_MODIFY_OK = 14; // 信息修改成功
	public static final int PASSWORD_MODIFY_OK = 15; // 信息修改成功
	public static final int EMAIL_EXISTS = 16; // 邮箱已经存在
	public static final int BASE_ERROR = 17; // 基本信息修改错误
	public static final int PASSWORD_ERROR = 18; // 密码修改错误
	public static final int PASSWORD_OLD_REEOR = 19; // 原密码错误
	public static final int BLUESOCKET_NOT_CONNECTED=20;//蓝牙socket没连接
	public static final int BLUESOCKET_IS_CONNECTED=21;//蓝牙socket没连接
	public static final int BLUESOCKET_IS_GET=22;//蓝牙socket没连接
	public static final int GETPRICESUCCESS=23;
	public static final int GETPRICEERROR=24;
	/**
	 * 传递登陆数据，从Intent中获取Bundle数据
	 * 结合 putDataIntoIntent(Intent, String) 使用
	 * @param intent
	 * @return String id,loginid,password,nikename,phone,email,gender,create_at	
	 */
	public static String getDataFromIntent(Intent intent) {
		Bundle bundle = intent.getBundleExtra("data");
		String res = bundle.getString("data");;				
		return res;
	}
	
	/**
	 * 进度对话框
	 * @param context
	 * @param title
	 * @param message
	 * @param canCelable
	 * @param indeterminate
	 */
	public static ProgressDialog createProgressDialog(Context context, String title,
			String message, boolean canCelable, boolean indeterminate) {
		ProgressDialog p = new ProgressDialog(context);
		p.setIcon(R.drawable.progress);
		p.setTitle(title);
		p.setMessage(message);
		p.setCancelable(canCelable);
		p.setIndeterminate(indeterminate);
		return p;
	}
	
	
	/**
	 * 返回 format 格式的时间字符串
	 * 时间格式为 yyyy-MM-dd HH:mm:ss
	 * yyyy 返回4位年份
	 * MM 返回2位月份
	 * dd 返回2位日
	 * 时间类同
	 * @return 相应日期类型的字符串
	 */
	public static String getCurrentDate(String format){
		 SimpleDateFormat sdf = new SimpleDateFormat(format);
		 return sdf.format(new Date()).toString();
	}
}
