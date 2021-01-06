package com.sunnyweather.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.sunnyweather.android.SunnyWeatherApplication.Companion.context
import com.sunnyweather.android.logic.model.Location

import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.ui.place.PlaceViewModel
import com.sunnyweather.android.ui.weather.WeatherActivity

import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {
    var mLocationClient: AMapLocationClient? = null
    var  mLocation:Location ? = null
    //声明定位回调监听器
    var mLocationOption: AMapLocationClientOption? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMap()
    }
    private fun initMap() {
        //初始化定位
        mLocationClient = AMapLocationClient(this@MainActivity)
        //设置定位回调监听
        mLocationClient!!.setLocationListener(mLocationListener)
        mLocationOption = AMapLocationClientOption()
        //设置定位模式为高精度模式，AMapLocationMode.Battery_Saving为低功耗模式，AMapLocationMode.Device_Sensors是仅设备模式
        mLocationOption!!.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mLocationOption!!.isNeedAddress = true //设置是否返回地址信息（默认返回地址信息）
        mLocationOption!!.isOnceLocation = false //设置是否只定位一次,默认为false
        mLocationOption!!.isWifiActiveScan = true //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption!!.isMockEnable = false //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption!!.interval = 15000 //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption!!.isOnceLocation = false //可选，是否设置单次定位默认为false即持续定位
        mLocationOption!!.isOnceLocationLatest =
            false //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        mLocationOption!!.isWifiScan =
            true //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mLocationOption!!.isLocationCacheEnable = true //可选，设置是否使用缓存定位，默认为true
        //给定位客户端对象设置定位参数
        mLocationClient!!.setLocationOption(mLocationOption)
        //启动定位
        mLocationClient!!.startLocation()
    }

    var mLocationListener =
        AMapLocationListener { aMapLocation ->
            if (aMapLocation != null) {
                if (aMapLocation.errorCode == 0) {
                    println("所在城市：" + aMapLocation.country + aMapLocation.province + aMapLocation.city)
                    val place = Place(aMapLocation.district,location = Location(aMapLocation.getLongitude().toString(),aMapLocation.getLatitude().toString()),aMapLocation.address)
                    val intent = Intent(context, WeatherActivity::class.java).apply {
                        putExtra("location_lng", place.location.lng )
                        putExtra("location_lat", place.location.lat)
                        putExtra("place_name", place.name)
                    }
                    startActivity(intent)
                    mLocationClient!!.stopLocation() //停止定位
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e(
                        "info", "location Error, ErrCode:"
                                + aMapLocation.errorCode + ", errInfo:"
                                + aMapLocation.errorInfo
                    )
                }
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        //销毁
        if (mLocationClient != null) {
            mLocationClient!!.onDestroy()
        }
    }

}
