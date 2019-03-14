
	package com.robopenguins;

	import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;




	public class EditReminder extends Activity 
	{	
		private RadioButton notifyButton;
		private RadioButton soundButton;
		private RadioButton vibrateButton;
		private TimePicker timePicker;
		DBAdapter dbAdapter;
		Uri ringTonePath=null;

		private class EnableCheckHandler implements OnClickListener {
			
			public void onClick(View arg0) {
				
				boolean notify=notifyButton.isChecked();
				boolean sound=soundButton.isChecked();
				boolean vibrate=vibrateButton.isChecked();
				
				
				if(notify||sound||vibrate)
				{
					AlarmParams alarm=new AlarmParams(timePicker.getCurrentHour(), timePicker.getCurrentMinute(),notify,sound,vibrate,ringTonePath);
					dbAdapter.open();
					dbAdapter.setAlarm(alarm);
					dbAdapter.close();
					setRecurringAlarm(getApplicationContext(),alarm.hour,alarm.min);
				}
				else
				{
					dbAdapter.open();
					dbAdapter.clearAlarm();
					dbAdapter.close();
					clearAlarm(getApplicationContext());
				}
				Toast toast=Toast.makeText(getApplicationContext(), "Reminder Saved", 2000);
			     toast.setGravity(Gravity.TOP, -30, 50);
			     toast.show();
				finish();
			}
		}
		
		private class BackButtonHandler implements OnClickListener {
			
			public void onClick(View arg0) {
				setResult(Activity.RESULT_OK, null);
				finish();
			}
		}
		
		private class SetSoundHandler implements OnClickListener {
			
			public void onClick(View arg0) {
	            Intent intent = new Intent( RingtoneManager.ACTION_RINGTONE_PICKER);
	            intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TYPE,
	            RingtoneManager.TYPE_NOTIFICATION);
	            intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
	          
	            intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,ringTonePath);
	            
            	startActivityForResult( intent,999);
			}
		}
		
		
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (resultCode == RESULT_OK) {
	            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	            if (uri != null) {
	                ringTonePath = uri;
	            }
			}
		}
		
		public void clearAlarm(Context context)
		{
			Intent downloader = new Intent(context, AlarmReceiver.class);
			PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
					0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
			
			AlarmManager alarms = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarms.cancel(recurringDownload);
			
		}
		
		public static void setRecurringAlarm(Context context,int hour,int min) {
			 
		
			Calendar updateTime = Calendar.getInstance();
			updateTime.setTimeZone(TimeZone.getDefault());
			updateTime.setTimeInMillis(System.currentTimeMillis());
			updateTime.set(Calendar.HOUR_OF_DAY, hour);
			updateTime.set(Calendar.MINUTE, min);
			updateTime.set(Calendar.SECOND, 0);
			
			Intent downloader = new Intent(context, AlarmReceiver.class);
			PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
					0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarms = (AlarmManager) context.getSystemService(ALARM_SERVICE);
			alarms.setRepeating(AlarmManager.RTC_WAKEUP,
					updateTime.getTimeInMillis(),
					AlarmManager.INTERVAL_DAY, recurringDownload);
			
		
		}
		
		public void onCreate(Bundle savedInstanceState) 
		{
		        super.onCreate(savedInstanceState);
		        dbAdapter=new DBAdapter(this);
		        
		    	LayoutInflater inflater = LayoutInflater.from(this);
		    	LinearLayout layout = new LinearLayout(this);
				inflater.inflate(R.layout.editreminder, layout);
				
				dbAdapter.open();
				AlarmParams alarm= dbAdapter.getAlarm();
				dbAdapter.close();
				
				notifyButton=(RadioButton) layout.findViewById(R.id.notificationButton);
				soundButton=(RadioButton) layout.findViewById(R.id.soundButton);
				vibrateButton=(RadioButton) layout.findViewById(R.id.vibrateButton);
				
				timePicker=(TimePicker) layout.findViewById(R.id.timePicker);
				
				if(alarm!=null)
				{
					notifyButton.setChecked(alarm.notification);
					soundButton.setChecked(alarm.sound);
					vibrateButton.setChecked(alarm.vibrate);
					
					
					timePicker.setCurrentHour(alarm.hour);
					timePicker.setCurrentMinute(alarm.min);
					
					ringTonePath=alarm.ringtone;
				}
				
				((Button) layout.findViewById(R.id.reminderback)).setOnClickListener(new BackButtonHandler());
				((Button) layout.findViewById(R.id.reminderSaveButton)).setOnClickListener(new EnableCheckHandler());
				((Button) layout.findViewById(R.id.setSoundButton)).setOnClickListener(new SetSoundHandler());
				
		        setContentView(layout);
		}
	
}
