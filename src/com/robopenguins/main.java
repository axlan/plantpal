package com.robopenguins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class main extends Activity {

	ArrayList<Plant> plants;
	HashMap<Plant, TableRow> plantRows;
	
	DBAdapter dbAdapter;
	
	NotificationManager mNM;

	private class AddButtonHandler implements OnClickListener {
		
		public void onClick(View arg0) {
			handleAddButtonClick();
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	public void startNotification()
	{
		  mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		  String text="Plants need Watering!";
	        // Set the icon, scrolling text and timestamp
	        Notification notification = new Notification(R.drawable.water, text,
	                System.currentTimeMillis());

	        // The PendingIntent to launch our activity if the user selects this notification
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	                new Intent(this, main.class), 0);

	        // Set the info for the views that show in the notification panel.
	        notification.setLatestEventInfo(this, "PlantPal",
	                       text, contentIntent);

	        // Send the notification.
	        // We use a layout id because it is a unique number.  We use it later to cancel.
	        mNM.notify(R.string.app_name, notification);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.reminder:
	    	Intent data =new Intent(this, EditReminder.class);
			startActivity(data);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private TableRow GenerateEntry(final Plant plant) {
		TableRow row = new TableRow(this);

		ImageView mood = new ImageView(this);
		
		plant.CheckWatering(new Date(System.currentTimeMillis()));
		
		dbAdapter.open();
		dbAdapter.updatePlant(plant.getName(), plant);
		dbAdapter.close();

		switch (plant.getMood()) {
		case HAPPY:
			mood.setImageResource(R.drawable.happy);
			break;
		case CONTENT:
			mood.setImageResource(R.drawable.content);
			break;
		case BLUE:
			mood.setImageResource(R.drawable.blue);
			break;
		case SAD:
			mood.setImageResource(R.drawable.sad);
			break;
		}

		row.addView(mood);

		TextView name = new TextView(this);
		name.setText(plant.getName());
		name.setWidth(100);
		name.setTextColor((255 << 24) | (0 << 16) | (51 << 8) | 0);
		row.addView(name);

		TextView date = new TextView(this);

		int daysBetween=plant.GetDaysToNextWatering(new Date(System.currentTimeMillis()));

		if (daysBetween<0) {
			date.setTextColor(Color.RED);
			if(daysBetween==-1)
			{
				date.setText("1 day late!");
			}
			else
			{
				date.setText(""+(-daysBetween) + " days late!");
			}
		}
		else if (daysBetween>0) {
			date.setTextColor(Color.GREEN);
			if(daysBetween==1)
			{
				date.setText("Water in 1 day");
			}
			else
			{
				date.setText("Water in " + daysBetween + " days");
			}
		} else {
			date.setTextColor(Color.BLUE);
			date.setText("Water Today!");

		}

		row.addView(date);

		ImageView water = new ImageView(this);
		water.setImageResource(R.drawable.water);
		water.setPadding(25,0,0,0);
		if(daysBetween<=0)
		{
			water.setOnClickListener(
				new OnClickListener( ) // start anonymous inner class 
				{
					
					public void onClick(View arg0)  // provide the method...
					{ 
						plant.Water();
						dbAdapter.open();
						dbAdapter.updatePlant(plant.getName(), plant);
						dbAdapter.close();
						relist();
					}
				} //end of anonymous inner class
			);
		}
		//water.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.));
		row.addView(water);

		row.setBackgroundResource(R.drawable.border);
		
		final Activity caller=this;
		
		row.setOnLongClickListener(
				new OnLongClickListener() // start anonymous inner class 
				{

					public boolean onLongClick(View v) {
						Intent data =new Intent(caller, EditAct.class);
						data.putExtra("type", "edit");
						data.putExtra("name", plant.name);
						startActivityForResult(data, 0);
						return true;
					}
				} //end of anonymous inner class
					
					
			);

		return row;
	}

	
	
	public void relist()
	{
		dbAdapter.open();
		plants = dbAdapter.getAllEntries();
		dbAdapter.close();
		
		LinearLayout layout = new LinearLayout(this);

		layout.setOrientation(LinearLayout.VERTICAL);

		LayoutInflater inflater = LayoutInflater.from(this);
		TableLayout statusTitle = new TableLayout(this);
		inflater.inflate(R.layout.statustitle, statusTitle);

		((Button) statusTitle.findViewById(R.id.addbutton)).setOnClickListener(new AddButtonHandler());
		
		layout.addView(statusTitle);

		TableLayout table = new TableLayout(this);

	

		Collections.sort(plants);

		for (Plant plant : plants) {
			table.addView(GenerateEntry(plant));

		}

		layout.addView(table);

		setContentView(layout);
	}
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dbAdapter=new DBAdapter(this);
	

		relist();
		
		// setContentView(R.layout.main);
	}

	private void handleAddButtonClick() {
		Intent data =new Intent(this, EditAct.class);
		data.putExtra("type", "add");
		startActivityForResult(data, 0);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data); 
		if (resultCode == Activity.RESULT_OK)
		{
			
			relist();
		}
			
	}

}