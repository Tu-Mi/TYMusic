package com.example.fragment;

import java.util.ArrayList;

import com.example.bnmusic.R;
import com.example.receiver.MusicUpdateMain;
import com.example.util.Constant;
import com.example.util.DBUtil;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MusicFragmentFour extends Fragment
{
	String title;
	public static BaseAdapter ba;
	ArrayList<Integer> musiclist;
	int playlistNumber;
	
	public MusicFragmentFour(String title)
	{
		this.title=title;
	}
	
	public MusicFragmentFour(String title,String playlistNumber)
	{
		this.title=title;
		this.playlistNumber=Integer.parseInt(playlistNumber);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		LinearLayout ll_main = (LinearLayout)inflater.inflate(R.layout.fragment_four, container, false);
		
		ImageButton ib_back = (ImageButton) ll_main.findViewById(R.id.four_imagebutton_back_l);
		ib_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.popBackStack();
			}
		});
		
		TextView tv_title=(TextView)ll_main.findViewById(R.id.four_textview_title_l);
		tv_title.setText(title);
		return ll_main;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(title.equals(Constant.FRAGMENT_ILIKE))
		{
			musiclist = DBUtil.getMusicList(Constant.LIST_ILIKE);
		}
		else if(title.equals(Constant.FRAGMENT_LASTPLAY))
		{
			musiclist = DBUtil.getMusicList(Constant.LIST_LASTPLAY);
		}
		else if(title.equals(Constant.FRAGMENT_MYLIST))
		{
			musiclist = DBUtil.getMusicList(playlistNumber);
		}
		
		setListView();
	}
	
	private void setListView()
	{
		ba = new BaseAdapter()
		{
			LayoutInflater inflater = LayoutInflater.from(getActivity());

			@Override
			public int getCount() {
				return musiclist.size() + 1;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) 
			{
				if (arg0 == musiclist.size())
				{
					LinearLayout lll = (LinearLayout) inflater.inflate(
							R.layout.listview_count, null).findViewById(
							R.id.linearlayout_null);
					TextView tv_sum = (TextView) lll.getChildAt(0);
					tv_sum.setText("共有" + musiclist.size() + "首歌曲\n\n\n");
					return lll;
				}
				String musicName = DBUtil.getMusicInfo(musiclist.get(arg0)).get(2);
				musicName+="-"+DBUtil.getMusicInfo(musiclist.get(arg0)).get(1);

				LinearLayout ll = (LinearLayout) inflater.inflate
						(R.layout.fragment_localmusic_listview_row,
						null).findViewById(R.id.LinearLayout_row);
				TextView tv = (TextView) ll.getChildAt(1);
				tv.setText(musicName);
				return ll;
			}
		};

		ListView lv = (ListView) getActivity().findViewById(R.id.web_listview_music);

		lv.setAdapter(ba);

		if(title.equals(Constant.FRAGMENT_MYLIST))
		{
			lv.setOnItemLongClickListener(new OnItemLongClickListener()
			{
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					final int selectTemp = arg2;
					AlertDialog.Builder builder = new Builder(getActivity());
					builder.setTitle("更多功能");
					builder.setItems(new String[]{"从歌单中删除"}, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							DBUtil.deleteMusicInList(musiclist.get(selectTemp), playlistNumber);
							musiclist = DBUtil.getMusicList(playlistNumber);
							ba.notifyDataSetChanged();
						}
					}).create().show();
					return false;
				}
			});
		}

		lv.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				// 如果点击的是最后一项
				if (arg2 == arg0.getCount() - 1) 
				{
					return;
				}
				
				SharedPreferences sp = getActivity().getSharedPreferences
						("music",Context.MODE_MULTI_PROCESS);
				SharedPreferences.Editor spEditor=sp.edit();
				if(title.equals(Constant.FRAGMENT_ILIKE))
				{
					spEditor.putInt(Constant.SHARED_LIST, Constant.LIST_ILIKE);
				}
				else if(title.equals(Constant.FRAGMENT_LASTPLAY))
				{
					spEditor.putInt(Constant.SHARED_LIST, Constant.LIST_LASTPLAY);
				}
				else if(title.equals(Constant.FRAGMENT_DOWNLOAD))
				{
					spEditor.putInt(Constant.SHARED_LIST, Constant.LIST_DOWNLOAD);
				}
				else if(title.equals(Constant.FRAGMENT_MYLIST))
				{
					spEditor.putInt(Constant.SHARED_LIST, playlistNumber);
				}
				
				spEditor.commit();
				
				// 获取所点歌曲的ID;
				int musicid = musiclist.get(arg2);

				// 播放所选歌曲
				boolean flag;
				int oldmusicplay = sp.getInt(Constant.SHARED_LIST, -1);

				// 判断是否点击的是正在播放的歌曲
				if (oldmusicplay == musicid) 
				{
					flag = false;
				} 
				else 
				{
					flag = true;
					spEditor.putInt(Constant.SHARED_ID, musicid);
					spEditor.commit();
				}

				ArrayList<String> musicinfo = DBUtil.getMusicInfo(musicid);
				TextView tv_gequ = (TextView) getActivity().findViewById(R.id.main_textview_gequ);
				TextView tv_geshou = (TextView) getActivity().findViewById(R.id.main_textview_geshou);
				tv_gequ.setText(musicinfo.get(1));
				tv_geshou.setText(musicinfo.get(2));

				if (flag) 
				{
					sendintent(Constant.STATUS_STOP);
				} 
				else 
				{
					if (MusicUpdateMain.status == Constant.STATUS_PLAY)
					{
						sendintent(Constant.STATUS_PLAY);
					} 
					else if (MusicUpdateMain.status == Constant.STATUS_STOP) 
					{
						sendintent(Constant.STATUS_STOP);
					}
					else 
					{
						sendintent(Constant.STATUS_PAUSE);
					}
				}
			}
		});
	}
	//发送广播
	private void sendintent(int code) 
	{
		switch (code) {
		case Constant.STATUS_PLAY:
			Intent intentplay = new Intent(Constant.MUSIC_CONTROL);
			intentplay.putExtra("cmd", Constant.COMMAND_PAUSE);
			this.getActivity().sendBroadcast(intentplay);
			break;
		case Constant.STATUS_STOP:
			SharedPreferences sp = getActivity().getSharedPreferences
					("music",Context.MODE_MULTI_PROCESS);
			int musicid = sp.getInt(Constant.SHARED_ID, -1);
			String playpath = DBUtil.getMusicPath(musicid);
			Intent intentstop = new Intent(Constant.MUSIC_CONTROL);
			intentstop.putExtra("cmd", Constant.COMMAND_PLAY);
			intentstop.putExtra("path", playpath);
			this.getActivity().sendBroadcast(intentstop);
			break;
		case Constant.STATUS_PAUSE:
			Intent intentpause = new Intent(Constant.MUSIC_CONTROL);
			intentpause.putExtra("cmd", Constant.COMMAND_PLAY);
			this.getActivity().sendBroadcast(intentpause);
			break;
		}
	}
}