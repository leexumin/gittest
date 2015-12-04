package app.leeweather.com.gittest;

import android.app.Activity;
import android.content.DialogInterface;
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
    //用于显示发布时间
    private TextView publishText;
    //用于显示天气描述
    private TextView weatherDespText;
    //用于显示气温
    private TextView temp1Text;
    private TextView temp2Text;
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
        publishText = (TextView)findViewById(R.id.publish_text);
        weatherDespText = (TextView)findViewById(R.id.weather_desp);
        temp1Text = (TextView)findViewById(R.id.temp1);
        temp2Text = (TextView)findViewById(R.id.temp2);
        currentDataText= (TextView)findViewById(R.id.current_data);
        String countyCode =getIntent().getStringExtra("county_code");


       if (!TextUtils.isEmpty(countyCode)){
            //有县级代号时就去查询天气

            publishText.setText("正在很努力地同步...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);


    }else {
        //没有县级代号时就直接查询显示本地天气
            showWeather();
        }
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather =(Button)findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
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
                   publishText.setText("正在很努力的同步...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode =  prefs.getString("weather_code","");
                if (!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
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

        String address = "http://www.weather.com.cn/data/list3/city" +  countyCode + ".xml";
        queryFromServer(address,"countyCode");



    }
    /*
    查询天气代号所对应的天气
    * */
    private void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address,"weatherCode");
    }
    /*根据传入的地址和类型，去向服务器查询天气的代号或天气信息

     */
    private void queryFromServer(final String address,final String type) {
        HttpUtil.senddHttpRequest(address, new HttpCallbackListener() {
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


                } else if ("weatherCode".equals(type)) {
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
                        publishText.setText("同步失败了哟");
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
        temp2Text.setText(prefs.getString("temp2",""));
        temp1Text.setText(prefs.getString("temp1",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publishText.setText("今天"+ prefs.getString("publish_time","")+"发布");
        currentDataText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

    }
}
