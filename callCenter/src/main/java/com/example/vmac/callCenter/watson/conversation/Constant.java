package com.example.vmac.callCenter.watson.conversation;

/**
 * Created by Administrator on 2017/8/23.
 */

public interface Constant {
    public  static  final  int watsonType=1;//watson 回答
    public  static  final int customType=100; //客户发送

    public static  final  int start_speeker=8;// 开始语音合成播放

    /**
     * 类型和序列号输入框
     */
    public static final int show_typeSNInputBox =12;//
    public static final int show_input_callinfo_phone =21;//显示call电话

    public static  final int show_bt_radio_intent=10;//显示单选按钮
    public static final int check_type_SN=13;
    public static final  int ListDataChang=14;//提示list表数据变化
    public static final  int show_wansont_single_img=15;
    /**
     * 显示callno 输入
     */
    public static  final int show_callNo=16; //显示callNo输入

    public static  final  int check_callInfo_callNo=17;//查询
    public static  final  int check_callInfo_serial=19;//查询
    public static final int send_wansot_massge=18;//发送信息
    public static final int check_callInfo_byphone = 20;//查询call by phone
    public static final int check_callInfo_query =50;//查询call by phone
    public static final  int check_power_local = 51;//查询location信息
    public static final  int check_power_part = 52;//查询location信息

    public static final  int  handler_callInfo=1;//在查询到了callinfo
    public static final  int  handler_NocallInfo=2;//没有查询到了callinfo

    public static final  int  handler_machinStatue=3;//在查询到了callinfo
    public static final  int  handler_noMachinStatue=4;//没有查询到了callinfo
    public static final  int handler_exception=100;//查询异常
    public static final  int handler_porwer_link=8;//查询异常
    public static  final int handler_add_message=5;
    public static  final int handler_add_send_message=6;
    public static  final  int handler_show_rsc_result=7;






    public static final int show_machin_power_no=20;//

    public static  final  int show_power_input=21; //显示
    public static  final  int find_link=22; //link
    public static  final  int web_local_link=23;//locallink
    public static  final  int web_part_link=24;//locallink
    public static  final  int callBack_start_web=25; //跳转到web
    public static  final  int callBack_add_callinfo=27; //添加
    public static  final  int show_chat_srcbox=26;//rcms输入


    public static  final  int chat_item_src_img=27;//
    public  static  final  int chat_item_power_start_web=28;
    public static  final  int  chat_item_show_ratingbar=29;
    public static final int chat_item_show_call_or_send=30;

    public static final  int callbackTocallphone=31;//拨号
    public static final  int show_chat_item_show_callinfoActivity=32;//拨号


    public static final  String ACTION="action"; //action的key
    /**
     * show =1 : 保修 不保修
     * show = 2 : 联系客户人员
     * show = 3: 弹出工单查询方式
     * show = 5 : 询问是否存在报错代码， 存在 不存在
     * show = 6 : 如何查看报错代码
     */
    public static final String Show="show";//show key



    public static final String values="values";
    public static  final String show_bt_radio_TypeModel="showTypeModelButton";//显示单选按钮


    public static final String showTypeSNInputBox="inputbox";

    public static final  String showCallNoInput="CallNo_input";


    public static  final  String action_key_do="do";

    public static final String showWanstSingleValues="image";
    public static  final  String action_Value_call="工单查询";
    public static  final  String action_Machin_State="维保查询";

    public static  final  String action_local_values="location查询";
    public static  final  String action_part_values="FRU查询";
    public static final  String action_src_values="src查询";
//    public static final  String action_src_values="机器故障代码查询";
    public static  final String show_mathinType_input="machinetype";
    public static  final  String show_context_src_boxInput = "srcbox";
    public static final String show_chat_item_ratingbar_key="feeback";
    public static final  String show_chat_item_choise_call_chat="hotline";
    public static final  String show_chat_item_telephone_key= "telephone";
    public static final  String show_chat_item_telephone_title= "phoneTitle";

    public static final String show_chat_item_show_callinfo_activity="opencall";

    public static final String conversatin_new_callNo_key="NewCallNo";

    public static final  String conversatin_call_status_key="call_status";
    public static final  String conversatin_call_status_hasCall="已开call";
    public static final  String conversatin_call_status_noCall="未开call";

    public static final  int resultCode_new_call=1;
    public static final  int resultCode_no_call=2;
}
