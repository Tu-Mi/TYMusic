package com.example.fragment;

import com.example.bnmusic.R;
import com.example.util.Constant;
import com.example.util.DBUtil;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MusicFragmentMain extends Fragment
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_main, container, false);


		// 本地音乐跳转按钮
		LinearLayout main_ll_localmusic = (LinearLayout)v.findViewById(R.id.main_linearlayout_localmusic);

		//本地音乐播放键
		ImageButton ib_localplay=(ImageButton)v.findViewById(R.id.main_imageview_play);
		ib_localplay.setOnClickListener(
				new OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{
						SharedPreferences sp = getActivity().getSharedPreferences("music",
								Context.MODE_MULTI_PROCESS);
						int musicid = sp.getInt(Constant.SHARED_ID, -1);

						SharedPreferences.Editor spEditor = sp.edit();
						musicid = DBUtil.getRandomMusic(DBUtil.getMusicList(Constant.LIST_ALLMUSIC), musicid);

						spEditor.putInt(Constant.SHARED_ID, musicid);
						spEditor.putInt(Constant.SHARED_LIST, Constant.LIST_ALLMUSIC);
						spEditor.commit();

						String playpath = DBUtil.getMusicPath(musicid);
						Intent intentstop = new Intent(Constant.MUSIC_CONTROL);
						intentstop.putExtra("cmd", Constant.COMMAND_PLAY);
						intentstop.putExtra("path", playpath);
						getActivity().sendBroadcast(intentstop);
					}
				}
		);
		// 本地音乐点击操作
		main_ll_localmusic.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				TextView main_tv_localmusic = (TextView) v.findViewById(R.id.main_textview_localmusic);
				TextView main_tv_musicCount = (TextView) v.findViewById(R.id.main_textview_summusic);
				LinearLayout main_ll_localmusic = (LinearLayout) v.findViewById(R.id.main_linearlayout_localmusic);
				switch (event.getAction())
				{
					case MotionEvent.ACTION_UP:
						FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
						MusicFragmentLocalmusic fragment = new MusicFragmentLocalmusic();
						changeFragment(fragmentTransaction, fragment);
				}
				return false;
			}
		});

		//我喜欢按钮监听
		LinearLayout main_ll_ilike = (LinearLayout) v.findViewById(R.id.main_linearlayout_ilove);
		main_ll_ilike.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				LinearLayout lltemp = (LinearLayout) v;
				TextView tvtemp = (TextView) lltemp.getChildAt(1);
				switch (event.getAction())
				{
					case MotionEvent.ACTION_UP:
						FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
						MusicFragmentFour fragment=new MusicFragmentFour(Constant.FRAGMENT_ILIKE);
						changeFragment(fragmentTransaction, fragment);
				}
				return false;
			}
		});

		//最近播放按钮监听
		LinearLayout main_ll_lastplay = (LinearLayout) v.findViewById(R.id.main_linearlayout_lastplay);
		main_ll_lastplay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				LinearLayout lltemp = (LinearLayout) v;
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				MusicFragmentFour fragment=new MusicFragmentFour(Constant.FRAGMENT_LASTPLAY);
				changeFragment(fragmentTransaction, fragment);
				}
		});
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		// 本地音乐歌曲数量
		TextView main_tv_musicCount = (TextView) getActivity().findViewById(R.id.main_textview_summusic);
		main_tv_musicCount.setText(DBUtil.getMusicCount() + "首");
	}

	public static void changeFragment(FragmentTransaction fragmentTransaction,Fragment fragment)
	{

		fragmentTransaction.replace(R.id.main_linearlayout_l,fragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
}