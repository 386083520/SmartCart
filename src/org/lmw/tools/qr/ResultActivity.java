package org.lmw.tools.qr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.lmw.tools.qr.common.App;
import org.lmw.tools.qr.common.BaseActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends BaseActivity implements OnClickListener{
	TextView res;
	ImageView res_pic;
	ImageButton copy;
	ImageButton share;
	ImageButton browser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_res);
		initView();
	}
	
	public void initView(){
		String scan_res=getIntent().getExtras().getString("scan_res");
		String scan_pic=getIntent().getExtras().getString("scan_pic");
		
		res=(TextView)findViewById(R.id.res);
		res_pic=(ImageView)findViewById(R.id.res_pic);
		copy=(ImageButton)findViewById(R.id.copy);
		share=(ImageButton)findViewById(R.id.share);
		browser=(ImageButton)findViewById(R.id.browser);
		
		Bitmap bitmap = getLoacalBitmap(scan_pic); 
		res.setText(scan_res);
		res_pic.setImageBitmap(bitmap);
		
		copy.setOnClickListener(this);
		share.setOnClickListener(this);
		browser.setOnClickListener(this);
	}
	
	 /**
	    * 加载本地图片
	    * @param url
	    * @return
	    */
	    public static Bitmap getLoacalBitmap(String url) {
	         try {
	              FileInputStream fis = new FileInputStream(url);
	              return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片        
	           } catch (FileNotFoundException e) {
	              e.printStackTrace();
	              return null;
	         }
	    }
	    
	    

	@Override
	public void onClick(View v) {
	if(v==copy){
		App.copy(res.getText().toString(), ResultActivity.this);
	}
	if(v==share){
		Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送到属性
		intent.setType("text/plain"); // 分享发送到数据类型
		//intent.putExtra(Intent.EXTRA_SUBJECT, "subject"); // 分享的主题
		intent.putExtra(Intent.EXTRA_TEXT, res.getText().toString()); // 分享的内容
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 允许intent启动新的activity
		startActivity(Intent.createChooser(intent, "分享")); // //目标应用选择对话框的标题
	}
	if(v==browser){
		try {
			Intent intent= new Intent();        
		    intent.setAction("android.intent.action.VIEW");    
		    Uri content_url = Uri.parse(res.getText().toString());   
		   // Uri content_url =Uri.parse("www.baidu.com");
		    intent.setData(content_url);  
		    startActivity(intent);
		} catch (Exception e) {
			showToast("无效网址");
		}

	}
	}
	    
}
