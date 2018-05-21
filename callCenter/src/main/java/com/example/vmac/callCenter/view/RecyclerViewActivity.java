package com.example.vmac.callCenter.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.vmac.callCenter.R;

import java.util.ArrayList;

public class RecyclerViewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_content_room);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        ArrayList<String> data =new ArrayList<>();
        for (int i=0;i<20;i++){
            data.add("数据项+ "+i);
        }
        StringAdapter adapter = new StringAdapter(data);
        recyclerView.setAdapter(adapter);
    }
}
