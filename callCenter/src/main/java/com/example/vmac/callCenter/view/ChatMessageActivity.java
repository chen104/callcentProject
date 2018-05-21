package com.example.vmac.callCenter.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.vmac.callCenter.R;

import java.util.ArrayList;
import java.util.List;


public class ChatMessageActivity extends AppCompatActivity implements  KeyboardChangeListener.KeyBoardListener {
    KeyboardChangeListener listener;
    private ChatListView chatListView;
    ViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final List<String> datalist =new ArrayList<>();
        for(int i=0;i<23;i++){
            datalist.add("我是List Item");
        }

        chatListView = (ChatListView) findViewById(R.id.recycler_view);
        adapter =new ViewAdapter(datalist);
        chatListView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
//                LinearLayoutManager.VERTICAL, reverseLayout);

        chatListView.setLayoutManager(layoutManager);
        chatListView.setItemAnimator(new DefaultItemAnimator());
        View view = findViewById(R.id.btn_send);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datalist.add("我是List Item");
                adapter.notifyDataSetChanged();
                if (adapter.getItemCount() > 1) {
                    chatListView.scrollToPosition(adapter.getItemCount()-1 );
                }
            }
        });
        listener =new KeyboardChangeListener(this);
        listener.setKeyBoardListener(this);
    }

    @Override
    public void onKeyboardChange(boolean isShow, int keyboardHeight) {
        Log.i(" show log "," isShow " + isShow + "  " + keyboardHeight);

        if (adapter.getItemCount() > 1) {
                     chatListView.getLayoutManager().smoothScrollToPosition(chatListView, null, adapter.getItemCount());
            //            chatListView.scrollToPosition(adapter.getItemCount()-1 );
        }
    }
}
