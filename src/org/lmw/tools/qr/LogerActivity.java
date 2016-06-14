package org.lmw.tools.qr;

import org.lmw.tools.qr.common.App;
import org.lmw.tools.qr.common.BaseActivity;
import org.lmw.tools.qr.dapter.LogAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.widget.swipedismiss.SwipeDismissListViewTouchListener;

public class LogerActivity extends BaseActivity implements OnClickListener,OnItemClickListener{
	ListView lv;
	LogAdapter mAdapter;
	SwipeDismissListViewTouchListener touchListener;
	ImageButton about;
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.act_loger);
	initView();
}

	public void initView(){
		about=(ImageButton)findViewById(R.id.about);
		about.setOnClickListener(this);
		
		lv=(ListView)findViewById(R.id.loglistview);
        mAdapter = new LogAdapter(this,App.rs);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);
        touchListener =new SwipeDismissListViewTouchListener(lv,new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                	App.rs.remove(position);
                                	App.witeToSp();
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
        lv.setOnTouchListener(touchListener);
        lv.setOnScrollListener(touchListener.makeScrollListener());
	}

	@Override
	public void onClick(View v) {
		if(v==about){
			startActivity(new Intent(getApplicationContext(), AboutActivity.class));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		Intent it = new Intent(getApplicationContext(), ResultActivity.class);
		it.putExtra("scan_res", App.rs.get(arg2).getRes());
		it.putExtra("scan_pic", App.rs.get(arg2).getPic());
		startActivity(it);
		
	}
	
	
}
