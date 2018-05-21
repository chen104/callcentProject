package com.example.vmac.callCenter.util;

import java.util.IllegalFormatException;

/**
 * Created by Administrator on 2017/9/4.
 */

public class RandomUtil{
    public static int getIndex(int count,int sub) {

        int index= (int) ((Math.random()*count-sub));
        return index;
    }

}
