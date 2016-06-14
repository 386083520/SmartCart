package org.lmw.tools.qr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.lmw.tools.util.ChangeToHex;
import org.lmw.tools.util.OrderStringUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GoodsInfoByBlueTooth extends Activity implements OnClickListener{
	OutputStream ops=null;
	InputStream ips=null;
	String codenumber;
	TextView res_text;
	ImageView res_pic;
	ImageButton ok;
	ImageButton cancel;
	String scan_res;
	private List<Integer> mBuffer;
	String res;
	String name;
	String price;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.goodsinfo_ui);
	res_text=(TextView)findViewById(R.id.res);
	res_pic=(ImageView)findViewById(R.id.res_pic);
	ok=(ImageButton) findViewById(R.id.ok);
	//cancel=(ImageButton) findViewById(R.id.cancel);
	GetInfoThread getInfoThread=new GetInfoThread();
	getInfoThread.start();
	//receiveMessage();
}
public class GetInfoThread extends Thread{
	@Override
	public void run() {
		codenumber=getIntent().getExtras().getString("scan_res");
		Integer codenumberOne=Integer.parseInt(codenumber.substring(0, 4));
		Integer codenumberTwo=Integer.parseInt(codenumber.substring(4, 8));
		Integer codenumberThree=Integer.parseInt(codenumber.substring(8, 13));
		Integer codenumberFour=Integer.parseInt(codenumber.substring(codenumber.length()-1));
		
		String codenumberOneHex=ChangeToHex.intToHexString(codenumberOne, 2);
		String codenumberTwoHex=ChangeToHex.intToHexString(codenumberTwo, 2);
		
		String codenumberThreeHex=ChangeToHex.intToHexString(codenumberThree, 3);
		
		String codenumberFourHex=ChangeToHex.intToHexString(codenumberFour, 1);
		
		String codenumberHex=codenumberOneHex+" "+codenumberTwoHex+" "+codenumberThreeHex+" "+codenumberFourHex;
		
		
		String[] codenumbertemp = codenumberHex.split(" ");
		byte[] codenumberArr = new byte[8];
		for (int i = 0; i < 8; i++) {
			codenumberArr[i] = (byte) Integer.parseInt(codenumbertemp[i], 16);
		}
		byte[] arr=new byte[20];
		arr[0]=(byte) 0xee;
		arr[1]=(byte) 0xbb;
		for(int i=2;i<10;i++){
			arr[i]=codenumberArr[i-2];
		}
		for(int i=10;i<19;i++){
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
		MethodChoiceActivity.blueSocket.connect();
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
	if("-1".equals(res))
		m.what = OrderStringUtil.CODE_ERROR;
	else if("exception".equals(res))
		m.what=OrderStringUtil.SERVER_ERROR;		
	else
		m.what = OrderStringUtil.CODE_SUCCESS;
	
	proHandle.sendMessage(m);
}



private Handler proHandle = new Handler(){
	@Override
	public void handleMessage(Message msg) {

		
		switch(msg.what){
		case OrderStringUtil.CODE_ERROR:
			res_text.setText("该商品信息在数据库中不存在");
			break;
		case OrderStringUtil.SERVER_ERROR:
			res_text.setText("抱歉，连接异常");
			break;
		case OrderStringUtil.CODE_SUCCESS:	
			
			res_text.setText("商品名称："+res);
			break;		
		
		}
	}
};






@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	if(v==ok){
		if(res_text.getText().toString().equals("该商品信息在数据库中不存在")){
			Toast.makeText(getApplicationContext(), "请重新扫描一次", 1000).show();
		}else{
		Intent it = new Intent(getApplicationContext(), GoodsListBlueActivity.class);
		it.putExtra("name", name);	
		it.putExtra("price", price);
		startActivity(it);
		Toast.makeText(GoodsInfoByBlueTooth.this, "已经保存到清单", 1000).show();
		finish();
		}
	}
	if(v==cancel){
		Toast.makeText(GoodsInfoByBlueTooth.this, "你已放弃购买该商品,请将商品放回原处", 1000).show();
	}
}
}
