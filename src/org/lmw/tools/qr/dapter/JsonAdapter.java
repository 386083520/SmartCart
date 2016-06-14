package org.lmw.tools.qr.dapter;

import java.util.List;

import org.lmw.tools.qr.R;

import org.lmw.tools.qr.bean.PurchaseBean;








import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class JsonAdapter extends BaseAdapter{
private List<PurchaseBean> list;
private Context context;
private LayoutInflater inflater;


public JsonAdapter(Context context,List<PurchaseBean> list) {
	this.list=list;
	this.context=context;
	inflater=LayoutInflater.from(context);
}
public JsonAdapter(Context context) {
	this.context=context;	
	inflater=LayoutInflater.from(context);
}

public void setData(List<PurchaseBean> purchaseBeans){
	this.list=purchaseBeans;
}
	@Override
	public int getCount() {
		
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		
		return list.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		Holder holder=null;
		if(convertView==null){
			convertView=inflater.inflate(R.layout.item_purchase, null);
			
			holder=new Holder(convertView);
			convertView.setTag(holder);
			
		}else{
			holder=(Holder) convertView.getTag();
		}
		PurchaseBean purchaseBean=list.get(position);
		holder.purchase_time_tv.setText(purchaseBean.getPurchaseTime());
		holder.purchase_amount_tv.setText(purchaseBean.getPurchaseAmount()+"");
		return convertView;
	}
	
	class Holder{
		private TextView purchase_time_tv;
		private TextView purchase_amount_tv;
		public Holder(View view) {
			purchase_time_tv=(TextView) view.findViewById(R.id.purchase_time_tv);
			purchase_amount_tv=(TextView) view.findViewById(R.id.purchase_amount_tv);
		}
	}

}
