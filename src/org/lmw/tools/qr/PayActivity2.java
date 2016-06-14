package org.lmw.tools.qr;

import java.io.UnsupportedEncodingException;

import org.lmw.tools.util.OrderHttpUtil;
import org.lmw.tools.util.OrderStringUtil;
import org.lmw.tools.util.OrderUrlUtil;
import org.lmw.tools.util.SQLLiteHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PayActivity2 extends Activity {

	private TextView need_price;
	private String sumPrice;
	private String res;
	Button bt_cancel;
	Button bt_ok;
	private ProgressDialog prgDialog;
	public static boolean IsPayed=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		sumPrice = OrderStringUtil.getDataFromIntent(getIntent());
		need_price = (TextView) findViewById(R.id.need_price);
		sumPrice = OrderStringUtil.getDataFromIntent(getIntent());

		enterPassword();
	}

	private void enterPassword() {

		View view = View.inflate(this, R.layout.pay_ui, null);
		need_price = (TextView) view.findViewById(R.id.need_price);
		need_price.setText("需支付" + sumPrice);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		final AlertDialog dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		final EditText payEditText = (EditText) view
				.findViewById(R.id.payEditText);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				finish();
			}
		});
		bt_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String password = payEditText.getText().toString().trim();
				
				if (password.isEmpty()) {
					Toast.makeText(getApplicationContext(), "密码不能为空", 1000)
							.show();
				} else {
					dialog.dismiss();
					// 显示登陆对话框
					prgDialog = new ProgressDialog(PayActivity2.this);
					prgDialog.setIcon(R.drawable.progress);
					prgDialog.setTitle("请稍等");
					prgDialog.setMessage("正在支付，请稍等...");
					prgDialog.setCancelable(true);
					prgDialog.setIndeterminate(true);
					prgDialog.show();
					MyThread2 thread2 = new MyThread2(password);
					thread2.start();
				}
			}
		});

	}

	public class MyThread2 extends Thread {
		String password;

		public MyThread2(String password) {
			this.password = password;
		}

		@Override
		public void run() {
			String username = "username=" + FirstActivity.username;
			String payment_passwd = "payment_passwd=" + password;

			String url = OrderHttpUtil.BASE_URL + OrderUrlUtil.PAY_URL
					+ username + "&" + payment_passwd;
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
				Log.i("AAABBB", new String(res.getBytes(), "utf-8") + "======");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message m = new Message();

			if ("-1".equals(res))
				m.what = OrderStringUtil.PAY_ERROR;
			else if ("1".equals(res))
				m.what = OrderStringUtil.PAY_SUCCESS;
			else
				m.what = OrderStringUtil.SERVER_ERROR;

			proHandle.sendMessage(m);
		}
	}

	private Handler proHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					PayActivity2.this);

			prgDialog.dismiss();

			switch (msg.what) {
			case OrderStringUtil.PAY_ERROR:
				builder.setIcon(R.drawable.alert_error)
						.setTitle("错误")
						.setMessage("支付密码错误，请重新支付")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									// 点击确定按钮
									public void onClick(DialogInterface dialog,
											int which) {
										IsPayed=false;
										finish();
									}
								}).show();
				break;
			case OrderStringUtil.SERVER_ERROR:
				builder.setIcon(R.drawable.alert_error)
						.setTitle("错误")
						.setMessage("服务器错误，请重试")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									// 点击确定按钮
									public void onClick(DialogInterface dialog,
											int which) {
										IsPayed=false;
										finish();
									}
								}).show();
				break;
			case OrderStringUtil.PAY_SUCCESS:
				builder.setIcon(R.drawable.alert_ok)
						.setTitle("登陆成功")
						.setMessage("恭喜您，付款成功")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									// 点击确定按钮
									public void onClick(DialogInterface dialog,
											int which) {
										GoodsListActivity.db.delete(
												SQLLiteHelper.TB_NAME, null,
												null);
										IsPayed=true;
										finish();
									}
								}).show();
				break;

			}
		}
	};
}
