package org.lmw.tools.qr;









import org.lmw.tools.util.OrderHttpUtil;
import org.lmw.tools.util.OrderStringUtil;




import org.lmw.tools.util.OrderUrlUtil;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends Activity{
	private EditText username;
	private EditText password;
	private EditText pay_password;
	private ProgressDialog proDlg;
	private String res;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.register_ui);
	username = (EditText)findViewById(R.id.text_reg_username);
	password = (EditText)findViewById(R.id.text_reg_password);
	pay_password=(EditText) findViewById(R.id.text_pay_password);
}
@Override
protected void onStop() {
	// TODO Auto-generated method stub
	finish();
	super.onStop();
}
public void register(View v){
	final String uname = username.getText().toString();
	final String upwd = password.getText().toString();
	final String upaypassword = pay_password.getText().toString();
	if("".equals(uname.trim())){ // 用户名为空！

		AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
		builder.setIcon(R.drawable.alert_wanring)
				.setTitle(R.string.login_account_null)
				.setMessage(R.string.login_account_null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 点击确定按钮
					public void onClick(DialogInterface dialog, int which) {
						username.setText("");
						password.setText("");
						
					}
				}).show();
		return ;
		
	} 
	if("".equals(upwd)){
		AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
		builder.setIcon(R.drawable.alert_wanring)
				.setTitle(R.string.login_password_null)
				.setMessage(R.string.login_password_null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 点击确定按钮
					public void onClick(DialogInterface dialog, int which) {
						password.setText("");
						
					}
				}).show();
		return ;
	} 
	if(upaypassword.length()<6){ // 密码长度不够

		AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
		builder.setIcon(R.drawable.alert_wanring)
				.setTitle(R.string.login_account_null)
				.setMessage(R.string.login_account_null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 点击确定按钮
					public void onClick(DialogInterface dialog, int which) {
						username.setText("");
						password.setText("");
						pay_password.setText("");
					}
				}).show();
		return ;
		
	} 
	
	proDlg = OrderStringUtil.createProgressDialog(RegisterActivity.this, getResources().getString(R.string.pro_title),
			getResources().getString(R.string.pro_message), true, true);
	proDlg.show();
	
	new Thread(){
		@Override
		public void run() {
			/**
			 * 1  验证用户是否存在，不存在，注册
			 * 2  注册成功，返回账号和密码显示
			 * 3  登录
			 */
			String registerString = "username=" + uname + "&password=" + upwd + "&payment_passwd=" + upaypassword ;
			Log.i("AAABBB", registerString);
			System.out.println(registerString);
			String url = OrderHttpUtil.BASE_URL + OrderUrlUtil.REGISTER_URL + registerString;					
			
			res = OrderHttpUtil.getHttpGetResultForUrl(url);
			handler.sendEmptyMessage(1);
		}
	}.start();
	
}
public void goback(View v){
	Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
	startActivity(intent);
	finish();
}
public void reset(View v){
	username.setText("");
	password.setText("");
}
private Handler handler = new Handler(){
	public void dispatchMessage(Message msg) {
		proDlg.dismiss();
		showRegisterMesg(res);
	};
};
/**
 * 提示注册信息
 * 1 注册成功
 * 0 注册失败
 * 2 登陆ID已存在
 * exception 网络异常
 * @param res
 */
protected void showRegisterMesg(String res) {
	if("0".equals(res)){
		AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
		builder.setIcon(R.drawable.alert_error)
				.setTitle("注册失败")
				.setMessage("注册失败，请稍后再试！")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 点击确定按钮
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
		return ;
	}
	if("1".equals(res)){
		AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
		builder.setIcon(R.drawable.alert_add)
				.setTitle("注册成功")
				.setMessage("恭喜您，注册成功，请登陆！")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 点击确定按钮
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
						startActivity(intent);
					}
				}).show();
		return ;
	}
	
	if("2".equals(res)){
		AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
		builder.setIcon(R.drawable.alert_error)
				.setTitle("登陆账号已存在")
				.setMessage("登陆账号已存在，请使用其它账号！")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 点击确定按钮
					public void onClick(DialogInterface dialog, int which) {
						username.setText("");
					}
				}).show();
		return ;
	}
	if("exception".equals(res)){
		AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
		builder.setIcon(R.drawable.alert_error)
				.setTitle("网络异常")
				.setMessage("请检查网络后重新注册")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 点击确定按钮
					public void onClick(DialogInterface dialog, int which) {
						username.setText("");
						password.setText("");
					}
				}).show();
		return ;
	}
	
}

}
