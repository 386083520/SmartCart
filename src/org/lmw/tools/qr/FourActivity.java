package org.lmw.tools.qr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class FourActivity extends Activity{
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.four);
			setTitle("第四个页面");
		}
		public void jumpToUnfinished(View v){
			if(GoodsListActivity.db==null){
				Toast.makeText(getApplicationContext(), "您没有未消费订单", 1000).show();
			}else{
				Intent intent=new Intent(FourActivity.this,GoodsListActivity.class);
				startActivity(intent);
				finish();
			}
		}
}
