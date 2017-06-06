package pers.zinclee123.httputil;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zinclee123 on 2017/5/28.
 */

public class HttpUtils {

    // 将自身的实例对象设置为一个属性,并加上Static和final修饰符
    private static final HttpUtils instance = new HttpUtils();

    private final OkHttpClient client;

    // 静态方法返回该类的实例
    public static HttpUtils getInstance() {
        return instance;
    }

    // 定义一个私有的构造方法
    private HttpUtils() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public Observable<String> get(final String url,final Map<String ,Object> params){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<String> e) throws Exception {
                Request request = new Request.Builder()
                        .url(url + generateGetPara(params))
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException exception) {
                        e.onError(exception);
                        e.onComplete();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        e.onNext(response.body().string());
                        e.onComplete();
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> post(final String url,final Map<String ,Object> params){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<String> e) throws Exception {
                Request request = new Request.Builder()
                        .url(url)
                        .post(generatePostReqBody(params))
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException exception) {
                        e.onError(exception);
                        e.onComplete();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        e.onNext(response.body().string());
                        e.onComplete();
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> postFile(final String url,final Map<String ,Object> params,final Map<String,File> files){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<String> e) throws Exception {
                Request request = new Request.Builder()
                        .url(url)
                        .post(generatePostFileBody(params,files))
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException exception) {
                        e.onError(exception);
                        e.onComplete();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        e.onNext(response.body().string());
                        e.onComplete();
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    String generateGetPara(Map<String,Object> params){
        String result = "";
        if (params != null){
            for(Map.Entry<String,Object> entry : params.entrySet()){
                result = result + "&" + entry.getKey() + "=" + entry.getValue();
            }
        }
        if (!TextUtils.isEmpty(result)){
            result.replaceFirst("&","?");
        }
        return  result;
    }

    RequestBody generatePostReqBody(Map<String,Object> params){
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null){
           for(Map.Entry<String,Object> entry : params.entrySet()){
               builder.add(entry.getKey(),entry.getValue().toString());
           }
        }
        return builder.build();
    }

    RequestBody generatePostFileBody(final Map<String ,Object> params,final Map<String,File> files){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        if (params != null){
            for(Map.Entry<String,Object> entry : params.entrySet()){
                builder.addFormDataPart(entry.getKey(),entry.getValue().toString());
            }
        }

        if (files != null) {
            for(Map.Entry<String,File> entry : files.entrySet()){
                builder.addFormDataPart(entry.getKey(),
                        entry.getValue().getName(),
                        RequestBody.create(null, entry.getValue()));
            }
        }

        return builder.build();
    }

}
