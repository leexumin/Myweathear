package app.leeweather.com.gittest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by leexumin on 2015/12/1.
 */
public class Utility {

    /*
   解析
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if (allProvinces != null &&allProvinces.length>0){
                for (String p : allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据储存
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length>0){
                for (String c : allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);

                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;

    }

    public  static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCountries = response.split(",");
            if (allCountries != null &&allCountries.length>0){
                for (String c : allCountries){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);

                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;

    }
   /*
 解析天气预报服务器返回的JSON数据，并存储到本地
  */
    public static void handleWeatherResponse(Context context,String response,String citykey  ) {
        try{
        //解析第一层数据
            JSONObject jsonObject = new JSONObject(response);
            JSONObject crentdata = jsonObject.getJSONObject("data");
          String wendu = crentdata.getString("wendu");
            String zhuyi = crentdata.getString("ganmao");
          String cityname = crentdata.getString("city");
          //解析第二层数组数据，
            JSONArray forecast = crentdata.getJSONArray("forecast");
            JSONObject fore = forecast.getJSONObject(0);
            //当天天气情况
             String gaowen = fore.getString("high");
             String fengli = fore.getString("fengli");
            String fengxiang = fore.getString("fengxiang");
            String riqi= fore.getString("date");
             String diwen=fore.getString("low");
             String type = fore.getString("type");
            JSONObject fore1 = forecast.getJSONObject(1);//次日天气
            String gaowen1 = fore1.getString("high");
            String fengli1 = fore1.getString("fengli");
            String fengxiang1= fore1.getString("fengxiang");
            String riqi1 = fore1.getString("date");
            String diwen1 =fore1.getString("low");
            String type1 = fore1.getString("type");



           saveWeatherInfo(context,wendu,zhuyi,cityname,gaowen,fengli,
                   fengxiang,riqi,diwen,type,gaowen1,
                   fengli1,fengxiang1,riqi1,diwen1,type1,citykey);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    /*将服务器返回的天气数据存储到SharedPreferences文件中

     */
 public static void saveWeatherInfo(Context context,String wendu,String zhuyi,String cityname,
                                String   gaowen, String fengli,
                                   String fengxiang,String riqi,String diwen, String type,String gaowen1,
                              String  fengli1,String fengxiang1,String riqi1,String diwen1,String type1,String citykey){
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
      SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
     editor.putBoolean("city_selected", true);
      //当日天气的临时存储
     editor.putString("citykey",citykey);
       editor.putString("wendu",wendu);
       editor.putString("zhuyi",zhuyi);
        editor.putString("city_name", cityname);
        editor.putString("gaowen",gaowen);
        editor.putString("diwen",diwen);
        editor.putString("d1",riqi);
        editor.putString("type",type);
        editor.putString("fengli",fengli);
        editor.putString("fengxiang",fengxiang);
        //次日天气的临时存储
     editor.putString("gaowen1",gaowen1);
     editor.putString("diwen1",diwen1);
     editor.putString("d2",riqi1);
     editor.putString("type1",type1);
     editor.putString("fengli1",fengli1);
     editor.putString("fengxiang1",fengxiang1);

     editor.putString("current_data",sdf.format(new Date()));
        editor.commit();

    }



}

