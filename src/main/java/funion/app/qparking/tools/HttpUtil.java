package funion.app.qparking.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import funion.app.qparking.QParkingApp;

/**
 * Created by 运泽 on 2015/12/30.
 */
public class HttpUtil {
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码

    /**
     * 上传文件到服务器
     *
     * @param file
     *            需要上传的文件
     * @param
     *
     * @return 返回响应的内容
     */
    public static String uploadFile(String path,Map<String,String> param,File file,String fileInputName) {
        String TAG = "uploadFile";
        Log.e(TAG, "uploadFile path:"+path);
        int res = 0;
        String result = "error";
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        InputStream connInput = null;
        InputStream fis = null;

        try {
            URL url = new URL(QParkingApp.URL+path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);
//			conn.addRequestProperty("type", type);
//			conn.addRequestProperty("carId", carId+"");
//			conn.addRequestProperty("userId", userId+"");

            if (file != null) {
                /**
                 * 当文件不为空时执行上传
                 */
                dos = new DataOutputStream(
                        conn.getOutputStream());
                StringBuffer sb = new StringBuffer();

                if(param != null){
                    Set<Entry<String,String>> set = param.entrySet();
                    for(Entry<String,String> entry:set){
						/*==========参数请求开始===============*/
                        sb.append(PREFIX);
                        sb.append(BOUNDARY);
                        sb.append(LINE_END);
                        sb.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"").append(LINE_END);
                        sb.append(LINE_END);
                        sb.append(entry.getValue());
                        sb.append(LINE_END);
						/*==========参数请求结束===============*/
                    }
                }

				/*==========文件开始===============*/
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名
                 */
                sb.append("Content-Disposition: form-data; name=\""+fileInputName+"\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                fis = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = fis.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                dos.write(LINE_END.getBytes());
				/*==========文件结束===============*/
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);
                if (res == 200) {
                    Log.e(TAG, "request success");
                    connInput = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = connInput.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                    Log.e(TAG, "result : " + result);
                } else {
                    Log.e(TAG, "request error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                }
            if(connInput != null)
                try {
                    connInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(dos != null)
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(conn != null)
                conn.disconnect();
        }
        return result;
    }


    public static String downloadFile(String path,Map<String,String> param,String filePath) {
        String TAG = "downloadFile";
        Log.e(TAG, "download type:"+path+",filePath:"+filePath);
        HttpURLConnection conn = null;
        ByteArrayOutputStream dos = null;
        InputStream connInput = null;
        OutputStream fos = null;

        try {
            StringBuilder sb = new StringBuilder(QParkingApp.URL+path);
            if(param != null){
                Set<Entry<String,String>> set = param.entrySet();
                int i=0;
                for(Entry<String,String> entry:set){
                    if(i++ == 0){
                        sb.append("?").append(entry.getKey()).append("=").append(entry.getValue());
                    }else{
                        sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
            }
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("GET"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            connInput = conn.getInputStream();
            Log.e(TAG, "response code:" + conn.getResponseCode());
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                Log.e(TAG, "request success");
                dos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0,totalLen = 0;
                while( (len=connInput.read(buffer)) != -1){
                    dos.write(buffer, 0, len);
                    totalLen+= len;
                }
                //错误或无图片
                Log.e(TAG, "length:"+totalLen);
                if(totalLen <= 50){
                    return null;
                }
                byte[] imageBytes = dos.toByteArray();
                dos.close();
                if(imageBytes != null){
                    fos = new FileOutputStream(filePath);
                    Bitmap mBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    if(mBitmap != null)
                        mBitmap.recycle();
                    Log.e(TAG, "download success");
                }
            }  else{
                Log.e(TAG, "request error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally{
            if(fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                }
            if(connInput != null)
                try {
                    connInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(dos != null)
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(conn != null)
                conn.disconnect();
        }

        return filePath;
    }
}
