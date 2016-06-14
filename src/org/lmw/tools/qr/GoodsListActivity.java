package org.lmw.tools.qr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.lmw.tools.qr.LoginBlueActivity.MyThread;
import org.lmw.tools.qr.bean.GoodsBean;
import org.lmw.tools.util.ChangeToHex;
import org.lmw.tools.util.InfoToHex;
import org.lmw.tools.util.OrderHttpUtil;
import org.lmw.tools.util.OrderStringUtil;
import org.lmw.tools.util.OrderUrlUtil;
import org.lmw.tools.util.SQLLiteHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GoodsListActivity extends Activity {
	private static String DB_NAME = "mygoods.db";
	private static int DB_VERSION = 1;
	private static int POSTION;
	private ListView listview;
	private Cursor cursor;
	private ProgressDialog prgDialog;
	String name;
	String price;
    String getid;
    String number;
	TextView sumPrice;
	public static SQLiteDatabase db;
	private SQLLiteHelper dbHelper;
	private ListAdapter listAdapter;
	private double sum=0.0;
	int addNum = 0;
	private double singleGoodsPrice;
	private List<GoodsBean> goodsList = new ArrayList<GoodsBean>();
	List<GoodsBean> goodsList2;
	
	private String res;
	OutputStream ops=null;
	InputStream ips=null;
	TextView tv;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goodslist_ui);
		tv=(TextView) findViewById(R.id.tv);
		getid=getIntent().getExtras().getString("getid");
		name = getIntent().getExtras().getString("name");
		
		price = getIntent().getExtras().getString("price");
		number=getIntent().getExtras().getString("number");
		Log.i("HHH", number);
		sumPrice=(TextView) findViewById(R.id.sumPrice);
		try {
			/* 初始化并创建数据库 */
			dbHelper = new SQLLiteHelper(this, DB_NAME, null, DB_VERSION);
			/* 创建表 */
			db = dbHelper.getWritableDatabase(); // 调用SQLiteHelper.OnCreate()
			insertorupdate();
			/* 查询表，得到cursor对象 */
			query();
		} catch (IllegalArgumentException e) {
			// 当用SimpleCursorAdapter装载数据时，表ID列必须是_id，否则报错column '_id' does not
			// exist
			e.printStackTrace();
			// 当版本变更时会调用SQLiteHelper.onUpgrade()方法重建表 注：表以前数据将丢失
			++DB_VERSION;
			dbHelper.onUpgrade(db, --DB_VERSION, DB_VERSION);
			// dbHelper.updateColumn(db, SQLiteHelper.ID, "_"+SQLiteHelper.ID,
			// "integer");
		}
		listview = (ListView) findViewById(R.id.listView);
		listAdapter = new ListAdapter();
		listview.setAdapter(listAdapter);
		sumPrice.setText("总价："+sum);

	}

	
   private void query(){
	   cursor = db.query(SQLLiteHelper.TB_NAME, null, null, null, null,
				null, GoodsBean.ID + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast() && (cursor.getString(2) != null)) {
			GoodsBean goods = new GoodsBean();
			
			goods.setId(cursor.getString(0));
			goods.setGetid(cursor.getString(1));
			goods.setName(cursor.getString(2));
			goods.setPrice(cursor.getString(3));
			goods.setNumber(cursor.getInt(4));
			singleGoodsPrice=Double.parseDouble(cursor.getString(3))*cursor.getInt(4);
			sum=sum+singleGoodsPrice;
			goodsList.add(goods);
			cursor.moveToNext();
		}
   }
	private void insertorupdate() {
		//int addNum = 0;
		 String args[] = {name};
		Cursor c = db.rawQuery("SELECT * FROM "+SQLLiteHelper.TB_NAME+" WHERE name=?", args); 
		if(c.getCount()==0){
		ContentValues values = new ContentValues();
		values.put(GoodsBean.GETID, getid);
		values.put(GoodsBean.NAME, name);
		values.put(GoodsBean.PRICE, price);
		values.put(GoodsBean.NUMBER, Integer.parseInt(number));
		// 插入数据 用ContentValues对象也即HashMap操作,并返回ID号
		Long goodsID = db.insert(SQLLiteHelper.TB_NAME, GoodsBean.ID, values);
		/*
		 * GoodsBean goods = new GoodsBean(); goods.setId(""+goodsID);
		 * goods.setName(name); goods.setPrice(price); goods.setNumber(1);
		 * goodsList.add(goods); listview.setAdapter(new ListAdapter());
		 */
		}else{
			c.moveToFirst();
			while (!c.isAfterLast() && (c.getString(2) != null)) {
			int nameColumnIndex = c.getColumnIndex(GoodsBean.NUMBER);
			int num = c.getInt(nameColumnIndex);
			addNum=num+Integer.parseInt(number);
			c.moveToNext();
			}
			String sql = "update "+SQLLiteHelper.TB_NAME+" set "+GoodsBean.NUMBER+" = "+addNum+" where name= '"+name+"'";//修改的SQL语句
			db.execSQL(sql);//执行修改

		}

	}
	

	/*
	 * @Override protected void onDestroy() { db.delete(SQLLiteHelper.TB_NAME,
	 * null, null); super.onDestroy(); }
	 */

	private class ListAdapter extends BaseAdapter {
		public ListAdapter() {
			super();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return goodsList.size();
		}

		@Override
		public Object getItem(int postion) {
			// TODO Auto-generated method stub
			return postion;
		}

		@Override
		public long getItemId(int postion) {
			// TODO Auto-generated method stub
			return postion;
		}

		@Override
		public View getView(final int postion, View view, ViewGroup parent) {
			view = getLayoutInflater().inflate(R.layout.listview, null);
			 int[] colors = { Color.rgb(153,204,51), Color.rgb(219, 238, 244) };//RGB颜色  
			 
			view.setBackgroundColor(colors[postion % 2]);
			TextView tvName = (TextView) view.findViewById(R.id.tvName);
			tvName.setText("" + goodsList.get(postion).getName());
			TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);
			tvPrice.setText("" + goodsList.get(postion).getPrice());
			TextView tvNumber = (TextView) view.findViewById(R.id.tvNumber);
			tvNumber.setText("" + goodsList.get(postion).getNumber());
			TextView bu = (TextView) view.findViewById(R.id.btRemove);
			bu.setText("删除");
			bu.setId(Integer.parseInt(goodsList.get(postion).getId()));

			/* 删除表数据 */
			bu.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View view) {
					try {
						/*if(addNum==1){*/
						int goodsNum;
						goodsNum=goodsList.get(postion).getNumber();
						double goodsPrice;
						goodsPrice=Double.parseDouble(goodsList.get(postion).getPrice());
						sum=sum-goodsNum*goodsPrice;
						sumPrice.setText(sum+"");
						db.delete(SQLLiteHelper.TB_NAME, GoodsBean.ID + "="
								+ view.getId(), null);
						goodsList.remove(postion);
						listview.setAdapter(new ListAdapter());
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			return view;
		}
	}
	public void pay(View v){
		
		cursor = db.query(SQLLiteHelper.TB_NAME, null, null, null, null,
				null, GoodsBean.ID + " DESC");
		goodsList2 = new ArrayList<GoodsBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast() && (cursor.getString(2) != null)) {
			GoodsBean goods = new GoodsBean();
			
			
			goods.setGetid(cursor.getString(1));
			
			goods.setNumber(cursor.getInt(4));
			
			goodsList2.add(goods);
			
			cursor.moveToNext();
		}
		if(!goodsList2.isEmpty()){
		updateByWifi();
		}else{
			Toast.makeText(getApplicationContext(), "你已结算成功！", 1000).show();
		}
	}


	private void updateByWifi() {
		Message m = new Message();
		// 显示登陆对话框
		prgDialog = new ProgressDialog(GoodsListActivity.this);
		prgDialog.setIcon(R.drawable.progress);
		prgDialog.setTitle("请稍等");
		prgDialog.setMessage("正在连接，请稍等...");
		prgDialog.setCancelable(true);
		prgDialog.setIndeterminate(true);
		prgDialog.show();
		GoodsListThread thread2=new GoodsListThread();
		thread2.start();
		
	}
	
	public class GoodsListThread extends Thread{
		
		String goodsInfo="";
		
		@Override
		public void run() {
			
			    for(int i=0;i<goodsList2.size();i++){
			    	GoodsBean goods=goodsList2.get(i);
			    	goodsInfo=goodsInfo+goods.getGetid()+",";
			    	goodsInfo=goodsInfo+goods.getNumber()+",";
			    }
			    goodsInfo=goodsInfo.substring(0, goodsInfo.length()-1);
				String goodsString="goodsInfo="+FirstActivity.username+","+"01,"+goodsInfo;
				String url = OrderHttpUtil.BASE_URL + OrderUrlUtil.GOODSLIST_URL + goodsString;
				try {
					res = OrderHttpUtil.getHttpPostResultForUrl(url);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message m = new Message();
				String a=res.substring(0, 1);
				Log.i("AAABBB", res);
				Log.i("AAABB", a);
				if("0".equals(a))
					m.what = OrderStringUtil.GETPRICEERROR;
				else if("exception".equals(res))
					m.what=OrderStringUtil.SERVER_ERROR;
				else
					m.what=OrderStringUtil.GETPRICESUCCESS;
				proHandle.sendMessage(m);
			
		}
	}
	
	private Handler proHandle = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			AlertDialog.Builder builder = new AlertDialog.Builder(GoodsListActivity.this);
			prgDialog.dismiss();
			switch(msg.what){
			case OrderStringUtil.GETPRICEERROR:
				builder.setIcon(R.drawable.alert_error)
				.setTitle("错误")
				.setMessage("数据返回异常，请重试！")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 点击确定按钮
					public void onClick(DialogInterface dialog, int which) {									
					}
				}).show();
				break;
			
			
			
			case OrderStringUtil.SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "服务器错误", 1000).show();
				break;
			case OrderStringUtil.GETPRICESUCCESS:			
				String[] getRes=res.split(",");
				String username=getRes[0];
				int cartId=Integer.parseInt(getRes[1]);
				int billId=Integer.parseInt(getRes[2]);
				final float sumPrice=Float.parseFloat(getRes[3]);
				float remainerPrice=Float.parseFloat(getRes[4]);
				int reward=Integer.parseInt(getRes[5]);
				
				
				builder.setIcon(R.drawable.alert_ok)
				.setTitle("发送成功")
				.setMessage("点击确定跳转到结算界面")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 点击确定按钮
					public void onClick(DialogInterface dialog, int which) {
						
						
						Intent it=new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("data",sumPrice+"");
						it.putExtra("data", bundle);
						it.setClass(GoodsListActivity.this,PayActivity2.class);	
						startActivity(it);
					}
				}).show();
				break;		
			
			}
		}
	};
	
	
}
