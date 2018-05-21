package com.example.vmac.callCenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.vmac.callCenter.watson.conversation.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/16.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private String TAG=CallCenterChatAdapter.class.getSimpleName();
    private List<String> playList;
    private String intent;
    private Activity activity;
    public String getIntent() {
        return intent;
    }

    private int SELF = 100;


    public static  final String debug="debug";
    public static final  String isChoise="Choise";
    public Handler handler;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private ArrayList<Message> messageArrayList;

    private Context appContext;

    private OnClickCallBack callBack;
    private Map<String,Object> conversationContext;


    public void setConversationContext(Map<String, Object> conversationContext) {
        this.conversationContext = conversationContext;
    }

    public MessageAdapter(ArrayList<Message> messageArrayList, OnClickCallBack listener, Context context, Activity activity) {
        this.messageArrayList=messageArrayList;
        callBack=listener;
        this.appContext=context;
        this.activity =activity;
    }

    public void setPlayList(List<String> playList) {
        this.playList = playList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        Log.i(TAG,"***onCreateViewHolder viewType"+viewType);
        if (viewType == Constant.customType) {
            // self message 我的对话内容
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);

        }
        else  if(viewType==Constant.show_bt_radio_intent){ //功能框

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_fucntion_choise_big, parent, false);
            View v_call = itemView.findViewById(R.id.chat_item_functon_call);

            View create_call = itemView.findViewById(R.id.chat_item_functon_create_call);

            View v_repair= itemView.findViewById(R.id.chat_item_functon_repair);
            View v_local= itemView.findViewById(R.id.chat_item_functon_local);
            View v_part = itemView.findViewById(R.id.chat_item_functon_part);
            View v_src=itemView.findViewById(R.id.chat_item_functon_src) ;
            v_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message m= new Message();
                    m.msgType=Constant.customType;
                    m.message = "工单查询";
                    messageArrayList.add(m);
                    callBack.OnCallBack(1,"工单查询");

                }
            });
            create_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message m= new Message();
                    m.msgType=Constant.customType;
                    m.message = "我要开call ";
                    messageArrayList.add(m);
                    callBack.OnCallBack(Constant.callBack_add_callinfo,"我要开call ");

                }
            });

            v_repair.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Message m= new Message();
                    m.msgType=Constant.customType;
                    m.message = "维保查询";
                    messageArrayList.add(m);
                    callBack.OnCallBack(1,"维保查询");
                }
            });

            v_local.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message m= new Message();
                    m.msgType=Constant.customType;
                    m.message = "位置查询";
                    messageArrayList.add(m);
                    callBack.OnCallBack(1,"位置查询");
                }
            });

            v_part.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message m= new Message();
                    m.msgType=Constant.customType;
                    m.message = "备件查询";
                    messageArrayList.add(m);
                    callBack.OnCallBack(1,"备件查询");
                }
            });

            v_src.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message m= new Message();
                    m.msgType=Constant.customType;
                    m.message = "机器故障代码查询";
                    messageArrayList.add(m);
                    callBack.OnCallBack(1,"机器故障代码查询");
                }
            });
        }else  if(viewType==Constant.chat_item_src_img) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_web_img, parent, false);

        }



        else  if(viewType==Constant.chat_item_show_ratingbar) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_ratingbar, parent, false);

        }


        else {

            // WatBot message watSon 对话的内容
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_watson, parent, false);
            TextView textView =(TextView) itemView.findViewById(R.id.chat_item_watson_message);
            itemView .setOnClickListener(this);
            AppCompatImageView imageView = (AppCompatImageView) itemView.findViewById(R.id.appCompatImageView);
            textView.setOnClickListener(this);
        }



        return new MessageAdapter.ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        return  message.msgType;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,final int position) {
        Log.i(TAG,"*** onBindViewHolder position "+position);
        final Message message = messageArrayList.get(position);
        final View itemView = holder.itemView;
        if(message.msgType == Constant.customType){
          TextView textView = (TextView) itemView.findViewById(R.id.chat_item_custom_message);
          textView.setText(message.message);
        }else if(message.msgType==Constant.watsonType){
            TextView textView = (TextView) itemView.findViewById(R.id.chat_item_watson_message);
            textView.setText(message.message);
        }else if(message.msgType==Constant.chat_item_src_img){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.OnCallBack(Constant.callBack_start_web,message.message);
                }
            });
        }else if(message.msgType == Constant.chat_item_show_ratingbar){


            View view_good=   itemView.findViewById(R.id.bt_rating_very_good);
            view_good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //  Toast.makeText(appContext," 非常好 ",Toast.LENGTH_SHORT).show();
                    choiseEvaluate(" 非常好 ",position);

                }
            });

            View good=   itemView.findViewById(R.id.bt_rating_good);
            good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(appContext," 满意 ",Toast.LENGTH_SHORT).show();
                    choiseEvaluate(" 满意 ",position);
                }
            });
            View bad=   itemView.findViewById(R.id.bt_rating_bad);
            bad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //  Toast.makeText(appContext," 不好 ",Toast.LENGTH_SHORT).show();
                    choiseEvaluate(" 不好 ",position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    private void choiseEvaluate(String evaluate,int position){
        messageArrayList.remove(position);

        Message rmsg =new Message();
        rmsg.msgType = Constant.customType;
        rmsg.setMessage(evaluate);
        messageArrayList.add(rmsg);
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public String link;
        public TextView txMessage;


        public ViewHolder(View view) {
            super(view);
//            message = (TextView) itemView.findViewById(R.id.message);
        }
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG,"***点击事件");
        switch (v.getId()){
            case R.id.chat_item_watson_message:
                String text =((TextView)v).getText().toString();
                Log.i(TAG,"播放文本 "+text);
                callBack.OnCallBack(Constant.start_speeker,text);
                break;
        }

    }

}
