package com.example.vmac.callCenter.watson.conversation;

/**
 * Created by Administrator on 2018/1/15.
 */
import com.example.vmac.callCenter.Message;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 过滤发送或发挥的Message,把特定的
 */
public class MessageFilter {
  public  class MatcherResulter{
        public boolean isMatcher =false;
        public String matcherString ="";
    }
    public String handlerMessage(Message message,Constant constant){
      String msgStr = message.getMessage();
        if(msgStr ==null){
            return null;
        }
        if(msgStr.contains("No")){

        }
        return null;
    }

    /**
     * 匹配callinfo 的编号
     * @param param
     * @return
     */
    public MatcherResulter isMatcherCallNo(String param){
        MatcherResulter matcherResulter =new MatcherResulter();
        param = param.toUpperCase();
        param =  param.replace(" ","");
        param =  param.replace("，","").replace(",","");
        String regEx = "[A-Z0-9]{7}";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(param);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.find();
        matcherResulter.isMatcher=rs;
        if(rs){

            matcherResulter.matcherString = matcher.group();
        }
        return  matcherResulter;
    }

    /**
     *
     * @param param
     * @return
     */
    public MatcherResulter isMatcherMachinTypeAndMachinSn(String param){
        MatcherResulter matcherResulter =new MatcherResulter();
        param =  param.replace(" ","").replace(",","").replace("，","");
        param = param.toUpperCase();
        String regEx = "[1-9][0-9]{3}-[A-Z0-9]{7}";//
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(param);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.find();
        matcherResulter.isMatcher = rs;

        if(rs) {
            matcherResulter.matcherString = matcher.group();
            return  matcherResulter;
        }

        regEx = "[1-9][0-9]{3}[A-Za-z0-9]{7}";//

        pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(param);
        // 字符串是否与正则表达式相匹配
        rs = matcher.find();
        matcherResulter.isMatcher = rs;
        if(rs) {
            matcherResulter.matcherString = matcher.group();
            return  matcherResulter;
        }
        return  matcherResulter;
    }


    /**
     * [A-Z0-9]{3}
     * 匹配 model 的编号
     * @param param
     * @return
     */
    public MatcherResulter isMachineModel(String param){
        MatcherResulter matcherResulter =new MatcherResulter();
        param =  param.replace(" ","");
        param = param.toUpperCase();
        String regEx = "[A-Z0-9]{3}";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(param);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.matches();
        matcherResulter.isMatcher = rs;
        if(rs){
            matcherResulter.matcherString = matcher.group();
        }
        return  matcherResulter;
    }



    /**
     * 匹配  [1-9][0-9]{3}-[A-Z0-9]{3}  表达式
     * 是type-model
     * @param param
     * @return
     */
    public  MatcherResulter isMatcherMachinTypeAndMachin(String param){
        param =  param.replace(" ","").replace(",","").replace("，","");
        param = param.toUpperCase();
        MatcherResulter matcherResulter =new MatcherResulter();
        String regEx = "[1-9][0-9]{3}-[A-Z0-9]{3}";//
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(param);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.find();
        matcherResulter.isMatcher = rs;

        if(rs) {
            matcherResulter.matcherString = matcher.group().replace("-","");
            return  matcherResulter;
        }

        regEx = "[1-9][0-9]{3}[A-Za-z0-9]{3}";//

        pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(param);
        // 字符串是否与正则表达式相匹配
        rs = matcher.find();
        matcherResulter.isMatcher = rs;
        if(rs) {
            matcherResulter.matcherString = matcher.group();

        }
        return  matcherResulter;
    }

    /**
     *
     * @param param
     * @return
     */
    public  MatcherResulter isMatcherCallInfoPhone(String param){
        param = param.toUpperCase();
        MatcherResulter matcherResulter =new MatcherResulter();
        String regEx = "\\d-??\\d{8,}+";//
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(param);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.find();
        matcherResulter.isMatcher = rs;

        if(rs) {
            matcherResulter.matcherString = matcher.group();

        }
        return  matcherResulter;
    }

    public  MatcherResulter isMatcherCallInfoParam(String param){
        MatcherResulter rs = null;
       param =  param.replace(" ","").replace(",","").replace("，","");

        rs=  isMatcherCallInfoPhone(param);
        if(rs.isMatcher){ return rs;}

        rs=  isMatcherMachinTypeAndMachinSn(param);
        if(rs.isMatcher){ return rs;}

        rs=  isMatcherCallNo(param);
        if(rs.isMatcher){ return rs;}

        return new MatcherResulter();
    }


    /**
     * 匹配 [0-9A-Z]8
     * 报错代码
     * @param param
     * @return
     */
    public  MatcherResulter isMatcherSRCCode(String param){
        param = param.toUpperCase().replace(",","").replace(" ","");
        MatcherResulter matcherResulter =new MatcherResulter();
        String regEx = "[0-9A-Z]{8}+";//
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(param);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.find();
        matcherResulter.isMatcher = rs;

        if(rs) {
            matcherResulter.matcherString = matcher.group();

        }
        return  matcherResulter;
    }

    public static  boolean isNumber(String str){
        String regEx = "^\\d+";//
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return   matcher.matches();

    }

    public MatcherResulter isMachinType(String param){
        param = param.toUpperCase().replace(",","").replace(" ","");
        MatcherResulter matcherResulter =new MatcherResulter();
        String regEx = "\\d{4}";//
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(param);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.find();
        matcherResulter.isMatcher = rs;

        if(rs) {
            matcherResulter.matcherString = matcher.group();

        }
        return  matcherResulter;

    }

    public MatcherResulter isMachinSn(String param){
        param = param.toUpperCase().replace(",","").replace(" ","");
        MatcherResulter matcherResulter =new MatcherResulter();
        String regEx = "[0-9A-Z]{7}";//
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        Matcher matcher = pattern.matcher(param);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.find();
        matcherResulter.isMatcher = rs;

        if(rs) {
            matcherResulter.matcherString = matcher.group();

        }
        return  matcherResulter;

    }
}
