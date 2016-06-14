package org.lmw.tools.qr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.lmw.tools.qr.LoginActivity.MyThread;
import org.lmw.tools.util.ChangeToHex;
import org.lmw.tools.util.OrderHttpUtil;
import org.lmw.tools.util.OrderStringUtil;
import org.lmw.tools.util.OrderUrlUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginBlueActivity extends Activity{
	private EditText usernameEt;
	private EditText passwordEt;
	CheckBox chk;
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	ArrayList<String> usernameArraylist;
	ArrayList<String> passwordArraylist;
	ArrayList<Integer> idArraylist;
	String username;
	String password;
	String name;
	String pass;
	private List<Integer> mBuffer;
	private String res;
	private ProgressDialog prgDialog;
	public static LoginBlueActivity instance;
	OutputStream ops=null;
	InputStream ips=null;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
    setContentView(R.layout.login_ui);
    instance=this;
	usernameEt=(EditText) findViewById(R.id.usernameEt);
	passwordEt=(EditText) findViewById(R.id.passwordEt);
	chk = (CheckBox) findViewById(R.id.checkSaveName);
	pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
	editor = pref.edit();
	name = pref.getString("userName", "");
	pass = pref.getString("passWord", "");
	if (name == null) {
		chk.setChecked(false);
	} else {
		chk.setChecked(true);
		usernameEt.setText(name);
		usernameEt.setSelection(name.length());
		passwordEt.setText(pass);
		passwordEt.setSelection(pass.length());
	}
}

public void login(View v){
	// 显示登陆对话框
	prgDialog = new ProgressDialog(LoginBlueActivity.this);
	prgDialog.setIcon(R.drawable.progress);
	prgDialog.setTitle("请稍等");
	prgDialog.setMessage("正在登陆，请稍等...");
	prgDialog.setCancelable(false);
	prgDialog.setIndeterminate(true);
	prgDialog.show();
	name = usernameEt.getText().toString().trim();
	pass = passwordEt.getText().toString().trim();
	MyThread thread1=new MyThread();
	thread1.start();
}

public void register(View v){
	Intent intent = new Intent(LoginBlueActivity.this, RegisterActivity.class);
	startActivity(intent);
}
public class MyThread extends Thread{
	@Override
	public void run() {
		String nameHex=ChangeToHex.toHexString(name);
		String[] nametemp = nameHex.split(" ");
		byte[] nameArr = new byte[5];
		for (int i = 0; i < 5; i++) {
			nameArr[i] = (byte) Integer.parseInt(nametemp[i], 16);
		}
		String passHex=ChangeToHex.toHexString(pass);
		String[] passtemp = passHex.split(" ");
		byte[] passArr = new byte[5];
		for (int i = 0; i < 5; i++) {
			passArr[i] = (byte) Integer.parseInt(passtemp[i], 16);
		}
		
		byte[] arr=new byte[20];
		arr[0]=(byte) 0xee;
		arr[1]=(byte) 0xaa;
		for(int i=2;i<7;i++){
			arr[i]=nameArr[i-2];
		}
		for(int i=7;i<12;i++){
			arr[i]=passArr[i-7];
		}
		for(int i=12;i<19;i++){
			arr[i]=0x00;
		}
		arr[19]=(byte) 0xff;
		
			
			try {
				ops=MethodChoiceActivity.blueSocket.getOutputStream();
				ops.write(arr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			receiveMessage();
		
	}
}
private void receiveMessage() {
	mBuffer = new ArrayList<Integer>();
	byte[] buffer = new byte[256];
	int bytes;
	StringBuffer buf = new StringBuffer();
	Message m = new Message();
	try {
		ips=MethodChoiceActivity.blueSocket.getInputStream();
		bytes=ips.read(buffer);
		synchronized (mBuffer) {
			for (int i = 0; i < bytes; i++) {
				mBuffer.add(buffer[i] & 0xFF);
			}
		}
		for (int i : mBuffer) {
			buf.append(Integer.toHexString(i));
			buf.append(' ');
		}
		res=buf.toString();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	String a=res.substring(0, 1);
	Log.i("AAABBB", res);
	Log.i("AAABB", a);
	if("0".equals(a))
		m.what = OrderStringUtil.LOGIN_ERROR;
			
	else if("1".equals(a))
		m.what = OrderStringUtil.LOGIN_SUCCESS;
	else if("exception".equals(res))
		m.what=OrderStringUtil.SERVER_ERROR;
	else
		m.what=OrderStringUtil.SERVER_ERROR;
	proHandle.sendMessage(m);
}
private Handler proHandle = new Handler(){
	@Override
	public void handleMessage(Message msg) {

		AlertDialog.Builder builder = new AlertDialog.Builder(LoginBlueActivity.this);
		
		prgDialog.dismiss();
		switch(msg.what){
		case OrderStringUtil.LOGIN_ERROR:
			builder.setIcon(R.drawable.alert_error)
					.setTitle("错误")
					.setMessage("用户名或密码错误，请确认")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						// 点击确定按钮
						public void onClick(DialogInterface dialog, int which) {									
						}
					}).show();
			break;
		case OrderStringUtil.SERVER_ERROR:
			builder.setIcon(R.drawable.alert_error)
					.setTitle("错误")
					.setMessage("请检查网络连接后再试")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						// 点击确定按钮
						public void onClick(DialogInterface dialog, int which) {									
						}
					}).show();
			break;
		case OrderStringUtil.LOGIN_SUCCESS:	
			if (chk.isChecked()) {
				editor.putString("userName", name);
				editor.putString("passWord", pass);
				editor.commit();
			} else {
				editor.remove("userName");
				editor.remove("passWord");
				editor.commit();
			}
			builder.setIcon(R.drawable.alert_ok)
					.setTitle("登陆成功")
					.setMessage("恭喜您，登陆成功")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						// 点击确定按钮
						public void onClick(DialogInterface dialog, int which) {
							
							Intent intent=new Intent();
							
							Bundle bundle = new Bundle();
							//bundle.putString("data", res);
							bundle.putString("data", res+","+name+",1234,2000");
							intent.putExtra("data", bundle);
							intent.setClass(LoginBlueActivity.this, MainActivity.class);	
							startActivity(intent);
							
							
						}
					}).show();
			break;			
		}
	}
};


}
