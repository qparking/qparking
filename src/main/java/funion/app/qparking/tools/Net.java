package funion.app.qparking.tools;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * 手机网络信息类 国际标准的手机网络有两种接入方式 ：1.WIFI；2.GPRS(就是以下代码中的mobile) GPRS又被中国移动分为两种接入方式:
 * 1.cmwap；2.cmnet
 * 
 * wifi和cmnet在接入网络的时，是直连internet的 cmwap需要使用代理才能访问任何网站，否则会有限制
 */
public class Net
{
	/************** 网络连接方式 *****************/
	public static final int NONET = 0; // 无网络
	public static final int LINE_TYPE = 1; // 直连方式
	public static final int PROXY_TYPE = 2; // 移动网关代理方式
	/** 手机卡的类型，0:移动、1:联通  2:电信 3:其他*/
	public static int MOBILE_TYPE;

	public static final String WIFI = "wifi";
	public static final String CMNET = "cmnet";
	public static final String CMWAP = "cmwap";
	public static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

	public static int currentNetType = NONET; // 当前网络类型，默认为无网络

	/**
	 * 获取网络连接方式
	 * 
	 * @param context
	 * @return int 0：无网络；1：直连方式(wifi或cmnet)；2：代理(cmwap). 1和2同时存在时，优先1(wifi)
	 */
	public static int getNetType(Context context)
	{
		if (context == null)
		{
			return NONET; // 无网络
		}

		if (isWifiConnected(context)) // wifi
		{
			return LINE_TYPE;
		}
		else if (isMobileConnected(context)) // mobile
		{
			MOBILE_TYPE = getMobileType(context);
			String proxy = getCurrentApnInUse(context);
			if (proxy!=null && proxy.equals(""))
			{
				return LINE_TYPE; // 直连
			}
			else
			{
				return PROXY_TYPE; // 代理
			}
		}
		else
		{
			return NONET; // 无网络
		}

	}

	/**
	 * 判断当前连接方式是否是WIFI连接
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isWifiConnected(Context context)
	{
		return getNetworkState(context, ConnectivityManager.TYPE_WIFI) == State.CONNECTED;
	}


	/**
	 * 判断是否是MOBILE连接
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isMobileConnected(Context context)
	{
		return getNetworkState(context, ConnectivityManager.TYPE_MOBILE) == State.CONNECTED;
	}

	/**
	 * MOBILE方式下获取当前的网络连接方式，cmwap或cmnet
	 * 
	 */
	private static String getCurrentApnInUse(Context context)
	{
		if (context == null || Integer.parseInt(Build.VERSION.SDK) >= 14)
		{
			return "";
		}
		Cursor cursor = context.getContentResolver().query(PREFERRED_APN_URI, new String[]{"_id", "apn", "type", "proxy"}, null, null, null);
		cursor.moveToFirst();
		if (cursor.isAfterLast())
		{
			return "";
		}
		String apn = cursor.getString(3);
		if (apn == null)
		{
			apn = "";
		}
		return apn;
	}

	/**
	 * 获取连接状态
	 * 
	 * @param context
	 * @param networkType
	 * @return
	 */
	private static State getNetworkState(Context context, int networkType)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = cm.getNetworkInfo(networkType);

		return info == null ? null : info.getState();
	}

	/**
	 * 获取手机卡类型，移动、联通、电信
	 * 
	 * @param context
	 * @return
	 */
	public static int getMobileType(Context context)
	{
		int type = 0;
		TelephonyManager iPhoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String iNumeric = iPhoneManager.getSimOperator();
		if (iNumeric.length() > 0)
		{
			if (iNumeric.equals("46000") || iNumeric.equals("46002") || iNumeric.equals("46007"))
			{
				// 中国移动
				type = 0;
			}
			else if (iNumeric.equals("46001"))
			{
				// 中国联通
				type = 1;
			}
			else if (iNumeric.equals("46003"))
			{
				// 中国电信
				type = 2;
			}
			else 
			{
				type = 3;
			}
		}
		else
		{
			type = 3;
		}
		return type;
	}
}
