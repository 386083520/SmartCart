package org.lmw.tools.qr;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lmw.tools.qr.LoginActivity.MyThread;
import org.lmw.tools.qr.bean.PurchaseBean;
import org.lmw.tools.qr.dapter.JsonAdapter;
import org.lmw.tools.util.CalendarUtil;
import org.lmw.tools.util.OrderHttpUtil;
import org.lmw.tools.util.OrderStringUtil;






import org.lmw.tools.util.OrderUrlUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ThirdActivity extends Activity {
	String res;
	TextView tv;
	private ListView listView;
	private JsonAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.three);
		tv=(TextView) findViewById(R.id.tv);
		listView=(ListView) findViewById(R.id.listView);
		adapter=new JsonAdapter(this);
		MyThread thread1=new MyThread();
		thread1.start();
		setTitle("第三个页面");
		
	}
	
	
	public class MyThread extends Thread{
		@Override
		public void run() {
			String listString = "username=" + FirstActivity.username;
			//String codeString = "codenumber=" + codenumber+"&"+"username="+"test";
			Log.i("AAABBB", listString);
			String url = OrderHttpUtil.BASE_URL + OrderUrlUtil.PAYLIST_URL + listString;
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
			
			if("0".equals(res))
				m.what = OrderStringUtil.GETLIST_ERROR;
			else if("exception".equals(res))
				m.what=OrderStringUtil.SERVER_ERROR;		
			else
				m.what = OrderStringUtil.GETLIST_SUCCESS;
			
			proHandle.sendMessage(m);
		}
	}
	private Handler proHandle = new Handler(){
		@Override
		public void handleMessage(Message msg) {

			
			switch(msg.what){
			case OrderStringUtil.GETLIST_ERROR:
				Toast.makeText(getApplicationContext(), "没得到", 1000).show();
				break;
			case OrderStringUtil.SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "服务器错误", 1000).show();
				break;
			case OrderStringUtil.GETLIST_SUCCESS:	
				List<PurchaseBean> data=parseJson(res);
				adapter.setData(data);
				listView.setAdapter(adapter);
				break;		
			
			}
		}
	};
	
	private List<PurchaseBean> parseJson(String json){
		try {
			JSONArray jsonArray=new JSONArray(json);
			List<PurchaseBean> purchaseBeans=new ArrayList<PurchaseBean>();
			for(int i=0;i<jsonArray.length();i++){
				PurchaseBean purchaseBean=new PurchaseBean();
				
				String updateTime=jsonArray.getJSONObject(i).getString("updateTime");
				double price=jsonArray.getJSONObject(i).getDouble("price");
				purchaseBean.setPurchaseAmount(price);
				purchaseBean.setPurchaseTime(updateTime);
				purchaseBeans.add(purchaseBean);
				}
			return purchaseBeans;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
