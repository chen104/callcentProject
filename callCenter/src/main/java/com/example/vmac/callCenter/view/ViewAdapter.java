package com.example.vmac.callCenter.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vmac.callCenter.R;

import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */

public class ViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<String> dataList;
    public ViewAdapter(List<String> dataList){
        this.dataList=dataList;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item_watson, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((ViewHolder)holder).message.setText("   "+position+"  "+dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.chat_item_watson_message);


        }
    }
}
