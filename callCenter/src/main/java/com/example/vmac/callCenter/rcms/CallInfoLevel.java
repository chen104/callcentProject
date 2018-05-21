package com.example.vmac.callCenter.rcms;

/**
 * Created by Administrator on 2018/1/5.
 */

public class CallInfoLevel {
    public static  final CallInfoLevel Level1 = new CallInfoLevel("1","已经宕机、严重影响公司业务") ;
    public static  final CallInfoLevel Level2 = new CallInfoLevel("2","由于当前故障导致访问速度下降、对公司业务有一定影响") ;
    public static  final CallInfoLevel Level3= new CallInfoLevel("3","当前故障不影响业务运行，机器仍能使用") ;
    public CallInfoLevel(String level,String desc){
        this.level=level;
        this.desc=desc;
    }
    public String level;
    public String desc;
}
