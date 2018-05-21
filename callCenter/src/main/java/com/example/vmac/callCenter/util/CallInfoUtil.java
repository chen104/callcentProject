package com.example.vmac.callCenter.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Administrator on 2018/1/17.
 */

public class CallInfoUtil
{



    public static String idKey="id";
    public static String callNoKey="callNo"; //call编号
    public static String callNo="call编号";
    public static String customerAddressKey="customerAddress"; //客户地址
    public static String customerAddress="客户地址";

    public static String enduserKey="enduser"; //客户名称
    public static String enduser="报call人";

    public static String customerNameKey="customerName"; //报call人
    public static String customerName="客户名称";
    public static String telephoneKey="telephone";//客户电话
    public static String telephone="客户电话";

    public static String customerNumberKey="customerNumber";//客户编码
    public static String customerNumber="客户编码";
    public static String machineTypeKey="machineType"; //机器类型
    public static String machineType="机器类型";
    public static String ibmEngineerKey="ibmEngineer";//ibm工程工程师
    public static String ibmEngineer="ibm工程工程师";
    public static String ibmEngeineerSnKey="ibmEngeineerSn";//ibm工程师编号
    public static String ibmEngeineerSn="工程师编号";

    public static String   engineerTelephoneKey="engineerTelephone";
    public static String  engineerTelephone="工程师电话";

    public static String  faultPartKey ="faultPart";
    public static String  faultPart="故障备件";

    public static String  remarkKey ="remark";
    public static String   remark="其他备注";

    public static String  levelKey ="level";
    public static String   level="紧急程度";

    public static String faultDetailKey="faultDetail";
    public static String faultDetail="故障说明";

    public static   String parseJson(JsonObject jsonObject){
        StringBuilder sb=new StringBuilder();

        readItem(callNoKey,jsonObject,callNo,sb);

        readItem(enduserKey,jsonObject,enduser,sb);

        readItem(customerNumberKey,jsonObject,customerNumber,sb);

        readItem(customerNameKey,jsonObject,customerName,sb);

        readItem(telephoneKey,jsonObject,telephone,sb);

        readItem(machineTypeKey,jsonObject,machineType,sb);

        readItem(ibmEngineerKey,jsonObject,ibmEngineer,sb);

        readItem(ibmEngeineerSnKey,jsonObject,ibmEngeineerSn,sb);

        readItem(engineerTelephoneKey,jsonObject,engineerTelephone,sb);

        readItem(faultPartKey,jsonObject,faultPart,sb);

        readItem(faultDetailKey,jsonObject,faultDetail,sb);

        readItem(levelKey,jsonObject,level,sb);

//            readItem(CallInfoConstant.engineerTelephoneKey,jsonObject,CallInfoConstant.engineerTelephone,sb);



        return sb.toString();
    }

    public static void readItem(String key,JsonObject jsonObject,String chzn,StringBuilder sb){
        JsonElement element =jsonObject.get(key);
        if(element==null){return;}
        String accountName =element .getAsString();
        sb.append(chzn);
        sb.append(":");
        sb.append(accountName);
        sb.append("\n");

    }
}
