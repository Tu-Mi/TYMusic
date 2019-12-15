package com.example.receiver;

import java.util.ArrayList;

import com.example.activity.MusicActivityPlay;
import com.example.bnmusic.R;
import com.example.util.Constant;
import com.example.util.DBUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicUpdatePlay extends BroadcastReceiver
{
	public static int status = Constant.STATUS_STOP;
	public boolean seek_play_touch=true;
	MusicActivityPlay pa;
	ArrayList<String> musicinfo;
	int oldmusicid;
	int duration = 0;
	int current = 0;

	public MusicUpdatePlay(MusicActivityPlay pa)
	{
		this.pa = pa;
		oldmusicid = -1;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		//获取播放状态
		int tempStatus = intent.getIntExtra("status", -1);

		//获取控件引用
		ImageView ib_play = (ImageView) pa.findViewById(R.id.player_iv_play);


		//得到播放歌曲的id
		SharedPreferences sp = pa.getSharedPreferences
				("music",Context.MODE_MULTI_PROCESS);
		int musicid = sp.getInt(Constant.SHARED_ID, -1);

		//尝试更新歌词、歌曲以的信息
		try
		{
			TextView tv_gequ = (TextView) pa.findViewById(R.id.player_textView_gequ_w);
			TextView tv_geshou = (TextView) pa.findViewById(R.id.player_textView_geshou_w);
			tv_gequ.setText(DBUtil.getMusicInfo(musicid).get(1));
			tv_geshou.setText(DBUtil.getMusicInfo(musicid).get(2));
			//是否存在于我喜欢列表
			ImageView iv_like = (ImageView) pa.findViewById(R.id.player_imageView_like_w);
			if(DBUtil.getILikeStatus(musicid))
			{
				iv_like.setImageResource(R.drawable.player_liked_w);
				pa.like=true;
			}
			else
			{
				iv_like.setImageResource(R.drawable.player_like_w);
				pa.like=false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		//获得当前播放歌曲的信息 ;
		ArrayList<String> musicinfo = DBUtil.getMusicInfo(musicid);
		String music = musicinfo.get(1);
		String singer = musicinfo.get(2);

		switch (tempStatus)
		{
			case Constant.STATUS_PLAY:
				MusicUpdateMain.status = tempStatus;
				status = tempStatus;
				break;
			case Constant.STATUS_STOP:
				MusicUpdateMain.status = tempStatus;//更改播放状态
				status = tempStatus;
				break;
			case Constant.STATUS_PAUSE:
				MusicUpdateMain.status = tempStatus;//更改播放状态
				status = tempStatus;
				break;
			case Constant.COMMAND_GO:
				if (seek_play_touch)
				{
					//设置播放进度
					SeekBar sb = (SeekBar) pa.findViewById(R.id.player_seekBar_w);
					duration = intent.getIntExtra("duration", 0);
					current = intent.getIntExtra("current", 0);
					sb.setMax(duration);
					sb.setProgress(current);

					//计算本首歌长度和播放进度
					TextView tv_current = (TextView) pa.findViewById(R.id.play_textView_time_current);
					TextView tv_duration = (TextView) pa.findViewById(R.id.play_textView_time_duration);
					tv_current.setText(fromMsToMinuteStr(current));
					tv_duration.setText(fromMsToMinuteStr(duration));

				}
				break;
		}
		if (status == Constant.STATUS_PLAY) //改变musicactivityplay界面开始\暂停按钮。设置歌词控件的显示。单双行切换
		{
			ib_play.setImageResource(R.drawable.player_pause_w);
		}
		else
		{
			ib_play.setImageResource(R.drawable.player_play_w);
		}

	}

	public String fromMsToMinuteStr(int ms)
	{
		ms = ms / 1000;
		int minute = ms / 60;
		int second = ms % 60;
		return minute + ":" + ((second > 9) ? second : "0" + second);
	}
}
