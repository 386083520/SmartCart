package org.lmw.tools.qr.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.lmw.tools.qr.R;

import android.app.Application;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class App extends Application {
	public static String scan_pic_dir;
	public static List<LogEntity> rs = new ArrayList<LogEntity>();
	public static SharedPreferences sp;
	public static Editor editor;
	public static Gson gson;

	@Override
	public void onCreate() {
		super.onCreate();
		scan_pic_dir = getResources().getString(R.string.scan_pic_dir);
		createTempDir();
		gson = new Gson();
		sp = getSharedPreferences("qrdata", MODE_PRIVATE);
		editor = sp.edit();

		readFromSp();
	}

	/**
	 * 
	 * 此方法描述的是：向sp中写入数据
	 * 
	 * @author: limengwei
	 * @version: 2014-7-1 下午4:19:40
	 */
	public static void witeToSp() {
		editor.putString("logdata", gson.toJson(rs));
		editor.commit();
	}

	/**
	 * 
	 * 此方法描述的是：从sp中读取数据
	 * 
	 * @author: limengwei
	 * @version: 2014-7-1 下午4:20:01
	 */
	public void readFromSp() {
		Type type = new TypeToken<List<LogEntity>>() {
		}.getType();
		List<LogEntity> list = gson.fromJson(sp.getString("logdata", ""), type);
		if (list != null) {
			rs.clear();
			rs.addAll(list);
		}
	}

	/**
	 * 
	 * 此方法描述的是：创建缓存文件架
	 * 
	 * @author: limengwei
	 * @version: 2014-7-1 上午8:51:25
	 */
	public void createTempDir() {
		File dir = new File(scan_pic_dir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		scan_pic_dir = dir.getPath();
	}

	/**
	 * 
	 * 此方法描述的是： 保存Bitmap到SD卡
	 * 
	 * @author: limengwei
	 * @version: 2014-7-1 上午8:53:51
	 */
	public static String saveBitmap(Bitmap bitmap) {

		File file = new File(App.scan_pic_dir, "scan_pic_" + System.currentTimeMillis() + ".png");
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
				out.flush();
				out.close();
				return file.getPath();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 
	 * 此方法描述的是： 复制内容到剪贴板
	 * 
	 * @author: limengwei
	 * @version: 2014-7-1 上午9:19:50
	 */
	@SuppressWarnings("deprecation")
	public static void copy(String content, Context context) {
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
		Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
	}

}
