package com.example.mymap;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class MyMapApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());
	}
}
