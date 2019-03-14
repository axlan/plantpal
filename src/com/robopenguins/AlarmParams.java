package com.robopenguins;

import android.net.Uri;

public class AlarmParams {
	AlarmParams(){}
	
	
	

	public AlarmParams(int hour, int min, boolean notification, boolean sound,
			boolean vibrate,Uri ringtone) {
		super();
		this.hour = hour;
		this.min = min;
		this.notification = notification;
		this.sound = sound;
		this.vibrate = vibrate;
		this.ringtone=ringtone;
	}

	public int hour;
	public int min;
	public boolean notification;
	public boolean sound;
	public boolean vibrate;
	public Uri ringtone;
}
