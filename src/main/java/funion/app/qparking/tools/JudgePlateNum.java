package funion.app.qparking.tools;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/12/18 0018.
 * TODO车牌号码识别
 */
public class JudgePlateNum {

    public static boolean isPlateNum(String num){
        Pattern p=Pattern.compile("^[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$");
        Matcher m=p.matcher(num);
        return m.matches();
    }

    /**
     * 手机号码判断
     * @param phonenum
     * @return
     */
    public static boolean isPhoNum(String phonenum){
           /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(phonenum)) return false;
        else return phonenum.matches(telRegex);
    }
}
