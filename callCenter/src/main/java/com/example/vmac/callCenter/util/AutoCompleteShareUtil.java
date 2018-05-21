package com.example.vmac.callCenter.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.ArrayAdapter;

/**
 * Created by Administrator on 2018/1/6.
 */

public class AutoCompleteShareUtil {
    public  Context context;
    public  SharedPreferences sharedPreferences;
    public  String autoCompleteSharePath="autoCompleteShareFile";
    public  String callNo="callNo"; //call NO
    public   String CustomerAddress="CustomerAddress";
    public  String CustomerName ="CustomerName";
    public  String Enduser ="Enduser";
    public  String Telephone ="Telephone";
    public  String CustomerNumber="CustomerNumber";
    public  String MachineType="MachineType";
    public  String MachineSn = "MachineSn";
    public  String IbmEngineer ="IbmEngineer";
    public  String  EngineerTelephone ="EngineerTelephone";
    public  String Contacts="Contacts";
    public  String FaultPart="FaultPart";
    public  String Remark="Remark";
    public  String FaultDetail ="FaultDetail";
    public  String MachineModel="MachineModel";
    public  String default_values = "";
    private SharedPreferences.Editor editor;
    public AutoCompleteShareUtil(Context context){
        this.context=context;

        sharedPreferences =  context.getSharedPreferences(autoCompleteSharePath,Context.MODE_PRIVATE);
        editor= sharedPreferences.edit();
        init();
    }

    public  String[] getStringValues(String key){
       String valuesStr =  sharedPreferences.getString(key,"");
       if(default_values.equals(valuesStr)){
           return  new String[]{};
       }
       String[] values = valuesStr.split(",");
       return values;
    }

    public void saveHestoryValue(String key,String value){
        if(TextUtils.isEmpty(value)){
            return;
        }
        String values = sharedPreferences.getString(key,default_values);
        if(values.contains(value)){
            values =  values.replace(value+",","");
        }
//       values = value+","+values;
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(value);
        stringBuilder.append(',');
        stringBuilder.append(values);
        editor.putString(key,stringBuilder.toString());
        editor.apply();

    }

    public ArrayAdapter getStringAdapter(String key){
        String[] values=getStringValues(key);
        ArrayAdapter<String> mSearchAdapter = new ArrayAdapter<String>(
               context,
                android.R.layout.simple_dropdown_item_1line,
               values
        );
        return  mSearchAdapter;
    }

    private void init(){
        String[] customName=new String[]{"移动","招商","电信","中兴","联通"};
        String[] customAdress=new String[]{"深圳","广州","惠州","东莞","中山","佛山","北京"};
        saveArray(CustomerName,customName);
        saveArray(CustomerAddress,customAdress);


        String[] machinType=new String[]{"2076","1814","2833","1726","2424","2145","2423","2421"};
        saveArray(MachineType,machinType);
        String[] machinSN=new String[]{"75BTM80","7822BKV","78K199C","75GFH30", "13K19FX","78DRVB0","75BCG70","75FRH90"};
        saveArray(MachineSn,machinSN);
        String[] machinModel=new String[]{"961","24F","981","HC4","DH8","951","941"};
        saveArray(MachineModel,machinModel);

        String[] connactor=new String[]{ "SSR 林松啸 /MR 杨",  "SSR 张君严 /MR 李", "SSR 张俊彦 /MR 郭", "SSR 杨诗伟 /MR  李", "MR. 林", "SSR 沈丛恺 /MR. 刘", "SSR. 史殿波 /MR. 李","SSR  吴宛柱 /MR  蔡",  "MR  江",  "MR 曲"};
        saveArray(Contacts,connactor);
        String[] telephone = new String[]{"022-13820188898","0371-13526768285","0370-13937019762","0851-18054207835", "0592-13559281216",  "021-28989629", "0411-15042438150",  "027-13667270820", "0592-18950085326",  "0311-13903112588"};
        saveArray(Telephone,telephone);

        String[] enginner_phone = new String[]{"18698080678","15138005251","15138005251","18980063699","13391309633","18624385880","13986080298","15860737887","18503200168"};
        saveArray(EngineerTelephone,enginner_phone);
        String[] enginner = new String[]{"SONG XIAO LIN","JUN YAN JY ZHANG BE","SHI WEI SW YANG BE", "YING WEI FZ ZHU", "JIN YUAN JY YANG",  "DIAN BO AS SHI BE", "WAN ZHU WZ WU","LI YU QL LEI BE","XIAO BING CAO BE"};
        saveArray(IbmEngineer,enginner );
        String[] fault_detail=new String[]{"故障灯亮"};
        saveArray(FaultDetail,fault_detail);

    }
    private void saveArray(String key,String[] values){
        for(String e:values){
            saveHestoryValue(key,e);
        }
    }
}
