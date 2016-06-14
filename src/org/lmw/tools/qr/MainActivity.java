 package org.lmw.tools.qr;

import java.util.Timer;
import java.util.TimerTask;




import org.lmw.tools.util.OrderStringUtil;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity{
	public static String str1;
	public TabHost mth;
	public static final String TAB_HOME="首页";
	public static final String TAB_SEARCH="商品搜寻";
	public static final String TAB_MSG="消费记录";
	public static final String TAB_UNFINISHED="未完成订单";
	public RadioGroup radioGroup;
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main_ui);
	str1 = OrderStringUtil.getDataFromIntent(getIntent());
	mth=this.getTabHost();
	TabSpec ts1=mth.newTabSpec(TAB_HOME).setIndicator(TAB_HOME);
    ts1.setContent(new Intent(MainActivity.this,FirstActivity.class));
    mth.addTab(ts1);
    TabSpec ts2=mth.newTabSpec(TAB_SEARCH).setIndicator(TAB_SEARCH);
    ts2.setContent(new Intent(MainActivity.this,SecondActivity.class));
    mth.addTab(ts2);
    
    TabSpec ts3=mth.newTabSpec(TAB_MSG).setIndicator(TAB_MSG);
    ts3.setContent(new Intent(MainActivity.this,ThirdActivity.class));
    mth.addTab(ts3);
    /*TabSpec ts4=mth.newTabSpec(TAB_UNFINISHED).setIndicator(TAB_UNFINISHED);
    ts4.setContent(new Intent(MainActivity.this,FourActivity.class));
    mth.addTab(ts4);*/
    this.radioGroup=(RadioGroup)findViewById(R.id.main_radio);
    
    radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			
			switch(checkedId){
			case R.id.radio_button0:
				mth.setCurrentTabByTag(TAB_HOME);
				break;
			case R.id.radio_button1:
				mth.setCurrentTabByTag(TAB_SEARCH);
				break;
		    /*case R.id.radio_button2:
		    	mth.setCurrentTabByTag(TAB_UNFINISHED);
		    	break;*/
			case R.id.radio_button4:
				mth.setCurrentTabByTag(TAB_MSG);
				break;
			}
		}
	});
}


/**
 * 菜单、返回键响应
 *//*
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
 // TODO Auto-generated method stub
 if(keyCode == KeyEvent.KEYCODE_BACK)
{ 
  exitBy2Click(); //调用双击退出函数
}
 return false;
}
*//**
 * 双击退出函数
 *//*
private static Boolean isExit = false;
 
private void exitBy2Click() {
 Timer tExit = null;
 if (isExit == false) {
 isExit = true; // 准备退出
 Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
 tExit = new Timer();
 tExit.schedule(new TimerTask() {
  @Override
  public void run() {
  isExit = false; // 取消退出
  }
 }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
 
 } else {
 finish();
 System.exit(0);
 }
}*/
}
