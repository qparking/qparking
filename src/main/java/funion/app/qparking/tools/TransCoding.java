package funion.app.qparking.tools;

import java.io.UnsupportedEncodingException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 运泽 on 2015/12/24.
 * unicode转码工具
 */
public  class TransCoding {
    public static String trans(String str) {
        byte[] converttoBytes = new byte[0];
        try {
            converttoBytes = str.getBytes("UTF-8");
            String s2 = new String(converttoBytes, "UTF-8");
            return s2;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public  static String strToDate(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int i=Integer.parseInt(strDate);
        String times=sdf.format(new Date(i*1000L));
        return times;
    }

    public  static String strToDetailTime(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int i=Integer.parseInt(strDate);
        String times=sdf.format(new Date(i*1000L));
        return times;
    }
}
