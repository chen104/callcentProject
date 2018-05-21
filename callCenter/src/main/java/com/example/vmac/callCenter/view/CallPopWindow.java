package com.example.vmac.callCenter.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.vmac.callCenter.R;

/**
 * Created by Administrator on 2018/1/18.
 */

public class CallPopWindow extends PopupWindow  {
    private Context context;
    private View view;
    private LinearLayout callcenter;
    private LinearLayout  callPartcenter;
    private   View.OnClickListener listener;
   public void setOnClickListener(View.OnClickListener onClickListener){
       listener= onClickListener;
       callcenter.setOnClickListener(onClickListener);
       callPartcenter.setOnClickListener(onClickListener);
   }
    public CallPopWindow(Context context) {
        this(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public CallPopWindow (Context context, int width, int height) {
        super(context);
        this.context = context;
        setWidth(360);
        setHeight(200);
        setFocusable(true);
        setOutsideTouchable(true);
        setTouchable(true);
        view = LayoutInflater.from(context).inflate(R.layout.main_menu_popup,null);
        setContentView(view);

        setBackgroundDrawable(new BitmapDrawable());
       callcenter= (LinearLayout) view.findViewById(R.id.popup_callcenter);
        callPartcenter = (LinearLayout) view.findViewById(R.id.popup_callpart);

    }

}
