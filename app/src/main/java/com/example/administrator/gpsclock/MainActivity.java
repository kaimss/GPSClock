package com.example.administrator.gpsclock;

import android.app.Service;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

public class MainActivity extends AppCompatActivity implements LocationSource,AMapLocationListener {
private Circle circle;
private Marker marker;
    private AMap aMap;//定义AMap 地图对象的操作方法与接口。
    private OnLocationChangedListener  mListener;//位置发生变化时的监听
    private MapView mMapView;//显示地图的视图
    public AMapLocationClient mlocationClient;//定位服务类。此类提供单次定位、持续定位、地理围栏、最后位置相关功能。
    public AMapLocationClientOption mLocationOption;//定位参数设置，通过这个类可以对定位的相关参数进行设置
                                                       //在AMapLocationClient进行定位时需要这些参数
    private boolean isSet=true;
    private boolean isFirst=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
        initLoc();




    }

    private void initLoc(){
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.moveCamera(CameraUpdateFactory.zoomTo(25));// 设置地图可视缩放大小
        aMap.getUiSettings().setCompassEnabled(true);// 设置指南针
        aMap.getUiSettings().setScaleControlsEnabled(true);// 设置比例尺
        aMap.getCameraPosition(); //方法可以获取地图的旋转角度
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //自定义定位蓝点图标，不设置显示系统小蓝点
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
//                fromResource(R.drawable.i5));
        //自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.BLACK);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth((float) 0.1);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mlocationClient.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mlocationClient.stopLocation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }
    private MarkerOptions getMarkerOptions(LatLng point) {
        //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.timg));
        //位置
        options.position(new LatLng(point.latitude, point.longitude));

       // options.period(60);
        //options.snippet("目的地");
        options.title("目的地").snippet("thanks");

        return options;

    }
    /*
    private void addCirclemy(LatLng latLng){
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(300);
        circleOptions.strokeColor(Color.YELLOW);
        circleOptions.fillColor(Color.BLUE);
        circleOptions.strokeWidth(8);
       aMap.addCircle(circleOptions);
    }*/
    //定位回调函数
    @Override
    public void onLocationChanged(final AMapLocation aMapLocation) {
        if (aMapLocation != null) {

                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息



            if (isFirst) {

                //设置缩放级别
                //aMap.moveCamera(CameraUpdateFactory.zoomTo(1));
                //将地图移动到定位点
               // aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                isFirst = false;

                //点击定位按钮 能够将地图的中心移动到定位点
                mListener.onLocationChanged(aMapLocation);
                //获取定位信息
                StringBuffer buffer = new StringBuffer();
                buffer.append(aMapLocation.getCountry() + ""
                        + aMapLocation.getProvince() + ""
                        + aMapLocation.getCity() + ""
                        + aMapLocation.getProvince() + ""
                        + aMapLocation.getDistrict() + ""
                        + aMapLocation.getStreet() + ""
                        + aMapLocation.getStreetNum());
                Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();

                Button button1 = (Button) findViewById(R.id.button1);
                Button button2 = (Button) findViewById(R.id.button2);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        switch(v.getId()){
                            case R.id.button1:{
                                isSet=true;
                                if(isSet){
                                    Toast.makeText(getApplicationContext(), "点击设置闹钟", Toast.LENGTH_LONG).show();
                                    aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
                                        @Override
                                        public void onMapClick(LatLng latLng) {

                                            double latitude = latLng.latitude;
                                            double longitude = latLng.longitude;

                                            LatLng point=new LatLng(latitude,longitude);
                                            LatLng point2=new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                                            if(marker!=null) {
                                                marker.remove();
                                                circle.remove();
                                            }

                                            CircleOptions circleOptions=new CircleOptions();
                                            circleOptions.center(latLng);
                                            circleOptions.radius(300);
                                            circleOptions.strokeColor(Color.YELLOW);
                                            circleOptions.fillColor(Color.BLUE);
                                            circleOptions.strokeWidth(8);
                                            circle=aMap.addCircle(circleOptions);
                                            marker=aMap.addMarker(getMarkerOptions(point));
                                            float distance = AMapUtils.calculateLineDistance(point,point2);
                                            Toast.makeText(getApplicationContext(), "当前距离"+distance, Toast.LENGTH_LONG).show();
                                            if(distance<=300){
                                                Vibrator vb=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                                                vb.vibrate(5000);

                                            }


                                        }
                                    });
                                }
                            }
                            break;
                            default:
                                break;

                        }
                    }
                });
                button2.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        switch(v.getId()){
                            case R.id.button2:{
                                marker.remove();
                                circle.remove();


                                isSet=false;

                                break;}
                            default:
                                break;
                        }
                    }
                });
            }

        } else {
            String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            Log.e("AmapErr", errText);

        }

    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(2000);
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }
    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }



}