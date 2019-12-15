package com.example.receiver;

import java.util.ArrayList;

import com.example.service.MusicService;
import com.example.thread.MusicPlayerThread;
import com.example.util.Constant;
import com.example.util.DBUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.Toast;

public class MusicUpdateMedia extends BroadcastReceiver 
{
	public MediaPlayer mp;
	public int status = Constant.STATUS_STOP;
	public int playMode;
	public int threadNumber;
	
	Context context;

	MusicService ms;
	
	public MusicUpdateMedia(MusicService ms) 
	{
		this.ms = ms;//获取Service的引用
	}
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		this.context=context;
		switch (intent.getIntExtra("cmd", -1)) 
		{
		case Constant.COMMAND_PLAYMODE://更换播放模式
			playMode = intent.getIntExtra("playmode",Constant.PLAYMODE_SEQUENCE);
			break;
		case Constant.COMMAND_START://Media初始化
			NumberRandom();//产生随机数。用于判断更新进度的线程MusicPlayerThread是否应该继续运行。
			String musicpath = intent.getStringExtra("path");
			int musiccurrent = intent.getIntExtra("current", 0);
			if(musiccurrent==0)//如果进度为0则不需要进行此操作。
			{
				return;
			}
			if(mp!=null)
			{
				mp.release();
			}

			mp = new MediaPlayer();
			mp.setOnCompletionListener(new OnCompletionListener() 
			{
				//设置播放完成后的操作.
				@Override
				public void onCompletion(MediaPlayer mp) 
				{
					NumberRandom();
					onComplete(mp);

				}
			});
			if(musicpath==null)//如果拨放路径唯恐则返回。
			{
				return;
			}
			try {
				boolean flag=intent.getBooleanExtra("flag", true);
				mp.setDataSource(musicpath);//设置MediaPlayer数据源
				mp.prepare();//MediaPlayer的准备

				mp.start();//开始播放
				mp.seekTo(musiccurrent);//跳转进度
				status = Constant.STATUS_PLAY;//设置播放状态为播放
				if(flag)//如果标志位为真，则歌曲暂停。在进入Activity时起作用。
				{
					mp.pause();

					status = Constant.STATUS_PAUSE;
				}
				new MusicPlayerThread(this, context, threadNumber).start();//创建歌曲进度更新进程
			} catch (Exception e) {
				e.printStackTrace();

				NumberRandom();
			}
			break;
		case Constant.COMMAND_PLAY:
			String path = intent.getStringExtra("path");
			if (path != null) //如果路径为空则表示歌曲从暂停状态到播放状态
			{
				commandPlay(path);
			} 
			else //否则表示播放一首新歌
			{
				mp.start();//播放歌曲
			}
			status = Constant.STATUS_PLAY;//更改播放状态
			break;
		case Constant.COMMAND_STOP://停止
			NumberRandom();
			status = Constant.STATUS_STOP;
			if(mp!=null)
			{
				mp.release();
			}

			break;
		case Constant.COMMAND_PAUSE://暂停
			status = Constant.STATUS_PAUSE;
			mp.pause();

			break;
		case Constant.COMMAND_PROGRESS://拖动进度
			int current = intent.getIntExtra("current", 0);
			mp.seekTo(current);
			break;
		}

	}

	public void onComplete(MediaPlayer mp) 
	{
		SharedPreferences sp = ms.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);//获取SharedPreferences的引用
		int musicid = sp.getInt(Constant.SHARED_ID, -1);//获得正在播放的音乐id
		int playMode = sp.getInt("playmode", Constant.PLAYMODE_SEQUENCE);//获得当前的播放模式
		int list=sp.getInt(Constant.SHARED_LIST, Constant.LIST_ALLMUSIC);//获得当前的播放列表
		ArrayList<Integer> musicList = DBUtil.getMusicList(list);//获得歌曲播放列表
		if(musicid==-1)//如果当前播放歌曲不存在则返回
		{
			return;
		}
		if(musicList.size()==0)//如果播放列表为空则返回
		{
			return;
		}
		String playpath;
		switch (playMode)
		{
		case Constant.PLAYMODE_REPEATSINGLE://单曲循环模式
			playpath = DBUtil.getMusicPath(musicid);//获得歌曲地址
			commandPlay(playpath);
			break;
		case Constant.PLAYMODE_REPEATALL://列表循环模式
			musicid = DBUtil.getNextMusic(musicList,musicid);//获得下一首歌曲
			playpath = DBUtil.getMusicPath(musicid);
			commandPlay(playpath);
			break;
		case Constant.PLAYMODE_SEQUENCE://列表播放模式
			if (musicList.get(musicList.size() - 1) == musicid) //判断是否为播放列表的最后一首
			{
				mp.release();
				status = Constant.STATUS_STOP;
				Toast.makeText(context, "已到达播放列表的最后，请重新选歌", Toast.LENGTH_LONG).show();
			} 
			else 
			{
				musicid = DBUtil.getNextMusic(musicList,musicid);
				playpath = DBUtil.getMusicPath(musicid);
				commandPlay(playpath);
			}
			break;
		case Constant.PLAYMODE_RANDOM://随机播放模式
			musicid = DBUtil.getRandomMusic(musicList, musicid);//获得随机音乐id
			playpath = DBUtil.getMusicPath(musicid);
			commandPlay(playpath);
			break;
		}
		SharedPreferences.Editor spEditor = sp.edit();//获得编辑SharedPreferences的引用
		spEditor.putInt(Constant.SHARED_ID, musicid);//保存音乐id
		spEditor.commit();
	}


	public void NumberRandom() 
	{
		int numberTemp;
		do 
		{
			numberTemp = (int) (Math.random() * 30);
		}
		while (numberTemp == threadNumber);
		threadNumber = numberTemp;
	}
	
	private void commandPlay(String path) //播放音乐的方法
	{
		NumberRandom();//改变线程编号。
		if (mp != null) 
		{
			mp.release();//释放播放器
		}

		mp = new MediaPlayer();  
		mp.setOnCompletionListener(new OnCompletionListener()//设置完成监听
		{
			@Override
			public void onCompletion(MediaPlayer mp)
			{
				NumberRandom();
				onComplete(mp);

			}
		});
		try 
		{
			mp.setDataSource(path);//给MediaPlayer设置数据源
			mp.prepare();//准备MediaPlayer
			mp.start();//开始播放
			new MusicPlayerThread(this, context, threadNumber).start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			NumberRandom();
		}
		status = Constant.STATUS_PLAY;
	}
}