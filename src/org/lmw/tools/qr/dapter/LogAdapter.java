package org.lmw.tools.qr.dapter;

import java.util.List;

import org.lmw.tools.qr.R;
import org.lmw.tools.qr.common.LogEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LogAdapter extends BaseAdapter {
	LayoutInflater inflater;
	List<LogEntity> rs;
	int[] colors={R.color.holo_green_light,R.color.holo_blue_light,R.color.holo_orange_light,R.color.holo_red_light,R.color.holo_purple};
	public LogAdapter(Context c,List<LogEntity> rs) {
		this.rs=rs;
		inflater=LayoutInflater.from(c);
	}
	
	@Override
	public int getCount() {
		return rs.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View v, ViewGroup arg2) {
		v=inflater.inflate(R.layout.item_log, null);
		((TextView)v.findViewById(R.id.log_res)).setText(rs.get(position).getRes());
		((TextView)v.findViewById(R.id.log_time)).setText(rs.get(position).getTime());
		v.findViewById(R.id.head).setBackgroundResource(colors[position%colors.length]);
		return v;
	}

}
