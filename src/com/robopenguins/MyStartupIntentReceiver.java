package com.robopenguins;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyStartupIntentReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		DBAdapter dbAdapter=new DBAdapter(context);
		dbAdapter.open();
		AlarmParams alarm=dbAdapter.getAlarm();
		dbAdapter.close();
		System.out.println("Initializing Alarm");
		if (alarm!=null)
		{
			EditReminder.setRecurringAlarm(context, alarm.hour, alarm.min);
		}
		
	}
	}
