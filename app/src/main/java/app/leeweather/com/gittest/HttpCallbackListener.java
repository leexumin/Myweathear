package app.leeweather.com.gittest;

/**
 * Created by leexumin on 2015/12/1.
 */
public  interface HttpCallbackListener{
    void onFinish(String response);
    void onError(Exception e);
}
