package com.example.vmac.callCenter.view;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.vmac.callCenter.R;
import com.example.vmac.callCenter.rcms.CallInfoLevel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2018/1/5.
 */

public class LevelAdapter extends BaseAdapter {
    private Context context;
    private List<CallInfoLevel> dataList=new ArrayList<>();


    public  LevelAdapter(Context context){
        dataList.add(CallInfoLevel.Level1);
        dataList.add(CallInfoLevel.Level2);
        dataList.add(CallInfoLevel.Level3);
        this.context=context;
    }



    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position).level;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null)
        {
            LayoutInflater _LayoutInflater= LayoutInflater.from(context);
            convertView=_LayoutInflater.inflate(R.layout.callinfo_level_item, null);

            holder =new ViewHolder();
            convertView.setTag(holder);
            holder.level_title=(TextView)convertView.findViewById(R.id.call_info_level_item_title);
            holder.level_desc = (TextView)convertView.findViewById(R.id.call_info_level_item_desc);

        }  else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.level_title.setText(dataList.get(position).level);
        holder.level_desc.setText(dataList.get(position).desc);

        return convertView;
    }

    static class ViewHolder{
        TextView level_title;
        TextView level_desc;

    }

}
