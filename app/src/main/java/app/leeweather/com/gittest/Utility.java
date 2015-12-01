package app.leeweather.com.gittest;

import android.text.TextUtils;

/**
 * Created by leexumin on 2015/12/1.
 */
public class Utility {
    /*
    �����ʹ�����������ص�ʡ������
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
                    //���������������ݴ洢��province��
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /*
    ���������������ݴ洢��city��
     */
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities != null &&allCities.length>0){
                for (String c : allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                   city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //���������������ݴ洢��city��
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;

    }
    /*
   ���������������ݴ洢��county��
    */
    public synchronized static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCountries = response.split(",");
            if (allCountries != null &&allCountries.length>0){
                for (String c : allCountries){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //���������������ݴ洢��county��
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;

    }
}
