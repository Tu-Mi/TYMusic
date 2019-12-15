package com.example.service;

import com.example.receiver.MusicUpdateMedia;
import com.example.util.Constant;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.IBinder;

public class MusicService extends Service 
{
	MusicUpdateMedia mc;

	
	@Override
	public void onCreate() 
	{
		super.onCreate();

		mc = new MusicUpdateMedia(this);
		mc.mp = new MediaPlayer();
		mc.status = Constant.STATUS_STOP;

		//为Service注册广播接收器MusicUpdateMedia
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.MUSIC_CONTROL);
		this.registerReceiver(mc, filter);
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;//本程序不涉及此方法
	}






	

	

	

	

}