package app.leeweather.com.gittest;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private TextView D1weatherDespText;
    //用于天气提醒
    private  TextView zhuyiText;

    //用于显示当前气温
    private TextView nowTemptext;
    private TextView D2weatherDespText;
    //用于显示明天日期
    private TextView date2;
    //用于显示当前日期
    private TextView currentDataText;
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
        D2weatherDespText =(TextView)findViewById(R.id.day2weather_text);

      D1weatherDespText= (TextView)findViewById(R.id.day1weather_text);
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

          //原为queryWeatherCode







    }else {
        //没有县级代号时就直接查询显示本地天气
           showWeather();
        }

    }
    //刷新和返回菜单

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
                   date1.setText("正在很努力地同步...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String cityname =  prefs.getString("city_name", "");//有BUG。。
                if (!TextUtils.isEmpty(cityname)){
                    refresh(cityname);
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
       queryFromServer(address,"countyCode");}
        //http://wthrcdn.etouch.cn/weather_mini?citykey=101010100(使用新的接口)
        private void refresh (String cityname){

            String address = "http://wthrcdn.etouch.cn/weather_mini?city="+cityname;
            queryFromServer(address,"cityname");}

    /*
    查询天气代号所对应的天气
    * */
      private void queryWeatherInfo(String weatherCode){
      String address ="http://wthrcdn.etouch.cn/weather_mini?citykey="+weatherCode;

      //String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";

        queryFromServer(address,"weatherCode");
    }
    /*根据传入的地址和类型，去向服务器查询天气的代号或天气信息

     */
    private void queryFromServer(final String address,final String type) {
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


                } else if ("weatherCode".equals(type) || "cityname".equals(type)) {
                    //处理从服务器返回的信息
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
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
        nowTemptext.setText("当前温度"+" "+prefs.getString("wendu", "")+"℃");
        D1weatherDespText.setText(prefs.getString("diwen","")+"、"+prefs.getString("gaowen","")+"、"+
               prefs.getString("fengxiang","")+ prefs.getString("fengli","")+"、"+prefs.getString("type","")+"天");
               date2.setText("明天:"+" "+prefs.getString("d2",""));
        D2weatherDespText.setText(prefs.getString("diwen1","")+"、"+prefs.getString("gaowen1","")+"、"+
               prefs.getString("fengxiang1","")+ prefs.getString("fengli1","")+"、"+prefs.getString("type1","")+"天");
        zhuyiText.setText("天气提示："+" "+prefs.getString("zhuyi",""));
               date1.setText("今天:" +" "+ prefs.getString("d1", ""));
        currentDataText.setText(prefs.getString("current_data", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this,AutoUpdateService.class);
        startService(intent);

    }
}
