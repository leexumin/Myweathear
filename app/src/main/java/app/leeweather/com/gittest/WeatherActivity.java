package app.leeweather.com.gittest;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by leexumin on 2015/12/3.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    //用于显示城市名字
    private TextView cityNameText;
    //用于显示今天日期
    private TextView date1;
    //用于显示第一天的天气描述
    private TextView D1weatherDespText1;
    private TextView D1weatherDespText2;
    //用于天气提醒
    private  TextView zhuyiText;


    //用于显示当前气温
    private TextView nowTemptext;
    //显示明天天气描述
    private TextView D2weatherDespText;
    //用于显示明天日期
    private TextView date2;
    //用于显示当前日期
    private TextView currentDataText;
    //用于后天天气日期
    private TextView date3;
    //用于后天天气描述
    private TextView D3weatherDespText;
    //切换城市按钮
    private Button switchCity;
    //更新天气按钮
    private Button refreshWeather;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.weather_layout);
        //初始化控件
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText = (TextView)findViewById(R.id.city_name);
        date1 = (TextView)findViewById(R.id.date1_text);
        zhuyiText =(TextView)findViewById(R.id.zhuyi_text);
        date3 = (TextView)findViewById(R.id.date3_text);
        D3weatherDespText=(TextView)findViewById(R.id.day3weather_text);
        zhuyiText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线

        zhuyiText.getPaint().setAntiAlias(true);//抗锯齿
        D2weatherDespText =(TextView)findViewById(R.id.day2weather_text);

        D1weatherDespText1= (TextView)findViewById(R.id.day1weather_text_1);
        D1weatherDespText2= (TextView)findViewById(R.id.day1weather_text_2);
        nowTemptext = (TextView)findViewById(R.id.dangqianwendu);
        date2= (TextView)findViewById(R.id.date2_text);
        currentDataText= (TextView)findViewById(R.id.current_data);
        String countyCode =getIntent().getStringExtra("county_code");
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather =(Button)findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);


        if (!TextUtils.isEmpty(countyCode)){
            //有县级代号时就去查询天气代号

            date1.setText("正在很努力地同步...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);









        }else {
            //没有县级代号时就直接查询显示本地天气
            showWeather();
            //登录更新
            currentDataText.setText("正在更新天气...");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String citykey =  prefs.getString("citykey", "");
            if (!TextUtils.isEmpty(citykey)){
                queryWeatherInfo(citykey);
            }
        }


    }
        @Override
        protected void onResume(){
            super.onResume();
            //桌面切回活动更新
            currentDataText.setText("正在更新天气...");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String citykey =  prefs.getString("citykey", "");
            if (!TextUtils.isEmpty(citykey)){
                queryWeatherInfo(citykey);
            }

        }

       @Override
        public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
              Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                   currentDataText.setText("正在更新天气...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
               String citykey =  prefs.getString("citykey", "");
                if (!TextUtils.isEmpty(citykey)){
                  queryWeatherInfo(citykey);
                }
                break;
            default:
                break;

        }
    }

    /*
    查询县级对应代号的天气代号
     */

       private void queryWeatherCode (String countyCode){

        String address = "http://www.weather.com.cn/data/list3/city"+countyCode +".xml";
       queryFromServer(address,"countyCode",null);}


    /*
    查询天气代号所对应的天气
    * */
    private void queryWeatherInfo(String weatherCode){
      String address ="http://wthrcdn.etouch.cn/weather_mini?citykey="+weatherCode;

        queryFromServer(address, "weatherCode", weatherCode);
    }
    /*根据传入的地址和类型，去向服务器查询天气的代号或天气信息

     */
        private void queryFromServer(final String address,final String type,final String citykey) {

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {

                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];

                            queryWeatherInfo(weatherCode);

                        }
                    }


                } else if ("weatherCode".equals(type) || "citykey".equals(type)) {
                    //处理从服务器返回的信息
                    Utility.handleWeatherResponse(WeatherActivity.this, response, citykey);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }


            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        date1.setText("同步失败了哟");
                    }
                });
            }

        });

    }
    /*从SharedPreferences文件中读取天气的信息，并显示在界面上

     */
   private void showWeather(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        nowTemptext.setText(prefs.getString("wendu", "")+"℃");
        date1.setText( prefs.getString("d1", ""));
       D1weatherDespText1.setText(
               prefs.getString("fengxiang","")+ prefs.getString("fengli","")+"、"+prefs.getString("type",""));
        D1weatherDespText2.setText("最"+prefs.getString("diwen","")+"、"+"最"+prefs.getString("gaowen",""));
        date2.setText(prefs.getString("d2",""));
        D2weatherDespText.setText("最"+prefs.getString("diwen1","")+"、"+"最"+prefs.getString("gaowen1","")+"、"+
               prefs.getString("fengxiang1","")+ prefs.getString("fengli1","")+"、"+prefs.getString("type1",""));


       date3.setText(prefs.getString("d3",""));
       D3weatherDespText.setText("最"+prefs.getString("diwen2","")+"、"+"最"+prefs.getString("gaowen2","")+"、"+
               prefs.getString("fengxiang2","")+ prefs.getString("fengli2","")+"、"+prefs.getString("type2",""));


        zhuyiText.setText(prefs.getString("zhuyi",""));


        currentDataText.setText(prefs.getString("current_data", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);


    }

}
