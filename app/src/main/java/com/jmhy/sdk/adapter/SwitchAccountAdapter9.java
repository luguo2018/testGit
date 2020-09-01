package com.jmhy.sdk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jmhy.sdk.config.AppConfig;

import java.util.List;

/**
 * Created by on 2015/12/20.
 */
public class SwitchAccountAdapter9 extends BaseAdapter {
    private Context context;
    private List<String> accountlist,timeList,typeList;
    private InnerItemOnclickListener mListener;
    private boolean showDelete;

    public SwitchAccountAdapter9(Context context, List<String> accountlist,List<String> timeList,List<String> typeList) {
        this.context = context;
        this.accountlist = accountlist;
        this.timeList = timeList;
        this.typeList = typeList;
    }

    @Override
    public int getCount() {
        return accountlist.size();
    }

    @Override
    public String getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Item item;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(AppConfig.resourceId(context, "jm_swtich_account_item_9", "layout"), null);
            item = new Item();
            item.account = (TextView) convertView.findViewById(AppConfig.resourceId(context, "account", "id"));
            item.login_type = (TextView) convertView.findViewById(AppConfig.resourceId(context, "login_type", "id"));
            item.delete = (TextView) convertView.findViewById(AppConfig.resourceId(context, "delete", "id"));
            item.last_use = (TextView) convertView.findViewById(AppConfig.resourceId(context, "last_use", "id"));
            item.login_time = (TextView) convertView.findViewById(AppConfig.resourceId(context, "login_time", "id"));
//            item.cancel = (ImageView) convertView.findViewById(AppConfig.resourceId(context, "ibcancel", "id"));
            convertView.setTag(item);
        } else {
            item = (Item) convertView.getTag();
        }
        item.account.setText(accountlist.get(position));

        if (timeList.size() != 0 && typeList.size() != 0) {
            if (timeList.get(position) != null && typeList.get(position) != null) {
                item.login_time.setText(timeList.get(position));
                item.login_type.setText(typeList.get(position));
            }
        }
        if (AppConfig.skin9_switch_showDelete){
            item.delete.setVisibility(View.VISIBLE);
            item.delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.itemClick(position);
                }
            });
            item.last_use.setVisibility(View.GONE);
        }else{
            if (position==0){
                item.last_use.setVisibility(View.VISIBLE);
            }else{
                item.last_use.setVisibility(View.INVISIBLE);
            }
        }


//        item.cancel.setTag(position);
//        item.cancel.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                mListener.itemClick(Integer.parseInt(v.getTag().toString()));
//            }
//        });
        return convertView;
    }

    private class Item {

        private TextView account;
        private TextView login_type;
        private TextView last_use;
        private TextView login_time;
        private TextView delete;
//        private ImageView cancel;
    }

    public interface InnerItemOnclickListener {
        void itemClick(int position);
    }

    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener) {
        this.mListener = listener;
    }

}
