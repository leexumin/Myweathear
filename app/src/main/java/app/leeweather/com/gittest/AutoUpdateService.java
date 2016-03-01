package app.leeweather.com.gittest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;



/**
 * Created by leexumin on 2015/12/8.
 */
public class AutoUpdateService  extends Service {
    @Override
    public IBinder onBind (Intent intent){
        return null ;
    }
    @Override
    public int onStartCommand  (Intent intent,int flags,int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 4*60*60*1000;//4小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime()+ anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);

    }
    //更新天气信息
    private  void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String citykey = prefs.getString("citykey", "");//有BUG。。。
        String address = "http://wthrcdn.etouch.cn/weather_mini?city="+citykey;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response,null);

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

}
