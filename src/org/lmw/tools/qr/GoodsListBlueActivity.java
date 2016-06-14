package org.lmw.tools.qr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.lmw.tools.qr.LoginBlueActivity.MyThread;
import org.lmw.tools.qr.bean.GoodsBean;
import org.lmw.tools.util.ChangeToHex;
import org.lmw.tools.util.InfoToHex;
import org.lmw.tools.util.OrderStringUtil;
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

public class GoodsListBlueActivity extends Activity {
	private static String DB_NAME = "mygoods.db";
	private static int DB_VERSION = 1;
	private static int POSTION;
	private ListView listview;
	private Cursor cursor;
	private ProgressDialog prgDialog;
	String name;
	String price;
    String getid;
	TextView sumPrice;
	private SQLiteDatabase db;
	private SQLLiteHelper dbHelper;
	private ListAdapter listAdapter;
	private double sum=0.0;
	int addNum = 0;
	private double singleGoodsPrice;
	private List<GoodsBean> goodsList = new ArrayList<GoodsBean>();
	List<GoodsBean> goodsList2;
	private List<Integer> mBuffer;
	private String res;
	OutputStream ops=null;
	InputStream ips=null;
	TextView tv;
	BluetoothSocket socket=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goodslist_ui);
		tv=(TextView) findViewById(R.id.tv);
		getid=getIntent().getExtras().getString("getid");
		name = getIntent().getExtras().getString("name");
		price = getIntent().getExtras().getString("price");
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
		sumPrice.setText(sum+"");

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
		values.put(GoodsBean.NUMBER, 1);
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
			addNum=num+1;
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
						
						/*}else{
							addNum=addNum-1;
							String sql = "update "+SQLLiteHelper.TB_NAME+" set "+GoodsBean.NUMBER+" = "+addNum+" where _id= '"+view.getId()+"'";//修改的SQL语句
							db.execSQL(sql);//执行修改
							goodsList.clear();
							query();
							listAdapter = new ListAdapter();
							listview.setAdapter(listAdapter);
							
						}*/
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
		updateByBlue();
		}else{
			Toast.makeText(getApplicationContext(), "你已结算成功！", 1000).show();
		}
	}


	private void updateByBlue() {
		Message m = new Message();
		// 显示登陆对话框
		prgDialog = new ProgressDialog(GoodsListBlueActivity.this);
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
		
		
		
		@Override
		public void run() {
			
			try {
				
				byte[] nameArr=InfoToHex.userNameToHex(FirstActivity.username);
				byte[] numberArr=InfoToHex.numberToHex(2);
				byte[] goodsArr=InfoToHex.goodsListToHex(goodsList2);
				
				byte[] arr=new byte[38];
				arr[0]=(byte) 0xee;
				
				for(int i=1;i<6;i++){
					arr[i]=nameArr[i-1];
				}
				arr[6]=numberArr[0];
				for(int i=7;i<37;i++){
					arr[i]=goodsArr[i-7];
				}
				arr[37]=(byte) 0xff;
				
				
					
					
						
							 try{
					            	socket = MethodChoiceActivity._device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(MethodChoiceActivity.MY_UUID));
					            	
					            }catch(IOException e){
					            	Message m1=new Message();
					            	m1.what=OrderStringUtil.BLUESOCKET_IS_GET;
					            	 proHandle.sendMessage(m1);
					            }
							 socket.connect();
							 Message m2=new Message();
							 m2.what=OrderStringUtil.BLUESOCKET_IS_CONNECTED;
							 proHandle.sendMessage(m2);
							 ops=socket.getOutputStream();
							 Message m3=new Message();
							 m3.what=OrderStringUtil.OPS;
							 proHandle.sendMessage(m3);
							ops.write(arr);
						
							/*try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
					
						mBuffer = new ArrayList<Integer>();
						/*try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						byte[] buffer = new byte[1024];
						int bytes;
						StringBuffer buf = new StringBuffer();
						/* try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
						
							//ips=MethodChoiceActivity.blueSocket.getInputStream();
							ips=socket.getInputStream();
							
							Message m4=new Message();
							m4.what=OrderStringUtil.IPS;
							 proHandle.sendMessage(m4);
							/* try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}*/
							bytes=ips.read(buffer);
							if((buffer[bytes-1]&0xFF)!=255){
								bytes+=ips.read(buffer,bytes,1024-bytes);
							}
							/*try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							Log.i("AAABBBB", bytes+"---bytes");
							synchronized (mBuffer) {
								for (int i = 0; i < bytes; i++) {
									mBuffer.add(buffer[i] & 0xFF);
								}
								Log.i("AAABBBB", mBuffer+"---mBuffer");
								for (int i : mBuffer) {
									buf.append(ChangeToHex.intToHexString(i, 1));
									
								}
								Log.i("AAABBBB", buf+"---buf");
							}
							
							
							res=buf.toString();
						
						
						Log.i("AAABBB", res);
						Message m5=new Message();
						if("".equals(res))
							m5.what = OrderStringUtil.ERROR;
						else if(!socket.isConnected()){
							m5.what=OrderStringUtil.BLUESOCKET_NOT_CONNECTED; 
						 }
						
						else if("exception".equals(res))
							m5.what=OrderStringUtil.SERVER_ERROR;
						else
							m5.what=OrderStringUtil.OK;
						proHandle.sendMessage(m5);
			} catch (IOException e) {
				Message m6=new Message();
				m6.what=OrderStringUtil.BLUESOCKET_NOT_CONNECTED;
				proHandle.sendMessage(m6);
				e.printStackTrace();
				
			}finally{
				
				if(ips!=null){
					try {
						ips.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
					if(ops!=null){
					try {
						ops.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
					if(socket!=null){
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					socket=null;
					}
			}
			
		}
	}
	
	private Handler proHandle = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			AlertDialog.Builder builder = new AlertDialog.Builder(GoodsListBlueActivity.this);
			prgDialog.dismiss();
			switch(msg.what){
			case OrderStringUtil.ERROR:
				builder.setIcon(R.drawable.alert_error)
				.setTitle("错误")
				.setMessage("数据返回异常，请重试！")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 点击确定按钮
					public void onClick(DialogInterface dialog, int which) {									
					}
				}).show();
				break;
			case OrderStringUtil.BLUESOCKET_IS_GET:
				Toast.makeText(getApplicationContext(), "已获取到socket对象", Toast.LENGTH_SHORT).show(); 
				break;
			case OrderStringUtil.BLUESOCKET_IS_CONNECTED:
				Toast.makeText(getApplicationContext(), "已与购物车连接", Toast.LENGTH_SHORT).show(); 
				break;
			case OrderStringUtil.OPS:
				Toast.makeText(getApplicationContext(), "输出管道开启", Toast.LENGTH_SHORT).show(); 
				break;
			case OrderStringUtil.IPS:
				Toast.makeText(getApplicationContext(), "输入管道开启", Toast.LENGTH_SHORT).show(); 
				break;
			case OrderStringUtil.BLUESOCKET_NOT_CONNECTED:
				builder.setIcon(R.drawable.alert_error)
				.setTitle("错误")
				.setMessage("购物车连接异常，请重试！")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 点击确定按钮
					public void onClick(DialogInterface dialog, int which) {									
					}
				}).show();
				break;
			
			case OrderStringUtil.SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "服务器错误", 1000).show();
				break;
			case OrderStringUtil.OK:			
				
				String username=ChangeToHex.hexToString(res.substring(2, 12));
				
				int cartId=Integer.parseInt(res.substring(12, 14), 16);
				int billId=Integer.parseInt(res.substring(14, 18), 16);
				int sumPriceInt=Integer.parseInt(res.substring(18, 22), 16);
				int sumPricePoint=Integer.parseInt(res.substring(22, 24), 16);
				String sumPriceString=sumPriceInt+"."+sumPricePoint;
				float sumPrice=Float.parseFloat(sumPriceString);
				int remainerPriceInt=Integer.parseInt(res.substring(24, 28), 16);
				int remainerPricePoint=Integer.parseInt(res.substring(28, 30), 16);
				String remainerPriceString=remainerPriceInt+"."+remainerPricePoint;
				float remainerPrice=Float.parseFloat(remainerPriceString);
				FirstActivity.remainerPrice=remainerPrice;
				int reward=Integer.parseInt(res.substring(30, 34), 16);
				Toast.makeText(getApplicationContext(), username+cartId+billId+sumPrice+remainerPrice+reward, 1000).show();
				tv.setText(username+","+cartId+","+billId+","+sumPrice+","+remainerPrice+","+reward);
				//tv.setText(res);
				
				builder.setIcon(R.drawable.alert_ok)
				.setTitle("发送成功")
				.setMessage("点击确定跳转到结算界面")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 点击确定按钮
					public void onClick(DialogInterface dialog, int which) {
						
						db.delete(SQLLiteHelper.TB_NAME, null, null);
						if(ips!=null){
							try {
								ips.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							}
							if(ops!=null){
							try {
								ops.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							}
							if(socket!=null){
							try {
								socket.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							socket=null;
							}
						
					}
				}).show();
				break;		
			
			}
		}
	};
	/*protected void onDestroy() {
		try {
			if(ips!=null){
			ips.close();
			}
			if(ops!=null){
			ops.close();
			}
			if(socket!=null){
			socket.close();
			socket=null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};*/
	
}
