package org.lmw.tools.qr;

import org.lmw.tools.util.CalendarUtil;
import org.lmw.tools.util.OrderStringUtil;





import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class FirstActivity extends Activity {
	TextView user_info_name;
	TextView user_info_remain;
	TextView time;
	public static String username;
	public static float remainerPrice;
	public static int reward;
	private String str;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one);
		setTitle("第一个页面");
		user_info_remain=(TextView) findViewById(R.id.user_info_remain);
		user_info_name=(TextView) findViewById(R.id.user_info_name);
		time=(TextView) findViewById(R.id.time);
		str=MainActivity.str1;
		/**
		 * str 是登陆的成功数据
		 * 
		 */
		String strs[] = str.split(",");
		
		username=strs[1];
		remainerPrice=Float.parseFloat(strs[3]);
		reward=Integer.parseInt(strs[2]);
		StringBuilder buf = new StringBuilder();
		buf.append("欢迎访问智能购物系统.");
	
	    CalendarUtil cu = new CalendarUtil();
	    String chineseMonth = cu.getChineseMonth(Integer.parseInt(OrderStringUtil.getCurrentDate("yyyy")),
				Integer.parseInt(OrderStringUtil.getCurrentDate("MM")), Integer.parseInt(OrderStringUtil.getCurrentDate("dd")));
		String chineseDay = cu.getChineseDay(Integer.parseInt(OrderStringUtil.getCurrentDate("yyyy")),
				Integer.parseInt(OrderStringUtil.getCurrentDate("MM")), Integer.parseInt(OrderStringUtil.getCurrentDate("dd")));
		buf.append("今天是：").append(OrderStringUtil.getCurrentDate("yyyy年MM月dd日"));
		buf.append(";农历：").append(chineseMonth).append(chineseDay);
		time.setText(buf.toString());
		user_info_name.setText("欢迎您："+username);;
		user_info_remain.setText("余额："+remainerPrice+"  积分："+reward);
	}
	public void scanPic(View v){
		Intent intent=new Intent();
		intent.setClass(FirstActivity.this, ScanActivity.class);
		startActivity(intent);
	}
	

}
