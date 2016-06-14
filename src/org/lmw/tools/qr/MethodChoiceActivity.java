package org.lmw.tools.qr;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MethodChoiceActivity extends Activity{
	private BluetoothAdapter _bluetooth;//获取本地蓝牙适配器，即蓝牙设备
	private WifiManager wifiManager;
	private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句柄
	public final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
	private InputStream is;    //输入流，用来接收蓝牙数据
	public static BluetoothSocket blueSocket;      //蓝牙通信socket
	Button buletooch_method;
	public static BluetoothDevice _device = null;     //蓝牙设备
	boolean bThread = false;
@Override
public void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.method_choice_ui);
	buletooch_method=(Button) findViewById(R.id.buletooch_method);
	
    
   
}
public void buletoochMethod(View v){
	_bluetooth = BluetoothAdapter.getDefaultAdapter();
	
	
	// 设置设备可以被搜索  
    new Thread(){
 	   public void run(){
 		   if(_bluetooth.isEnabled()==false){
     		_bluetooth.enable();
     		
 		   }
 	   }   	   
    }.start(); 
	
	 //如果打开本地蓝牙设备不成功，提示信息，结束程序
    if (_bluetooth == null){
    	Toast.makeText(this, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG).show();
        finish();
        return;
    }
	
    
    //如未连接设备则打开DeviceListActivity进行设备搜索
	if(blueSocket==null){
		Toast.makeText(this, " 请选择设备...", Toast.LENGTH_LONG).show();
		Intent serverIntent = new Intent(this, DeviceListActivity.class); //跳转程序设置
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
	}
	else{
		 //关闭连接socket
	    try{
	    	Toast.makeText(this, " 正在断开设备连接...", Toast.LENGTH_LONG).show();
	    	
	    	blueSocket.close();
	    	blueSocket = null;
	    	
	    	buletooch_method.setText("蓝牙");
	    }catch(IOException e){}   
	}
	return;
}
public void internetMethod(View v){
	wifiManager=(WifiManager) this.getSystemService(Context.WIFI_SERVICE);
	if(!wifiManager.isWifiEnabled()){
		wifiManager.setWifiEnabled(true);
		Toast.makeText(getApplicationContext(), "正在开启，请稍等...", 1000).show();
	}else{
		Toast.makeText(getApplicationContext(), "wifi已开启,请勿重复点击", 1000).show();
	}
	if(wifiManager.isWifiEnabled()){
		Intent intent=new Intent(MethodChoiceActivity.this, LoginActivity.class);
		startActivity(intent);
	}
}

//接收活动结果，响应startActivityForResult()
public void onActivityResult(int requestCode, int resultCode, Intent data) {
	switch(requestCode){
	case REQUEST_CONNECT_DEVICE:     //连接结果，由DeviceListActivity设置返回
		// 响应返回结果
        if (resultCode == Activity.RESULT_OK) {   //连接成功，由DeviceListActivity设置返回
            // MAC地址，由DeviceListActivity设置返回
            String address = data.getExtras()
                                 .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            // 得到蓝牙设备句柄      
            _device = _bluetooth.getRemoteDevice(address);

            /*// 用服务号得到socket
            try{
            	blueSocket = _device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
            	
            }catch(IOException e){
            	Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
            }*/
            //连接socket
        	
            try{
            	//blueSocket.connect();
            	Toast.makeText(this, "连接"+_device.getName()+"成功！", Toast.LENGTH_SHORT).show();
            	
            	Intent intent=new Intent();
            	intent.setClass(MethodChoiceActivity.this, LoginActivity.class);
            	
            	
            	startActivity(intent);
            }catch(Exception e){
            	try{
            		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
            		blueSocket.close();
            		blueSocket = null;
            	}catch(IOException ee){
            		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
            	}
            	
            	return;
            }
            
        }
		break;
	default:break;
	}
}
@Override
protected void onDestroy() {
	super.onDestroy();
	try {
		if(blueSocket!=null){
		blueSocket.close();
		blueSocket=null;
		}
		if(_bluetooth.enable()){
			_bluetooth.disable();
			}
	} catch (Exception e) {
		
		e.printStackTrace();
	}
	
}




}
