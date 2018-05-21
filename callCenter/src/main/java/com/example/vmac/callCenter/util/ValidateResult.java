package com.example.vmac.callCenter.util;

/**
 * Created by Administrator on 2017/9/21.
 */

public class ValidateResult {
        public boolean resulte;
        public String tipMsg;
        public static ValidateResult getInstance(){
                return new ValidateResult();

        }
        public static ValidateResult getInstance(Boolean valiable,String msg){
                ValidateResult v=new ValidateResult();
                v.resulte=valiable;
                v.tipMsg=msg;
                return v;
        }
}
