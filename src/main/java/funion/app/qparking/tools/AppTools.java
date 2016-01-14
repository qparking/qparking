package funion.app.qparking.tools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import funion.app.qparking.R;

@SuppressLint({"ShowToast", "DefaultLocale"})
public class AppTools {
    public static boolean viewJudge1 = false;
    public static boolean viewJudge2 = false;

    public static String getSDPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    public static boolean checkFileExists(String path) {
        if (new File(path).exists()) {
            return true;
        }
        return false;
    }

    public static boolean checkMoney(String paramString) {
        return Pattern.compile("-?[0-9]*$?").matcher(paramString).matches();
    }

    public static boolean chekPhone(String paramString) {
        return Pattern
                .compile(
                        "((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)")
                .matcher(paramString).matches();
    }


    public static String getTimeformat(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long temp = Long.parseLong(time);
//        long temp1 = System.currentTimeMillis();
        Date date = new Date(temp);
        String result = sdf.format(date);
        return result;
    }

    /**
     * 判断支付类型方法
     *
     * @param type 支付类型
     * @return
     */
    public static String payMethod(String type) {
        if (type.equals("wx")) {
            return "微信支付";
        }
        if (type.equals("alipay")) {
            return "阿里支付";
        }
        return "余额";
    }

    /**
     * 距离换算
     */
    public static String distance(int num) {
        float f = (float) num;
        float x = (float) 1000;
        String result = (f / 1000) + " KM";
        return result;
    }

    /**
     * 配置异步加载图片信息
     */
    public static DisplayImageOptions confirgImgInfo(int erroImg, int loadingImg) {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_empty) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(erroImg)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(loadingImg)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)    //设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true).displayer(new FadeInBitmapDisplayer(300)).build();
        return options;
    }
}
