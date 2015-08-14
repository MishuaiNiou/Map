package com.example.mymap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends Activity {

	private SearchView searchView;
	private MapView mapView;
	private BaiduMap baiduMap;
	
	private double mLatitude;
	private double mLongitude;
	
	private LocationClient locationClient;
	private MyLocationListener listener = new MyLocationListener();
	
	private boolean isFirstLoc = true;
	
	BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);
	
	private ImageView currentLocation;
	private ImageView route;
	
	
	private Sensor sensor;
	private SensorManager sensorManager;
	private float lastX;
	
	private MyOrientationListener orientationListener;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        searchView = (SearchView) findViewById(R.id.searchView);
        currentLocation = (ImageView) findViewById(R.id.currentLocation);
        route = (ImageView) findViewById(R.id.route);
        
        mapView = (MapView)findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        
        baiduMap.setMyLocationEnabled(true);
        
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(listener);
        
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setNeedDeviceDirect(true);
        option.setIsNeedAddress(true);
        
        locationClient.setLocOption(option);
        
        MyLocationConfiguration configuration = new MyLocationConfiguration(null, true, descriptor);
        
        orientationListener = new MyOrientationListener();
        
        
        baiduMap.setMyLocationConfigeration(configuration);
        
        locationClient.start();
        
        
        
        
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
        
        currentLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LatLng ll = new LatLng(mLatitude, mLongitude);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				baiduMap.animateMapStatus(u);
			}
		});
        
        route.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(MainActivity.this , RouteActivity.class);
//				startActivity(intent);
			}
		});
        
    }
    
    
    public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if(location == null || mapView == null)
				return;
		
			MyLocationData locationData = new MyLocationData.Builder()
				.accuracy(location.getRadius())
				.direction(lastX)
				.latitude(location.getLatitude())
				.longitude(location.getLongitude())
				.build();
			
			baiduMap.setMyLocationData(locationData);
			
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();
			
			if(isFirstLoc){
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				baiduMap.animateMapStatus(u);
			}
			
		}
    	
    }
    
    
    public class MyOrientationListener implements SensorEventListener{
    	
		public MyOrientationListener() {
			super();
			sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
//			sensorManager.registerListener(orientationListener, sensor, SensorManager.SENSOR_MAX);
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)
			{
				float x = event.values[SensorManager.DATA_X];

				if (Math.abs(x - lastX) > 1.0){
					lastX = x;
				}
				
			}
		}
    	
    }
    
    
    @Override
    protected void onResume() {
    	sensorManager.registerListener(orientationListener, sensor, SensorManager.SENSOR_MAX);
    	mapView.onResume();
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	mapView.onPause();
    	super.onPause();
    	sensorManager.unregisterListener(orientationListener);
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	locationClient.stop();
    	baiduMap.setMyLocationEnabled(false);
    	mapView.onDestroy();
    	super.onDestroy();
    }
}
