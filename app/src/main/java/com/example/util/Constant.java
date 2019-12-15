package com.example.util;

public class Constant {//定义主类Constant
	//播放命令
	public static final int COMMAND_PLAY = 0; // 播放命令
	public static final int COMMAND_PAUSE = 1; // 暂停命令
	public static final int COMMAND_PROGRESS = 3; // 设置播放位置
	public static final int COMMAND_STOP = 15; // 停止命令
	public static final int COMMAND_GO = 7;
	public static final int COMMAND_START = 8;
	public static final int COMMAND_PLAYMODE=9;
	//播放状态
	public static final int STATUS_PLAY = 4; // 播放状态
	public static final int STATUS_PAUSE = 5; // 暂停状态
	public static final int STATUS_STOP = 6; // 停止状态
	//播放模式
	public static final int PLAYMODE_REPEATALL=10;
	public static final int PLAYMODE_REPEATSINGLE=11;
	public static final int PLAYMODE_SEQUENCE=-1;
	public static final int PLAYMODE_RANDOM=13;
	//handle常量
	public static final int DATABASE_ERROR = 0;
	public static final int DATABASE_COMPLETE = 1;
	public static final int PROGRESS_UPDATE = 2;

	//歌曲列表常量
	public static final int LIST_ALLMUSIC=-3;
	public static final int LIST_ILIKE=-2;
	public static final int LIST_LASTPLAY=-1;
	public static final int LIST_DOWNLOAD=-4;	
	//sharedpreferences
	public static final String SHARED="music";
	public static final String SHARED_ID="id";
	public static final String SHARED_LIST="list";

	//fragment播放
	public static final String FRAGMENT_ILIKE="我喜欢";
	public static final String FRAGMENT_MYLIST="我的歌单";
	public static final String FRAGMENT_DOWNLOAD="下载管理";
	public static final String FRAGMENT_LASTPLAY="最近播放";

	//IntentAction
	public static final String MUSIC_CONTROL = "kugoumusic.ACTION_CONTROL";
	public static final String UPDATE_STATUS = "kugoumusic.ACTION_STATUS";
	public static final String UPDATE_VISUALIZER = "kugoumusic.ACTION_VISUALIZER";

	public static final String WIDGET_STATUS = "kugoumusic.WIDGET_STATUS";
	public static final String WIDGET_SEEK = "kugoumusic.WIDGET_SEEK";

	//service名
	public static final String SERVICE_CLASS = "com.example.service.MusicService"; 
}