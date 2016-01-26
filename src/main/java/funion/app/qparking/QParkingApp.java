package funion.app.qparking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import funion.app.common.CrashHandler;
import funion.app.qparking.tools.AppTools;

//停车场信息节点
class TagParkingItem {
    //停车场id
    public String m_strPid;
    //停车场名
    public String m_strName;
    //停车场位置（经纬坐标）
    public LatLng m_llParking;
    //停车场地址信息
    public String m_strAddress;
    //免费车位数
    public int m_iFreeNum;
    //收费车位数
    public int m_iChargeNum;
    //停车场好评数
    public int m_iPraiseNum;
    //停车场差评数
    public int m_iDespiseNum;
    //停车场分享者名
    public String m_strShareName;
    //停车场位置类型0代表地上，1代表地下，2代表室内
    public int m_iLocationType;
    //我与停车场的距离
    public int m_iDistance;
};

public class QParkingApp extends Application {
    //微信分享appkey
    public static final String APPID = "wxd01b0b209c39b4aa";
    public static final String APPSECRET = "e49d8fd2bd02f40a5728af33fdeccf7b";
    //QQ分享appkey
    public static final String QQAPPID = "1104668766";
    public static final String QQAPPSECRET = "Xln1ofxbBcMoa16b";

    //请求地址
    public static final String URL = "http://qtc.luopan.net/api/";
    //请求地址-改
    public static final String URL_UPDATE = "http://qtc.luopan.net/";
    // 停车场信息列表
    List<TagParkingItem> m_listParking = new ArrayList<TagParkingItem>();
    // 消息变量
    AddressComponent m_addressAdd = null;
    public LatLng m_llMe;
    public LatLng m_llSubmit;
    // 目标停车场
    public TagParkingItem m_itemParking;
    @SuppressWarnings("rawtypes")
    RouteLine m_routeLine;
    public boolean m_bIsGoCenter;
    // 全局信息
    public String m_strUserID = "";
    String m_strUserName = "";
    String m_strMobile = "";
    String m_strScore = "";
    String m_strCarNo = "";

    String m_strVersion = "";
    String m_strDownloadUrl = "";
    String m_strShareInfo = "";
    //网络请求获取图片保存
    String leftBarImg = "";
    String rightBarImg = "";
    String commonmarking = "";
    String selmarking = "";
    String currentmarking = "";
    String subscribe = "";
    String nav = "";

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        initImageLoader(getApplicationContext());
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPoolSize(3);//线程池内加载的数量
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        if (AppTools.ExistSDCard()) {
            config.diskCache(new UnlimitedDiskCache(createFile(AppTools.getSDPath(),
                    "QParking/imageloader/cache")));
            config.diskCacheFileCount(100);//缓存的文件数量
        }
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.defaultDisplayImageOptions(DisplayImageOptions.createSimple());
        config.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 3 * 1000));//超时时间设置
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

    }

    // 显示toast提示
    public static void ToastTip(Context context, String strInfo, int iPos) {
        Toast toast = Toast.makeText(context, strInfo, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, iPos);
        toast.show();
    }

    // 保存用户信息
    public static void SaveUserInfo(Context context, String strShell) {
        // 保存用户信息
        SharedPreferences spParkInfo = context.getSharedPreferences("UserInfo",
                Activity.MODE_PRIVATE);

        Editor editorParkInfo = spParkInfo.edit();
        // 用putString的方法保存数据
        editorParkInfo.clear();
        editorParkInfo.putString("shell", strShell);
        editorParkInfo.commit();
    }

    // 根据view生成bitmap
    public static Bitmap convertViewToBitmap(View view) {
        view.setLayoutParams(new LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    // 获取缩放级别
    public static int GetZoomLevel(int iDistance) {
        int iLevelRet = 19;
        if (iDistance < 120)
            iLevelRet = 19;
        else if (iDistance < 300)
            iLevelRet = 18;
        else if (iDistance < 700)
            iLevelRet = 17;
        else if (iDistance < 1300)
            iLevelRet = 16;
        else if (iDistance < 2600)
            iLevelRet = 15;
        else if (iDistance < 6000)
            iLevelRet = 14;
        else if (iDistance < 12000)
            iLevelRet = 13;
        else if (iDistance < 24000)
            iLevelRet = 12;
        else if (iDistance < 48000)
            iLevelRet = 11;
        else if (iDistance < 96000)
            iLevelRet = 10;
        else if (iDistance < 200000)
            iLevelRet = 9;
        else if (iDistance < 400000)
            iLevelRet = 8;
        else if (iDistance < 800000)
            iLevelRet = 7;
        else if (iDistance < 1600000)
            iLevelRet = 6;
        else if (iDistance < 3200000)
            iLevelRet = 5;
        else if (iDistance < 6400000)
            iLevelRet = 4;

        return iLevelRet;
    }

    private static File createFile(String sdCardPath, String fileName) {
        String path = sdCardPath + fileName;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }
}
