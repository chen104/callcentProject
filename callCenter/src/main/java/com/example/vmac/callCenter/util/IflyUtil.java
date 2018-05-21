package com.example.vmac.callCenter.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/1/21.
 */

public class IflyUtil {
   static Pattern pattern = Pattern.compile("\\d{2,}+");
    // 忽略大小写的写法
    // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
//<sayas type=”number:digits”>412</sayas>
    public static  String convertText(String text){
         Matcher matcher = pattern.matcher(text);
        while(matcher.find()){
            String tem = matcher.group();
            text =   text.replace(tem,"<sayas type=\"number:digits\">"+tem+"</sayas>");
        }
        StringBuilder  sb =new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"GB2312\"?><speak>");
        sb.append(text);
        sb.append("</speak>");
        return sb.toString();
    }
}
