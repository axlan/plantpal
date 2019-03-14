package com.robopenguins;

import java.util.ArrayList;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Vibrator;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	NotificationManager mNM;
	private MediaPlayer mp;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			System.out.println("Heard Alarm");
			DBAdapter dbAdapter = new DBAdapter(context);
			dbAdapter.open();
			AlarmParams alarm = dbAdapter.getAlarm();
			ArrayList<Plant> plants = dbAdapter.getAllEntries();
			dbAdapter.close();
			

			int waterCount=0;
			
			for(Plant plant:plants)
			{

				int daysBetween=plant.GetDaysToNextWatering(new Date(System.currentTimeMillis()));

				if (daysBetween<=0) {
					waterCount++;
				}
				
			}
			if(waterCount>0)
			{
				// Send the notification.
				// We use a layout id because it is a unique number. We use it later
				// to cancel.
				if (alarm.notification) {
					mNM = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					// In this sample, we'll use the same text for the ticker and the
					// expanded notification
					CharSequence text;
					if(waterCount==1)
					{
						text = "1 plant needs watering!";
					}else
					{
						text = ""+waterCount+" plants need watering!";
					}
					// Set the icon, scrolling text and timestamp
					Notification notification = new Notification(R.drawable.water,
							text, System.currentTimeMillis());

					// The PendingIntent to launch our activity if the user selects this
					// notification
					PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
							new Intent(context, main.class), 0);

					// Set the info for the views that show in the notification panel.
					notification.setLatestEventInfo(context, "Plant Pal", text,
							contentIntent);

					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					mNM.notify(R.string.app_name, notification);
				}
				if (alarm.sound) {
					try
					{
						mp = MediaPlayer.create(context,alarm.ringtone);
					}
					catch(Exception e)
					{
						
					}
					if(mp==null)
					{
						mp = MediaPlayer.create(context, R.raw.alert);
					}
					mp.start();
					mp.setOnCompletionListener(new OnCompletionListener() {
	
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							mp.release();
						}
	
					});
				}
				if (alarm.vibrate) {
					// Get instance of Vibrator from current Context
					Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
					 
					// Vibrate for 300 milliseconds
					v.vibrate(300);
	
				}
			}
		} catch (Exception e) {
			Toast.makeText(
					context,
					"There was an error somewhere, but we still received an alarm",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();

		}
	}

}