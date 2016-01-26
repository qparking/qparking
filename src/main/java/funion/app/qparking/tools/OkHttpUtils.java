package funion.app.qparking.tools;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;
import java.util.Set;

/**
 * okHttp封装类
 * Created by yunze on 2015/12/18.
 */
public class OkHttpUtils {
    private static OkHttpUtils mOkHttpUtils;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;
    private static OkHttpUtils instance;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private OkHttpUtils() {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(Looper.getMainLooper());//放到主UI线程去处理
        mGson = new Gson();
    }

    /**
     * 单例模式
     */
    public static OkHttpUtils getInstance() {
        if (instance == null) {
            synchronized (OkHttpUtils.class) {
                if (instance == null) {
                    instance = new OkHttpUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 异步post请求(有参)
     *
     * @param callback 请求回掉接口(返回请求结果)
     * @param url      请求地址
     * @param params   请求参数
     */
    public void post(String url, final ResultCallback callback, Map<String, String> params, String method) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr, method);
        deliveryResult(callback, request);
    }

    /**
     * 异步get请求
     *
     * @param callback 请求回掉接口(返回请求结果)
     * @param url      请求地址
     */
    public void get(String url, final ResultCallback callback, String method) {
        Request request = buildPostRequest(url, null, method);
        deliveryResult(callback, request);
    }

    /**
     * 请求返回结果方法
     *
     * @param callback 请求结果回掉接口
     * @param request  请求对象
     */
    private void deliveryResult(final ResultCallback callback, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String result = response.body().string();
                    sendSuccessResultCallback(result, callback);
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } catch (com.google.gson.JsonParseException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                }
            }
        });
    }

    private void sendSuccessResultCallback(final String result, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(result);
                }
            }
        });
    }

    /**
     * 请求失败方法
     *
     * @param request  请求对象
     * @param e        异常对象
     * @param callback 结果返回回掉接口
     */
    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onError(request, e);
                }
            }
        });
    }

    /**
     * 创建post请求方法
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return Request 请求对象
     */

    private Request buildPostRequest(String url, Param[] params, String method) {
        String urlAdress = url + method;
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (params == null) {
            return new Request.Builder().url(urlAdress).build();
        }
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody body = builder.build();
        return new Request.Builder().url(urlAdress).post(body).build();
    }


    /**
     * 获取接口所需参数对象值
     *
     * @param params 获取参数对象
     */
    private Param[] map2Params(Map<String, String> params) {
        if (params == null) {
            return new Param[0];
        }
        int size = params.size();
        Param[] param = new Param[size];
        Set<Map.Entry<String, String>> entrys = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entrys) {
            param[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return param;
    }



    public static class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

    public static abstract class ResultCallback<T> {
//        Type mType;
//
//        public ResultCallback() {
//            mType = getSuperclassTypeParameter(getClass());
//        }
//
//        static Type getSuperclassTypeParameter(Class<?> subclass) {
//            Type superclass = subclass.getGenericSuperclass();
//            if (superclass instanceof Class) {
//                throw new RuntimeException("Missing type parameter.");
//            }
//            ParameterizedType parameterized = (ParameterizedType) superclass;
//            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
//        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(String result);
    }
}
