package com.example.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.bnmusic.R;
import com.example.fragment.MusicFragmentMain;
import com.example.receiver.MusicUpdateMain;
import com.example.service.MusicService;
import com.example.util.Constant;
import com.example.util.DBUtil;


import android.os.Bundle;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.app.Notification;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

public class MusicActivityMain extends FragmentActivity
{
	public static int mainFragmentId;
	private PopupWindow popupWindow;
	MusicUpdateMain mu;
	SeekBar sb;

	String main_gequ;
	String main_geshou;
	public boolean Seekbar_touch = true;
	int progress_seekbar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 数据库的建立
		DBUtil.createTable();

		// 注册务器、
		mu = new MusicUpdateMain(this);

		//获取系统所有正在运行的服务，检测是否存在本软件的服务，如果不存在则启动并且发送初始化mediaplayer的请求
		ActivityManager mActivityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);

		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(100);
		if(!MusicServiceIsStart(mServiceList, Constant.SERVICE_CLASS))
		{
			this.startService(new Intent(this, MusicService.class));

			// 获得 歌曲，歌手
			SharedPreferences sp = this.getSharedPreferences("music",Context.MODE_PRIVATE);
			int musicid = sp.getInt(Constant.SHARED_ID, -1);
			int seek = sp.getInt("current", 0);

			//发送播放请求
			Intent intent_start = new Intent(Constant.MUSIC_CONTROL);
			intent_start.putExtra("cmd", Constant.COMMAND_START);
			intent_start.putExtra("path", DBUtil.getMusicPath(musicid));
			intent_start.putExtra("current", seek);
			this.sendBroadcast(intent_start);
		}
			// 获得却换界面的linearlayout
		LinearLayout ll_main_player = (LinearLayout) findViewById(R.id.main_linearlayout_play);
		//获得下一曲控件
		ImageView iv_next = (ImageView) findViewById(R.id.imageview_next);

		// seekbar添加监听
		sb = (SeekBar) findViewById(R.id.seekBar1);
		sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				Seekbar_touch = true;
				int musicid=getShared(Constant.SHARED_ID);

				Intent intent = new Intent(Constant.MUSIC_CONTROL);
				intent.putExtra("cmd", Constant.COMMAND_PROGRESS);
				intent.putExtra("current", progress_seekbar);
				MusicActivityMain.this.sendBroadcast(intent);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser)
			{
				progress_seekbar = progress;
				if (fromUser)
				{
					Seekbar_touch = false;
				}
			}
		});

		// linearlayout切面界面
		ll_main_player.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(MusicActivityMain.this,MusicActivityPlay.class);
				startActivity(intent);
			}
		});

		// 下一首监听
		iv_next.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//获取下一首ID
				int playMode = getShared("playmode");
				int musicid=getShared(Constant.SHARED_ID);

				ArrayList<Integer> musiclist = DBUtil.getMusicList(getShared(Constant.SHARED_LIST));
				if(playMode==Constant.PLAYMODE_RANDOM)
				{
					musicid = DBUtil.getRandomMusic(musiclist, musicid);
				}else
				{
					musicid = DBUtil.getNextMusic(musiclist, musicid);
				}
				setShared(Constant.SHARED_ID,musicid);

				//获oast取播放数据
				String path = DBUtil.getMusicPath(musicid);

				//发送播放请求
				Intent intent = new Intent(Constant.MUSIC_CONTROL);
				intent.putExtra("cmd", Constant.COMMAND_PLAY);
				intent.putExtra("path", path);
				MusicActivityMain.this.sendBroadcast(intent);
			}
		});

		// 播放按钮监听
		ImageView iv_play = (ImageView) findViewById(R.id.imageview_play);
		iv_play.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int musicid=getShared(Constant.SHARED_ID);

				if (musicid != -1)
				{
					if (MusicUpdateMain.status == Constant.STATUS_PLAY)
					{
						Intent intent = new Intent(Constant.MUSIC_CONTROL);
						intent.putExtra("cmd", Constant.COMMAND_PAUSE);
						MusicActivityMain.this.sendBroadcast(intent);
					}
					else if (MusicUpdateMain.status == Constant.STATUS_PAUSE)
					{
						Intent intent = new Intent(Constant.MUSIC_CONTROL);
						intent.putExtra("cmd", Constant.COMMAND_PLAY);
						MusicActivityMain.this.sendBroadcast(intent);
					}
					else
					{
						String path = DBUtil.getMusicPath(musicid);
						Intent intent = new Intent(Constant.MUSIC_CONTROL);
						intent.putExtra("cmd", Constant.COMMAND_PLAY);
						intent.putExtra("path", path);
						MusicActivityMain.this.sendBroadcast(intent);
					}
				}
			}
		});

		//注册广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.UPDATE_STATUS);
		this.registerReceiver(mu, filter);

		// 将fragment嵌入activity
		FragmentManager fragmentManager = this.getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		MusicFragmentMain fragment = new MusicFragmentMain();
		fragmentTransaction.add(R.id.main_linearlayout_l, fragment);//将一个fragment添加到Activity中。
		fragmentTransaction.addToBackStack(null);
		mainFragmentId=fragmentTransaction.commit();//将主界面保存，便于之后直接回到主界面的操作
	}

	public static boolean MusicServiceIsStart(List<RunningServiceInfo> mServiceList,
											  String serviceClass) //遍历服务项、检测本程序服务是否开启。
	{
		for (int i = 0; i < mServiceList.size(); i++)
		{
			if (serviceClass.equals(mServiceList.get(i).service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		FragmentManager fragmentManager = getFragmentManager();

		//后退按钮
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (getFragmentManager().getBackStackEntryCount() > 1)
			{
				fragmentManager.popBackStack();
				return true;
			}
		}
		//菜单按钮
		else if(keyCode == KeyEvent.KEYCODE_MENU)
		{

			LayoutInflater inflater = LayoutInflater.from(MusicActivityMain.this);
			View parentView = inflater.inflate(R.layout.activity_main, null);

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		//注册广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.UPDATE_STATUS);
		this.registerReceiver(mu, filter);

		//产生通知管理器
		Notification();
	}

	@SuppressWarnings("deprecation")
	public void Notification() {
		try {
			// 获取通知管理器
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

			// 创建Intent
			Intent intent = new Intent(this, MusicActivityMain.class);
			// 将Intent封装为PendingIntent
			PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

			// 创建Notification
			Notification myNotification = new Notification();
			// 设置Notification的图标
			myNotification.icon = R.drawable.ic_launcher;
			ArrayList<String> notification = DBUtil.getMusicInfo(getShared(Constant.SHARED_ID));

			// 发出Notification
			nm.notify(0, myNotification);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStop() {
		super.onStop();
		// 注销接收播放、暂停、停止状态更新Intent的UIUpdateReceiver
		this.unregisterReceiver(mu);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		//设置播放、暂停按钮
		ImageView iv_play = (ImageView) findViewById(R.id.imageview_play);
		if (MusicUpdateMain.status == Constant.STATUS_PLAY)
		{
			iv_play.setImageResource(R.drawable.player_pause_w);
		}else
		{
			iv_play.setImageResource(R.drawable.player_play_w);
		}


	}

	//设置sharedpreferences
	public void setShared(String key, int value)
	{
		SharedPreferences sp = this.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor spEditor=sp.edit();
		spEditor.putInt(key, value);
		spEditor.commit();
	}

	//获取sharedpreferences
	public int getShared(String key)
	{
		SharedPreferences sp = this.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		int value = sp.getInt(key, -1);
		return value;
	}
}
