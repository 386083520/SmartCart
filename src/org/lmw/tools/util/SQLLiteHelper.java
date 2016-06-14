package org.lmw.tools.util;

import org.lmw.tools.qr.bean.GoodsBean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLLiteHelper extends SQLiteOpenHelper{
	public static final String TB_NAME = "goods";
	public SQLLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " +
				TB_NAME + "(" +
				GoodsBean.ID + " integer primary key," +
				GoodsBean.GETID + " varchar," +
				GoodsBean.NAME + " varchar," + 
				GoodsBean.PRICE + " varchar,"+
				GoodsBean.NUMBER + " integer"+
				")");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		
		db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
		onCreate(db);
	}
}
