package com.jmhy.sdk.adapter;

import java.util.List;

import com.jmhy.sdk.config.AppConfig;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by on 2015/12/20.
 */
public class UserAdapter extends BaseAdapter{
	private Context context;
	private List<String> list;
	private InnerItemOnclickListener mListener;  
	public UserAdapter(Context context, List<String> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Item item;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					AppConfig.resourceId(context, "jmitemcountlist", "layout"),
					null);
			item = new Item();
			item.user = (TextView) convertView.findViewById(AppConfig
					.resourceId(context, "TextView", "id"));
            item.cancel = (ImageView) convertView.findViewById(AppConfig.resourceId(context, "ibcancel", "id"));
			convertView.setTag(item);
		} else {
			item = (Item) convertView.getTag();
		}
		item.user.setText(list.get(position));
		item.cancel.setTag(position);
		item.cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mListener.itemClick(Integer.parseInt(v.getTag().toString()));
			}
		});
		return convertView;
	}

	class Item {

		private TextView user;
		private ImageView cancel;
	}

	 public interface InnerItemOnclickListener {  
	        void itemClick(int position);  
	    }  
	  
	    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener){  
	        this.mListener=listener;  
	    } 
}
