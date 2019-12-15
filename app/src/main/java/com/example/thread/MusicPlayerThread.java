package com.example.thread;

import android.content.Context;
import android.content.Intent;
import com.example.receiver.MusicUpdateMedia;
import com.example.util.Constant;

public class MusicPlayerThread extends Thread
{
	MusicUpdateMedia mum;
	Context context;
	int threadNumber;
	
	public MusicPlayerThread(MusicUpdateMedia mum , Context context , int threadNumber)
	{
		this.mum=mum;
		this.context=context;
		this.threadNumber=threadNumber;
	}
	
	public void run() 
	{
		looper: while (mum.threadNumber==threadNumber) 
		{
			if (mum.status == Constant.STATUS_STOP||mum.mp==null)
			{
				break looper;
			}
			int duration = 0;
			int current = 0;
			try 
			{
				if (mum.mp != null && mum.threadNumber==threadNumber
						&& (mum.status == Constant.STATUS_PLAY || mum.status == Constant.STATUS_PAUSE)) {
					duration = mum.mp.getDuration();
					current = mum.mp.getCurrentPosition();
				}

				Intent intent = new Intent(Constant.UPDATE_STATUS);
				intent.putExtra("status",Constant.COMMAND_GO);
				intent.putExtra("status2",mum.status);
				intent.putExtra("duration", duration);
				intent.putExtra("current", current);
				context.sendBroadcast(intent);
				
				Intent intentwidget = new Intent(Constant.WIDGET_SEEK);
				intentwidget.putExtra("status",mum.status);
				intentwidget.putExtra("duration", duration);
				intentwidget.putExtra("current", current);
				context.sendBroadcast(intentwidget);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			try 
			{
				Thread.sleep(100);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
}