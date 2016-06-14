package org.lmw.tools.qr;



import java.io.UnsupportedEncodingException;

import org.lmw.tools.util.OrderHttpUtil;
import org.lmw.tools.util.OrderStringUtil;
import org.lmw.tools.util.OrderUrlUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GoodsInfoActivity extends Activity implements OnClickListener{
	TextView res_text;
	ImageView res_pic;
	Button ok;
	private Button bt01,bt02;
	private EditText edt; 
	ImageButton cancel;
	String scan_res;
	String codenumber;
	String res;
	String name;
	String price;
	String getid;
	int num=1;//数量
	String numString="1";
@Override
public void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.goodsinfo_ui);
	initView();
}

public void initView(){
	codenumber=getIntent().getExtras().getString("scan_res");
	
	
	res_text=(TextView)findViewById(R.id.res);
	res_pic=(ImageView)findViewById(R.id.res_pic);
	ok=(Button) findViewById(R.id.ok);
	bt01=(Button)findViewById(R.id.addbt);
	bt02=(Button)findViewById(R.id.subbt);
	edt=(EditText)findViewById(R.id.edt);
	bt01.setTag("+");
	bt02.setTag("-");
	//设置输入类型为数字
			edt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
			edt.setText(String.valueOf(num));
			setViewListener();	
	//cancel=(ImageButton) findViewById(R.id.cancel);
	
	
	MyThread thread1=new MyThread();
	thread1.start();
	
	ok.setOnClickListener(this);
	//cancel.setOnClickListener(this);
	
}
/**
 * 设置文本变化相关监听事件
 */
private void setViewListener()
{
	bt01.setOnClickListener(new OnButtonClickListener());
	bt02.setOnClickListener(new OnButtonClickListener());
	edt.addTextChangedListener(new OnTextChangeListener());
}

/**
 * 加减按钮事件监听器
 * 
 *
 */
class OnButtonClickListener implements OnClickListener
{

	@Override
	public void onClick(View v)
	{
		numString = edt.getText().toString();
		if (numString == null || numString.equals(""))
		{
			num = 0;
			edt.setText("0");
		} else
		{
			if (v.getTag().equals("-"))
			{
				if (++num < 1)  //先加，再判断
				{
					num--;
					Toast.makeText(GoodsInfoActivity.this, "请输入一个大于0的数字",
							Toast.LENGTH_SHORT).show();
				} else
				{
					edt.setText(String.valueOf(num));
					numString = edt.getText().toString();
				}
			} else if (v.getTag().equals("+"))
			{
				if (--num < 1)  //先减，再判断
				{
					num++;
					Toast.makeText(GoodsInfoActivity.this, "请输入一个大于0的数字",
							Toast.LENGTH_SHORT).show();
				} else
				{
					edt.setText(String.valueOf(num));
					numString = edt.getText().toString();
                   Log.i("AAA", numString+"+");
				}
			}
		}
	}
}

/**
 * EditText输入变化事件监听器
 */
class OnTextChangeListener implements TextWatcher
{

	@Override
	public void afterTextChanged(Editable s)
	{
		numString = s.toString();
		if(numString == null || numString.equals(""))
		{
			num = 0;
			numString = edt.getText().toString();
		}
		else {
			int numInt = Integer.parseInt(numString);
			if (numInt < 0)
			{
				Toast.makeText(GoodsInfoActivity.this, "请输入一个大于0的数字",
						Toast.LENGTH_SHORT).show();
			} else
			{
				//设置EditText光标位置 为文本末端
				edt.setSelection(edt.getText().toString().length());
				num = numInt;
				numString = edt.getText().toString();

			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after)
	{

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before,
			int count)
	{
		
	}
	
}


public class MyThread extends Thread{
	@Override
	public void run() {
		String codeString = "codenumber=" + codenumber;
		//String codeString = "codenumber=" + codenumber+"&"+"username="+"test";
		Log.i("AAABBB", codeString);
		String url = OrderHttpUtil.BASE_URL + OrderUrlUtil.CODE_URL + codeString;
		Log.i("AAABBB", url);
		 String encoding = System.getProperty("file.encoding");
		 Log.i("AAABBB", encoding);
		try {
			res = OrderHttpUtil.getHttpPostResultForUrl(url);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Log.i("AAABBB", new String(res.getBytes(),"utf-8")+"======");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message m = new Message();
		
		if("-1".equals(res))
			m.what = OrderStringUtil.CODE_ERROR;
		else if("exception".equals(res))
			m.what=OrderStringUtil.SERVER_ERROR;		
		else
			m.what = OrderStringUtil.CODE_SUCCESS;
		
		proHandle.sendMessage(m);
	}
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
			String arr[]=res.split(",");
			//String arr[]=res.split("+");
			getid=arr[0];
			name=arr[1];
			price=arr[2];
			res_text.setText("商品名称："+name+"\n商品价格："+price+"元/件");
			break;		
		
		}
	}
};






@SuppressLint("ShowToast") @Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	if(v==ok){
		if(res_text.getText().toString().equals("该商品信息在数据库中不存在")){
			Toast.makeText(getApplicationContext(), "请重新扫描一次", 1000).show();
		}else{
		//Intent it = new Intent(getApplicationContext(), GoodsListBlueActivity.class);
			Intent it = new Intent(getApplicationContext(), GoodsListActivity.class);
		it.putExtra("getid", getid);
		it.putExtra("name", name);	
		it.putExtra("price", price);
		it.putExtra("number", numString);
		Log.i("AAA", numString+"res");
		startActivity(it);
		Toast.makeText(GoodsInfoActivity.this, "已经保存到清单", 1000).show();
		finish();
		}
	}
	/*if(v==cancel){
		Toast.makeText(GoodsInfoActivity.this, "你已放弃购买该商品,请将商品放回原处", 1000).show();
	}*/
}

}
