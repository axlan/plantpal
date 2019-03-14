package com.robopenguins;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;




public class EditAct extends Activity 
{	
	boolean newPlant;
	
	EditText nameText;
	EditText periodText;
	EditText startText;
	
	DBAdapter dbAdapter;
	
	private class SaveButtonHandler implements OnClickListener {
		
		public void onClick(View arg0) {
			handleSaveButtonClick();
		}
	}
	
	private class DeleteButtonHandler implements OnClickListener {
		
		public void onClick(View arg0) {
			handleDeleteButtonClick();
		}
	}
	
	private void handleSaveButtonClick() {
		LayoutInflater inflater = LayoutInflater.from(this);
    	LinearLayout layout = new LinearLayout(this);
		inflater.inflate(R.layout.editlayout, layout);
	
		String name=nameText.getText().toString();
		String start=startText.getText().toString();
		String period=periodText.getText().toString();
		if(name.length()==0||start.length()==0||period.length()==0)
		{
			return;
		}
	
		
		dbAdapter.open();
		
		if(newPlant)
		{
			boolean taken=dbAdapter.nameTaken(name);
			
			
			if(taken)
			{
				dbAdapter.close();
				Context context = getApplicationContext();
				CharSequence text = "Name Already Used!";
				int duration = Toast.LENGTH_SHORT;
		
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				return;
			}
			
			
			
			Plant newPlant=new Plant(Integer.parseInt(period),name);
			Date now = new Date(System.currentTimeMillis());
			newPlant.setNextWatering(Utilities.SkipAheadNDays(now, Integer.parseInt(start)));
			dbAdapter.insertPlant(newPlant);
		}
		else
		{
			String oldName=getIntent().getStringExtra("name");
			if(!oldName.equals(name))
			{
				boolean taken=dbAdapter.nameTaken(name);
				
				
				if(taken)
				{
					dbAdapter.close();
					Context context = getApplicationContext();
					CharSequence text = "Name Already Used!";
					int duration = Toast.LENGTH_SHORT;
			
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
					return;
				}
			}
			
			
			Plant plant=dbAdapter.getEntry(oldName);
			Date now = new Date(System.currentTimeMillis());
			plant.setNextWatering(Utilities.SkipAheadNDays(now, Integer.parseInt(start)));
			plant.setName(name);
			plant.setWateringPeriod(Integer.parseInt(period));
			dbAdapter.updatePlant(oldName, plant);
		}
		
		dbAdapter.close();
	
    	setResult(Activity.RESULT_OK, null);
		finish();
	}
	
	private void handleDeleteButtonClick() {
		Intent data = new Intent();
		if(newPlant)
		{
			setResult(Activity.RESULT_CANCELED, data);
			finish();
		}
		else
		{
			dbAdapter.open();
			dbAdapter.deletePlant(getIntent().getStringExtra("name"));
			dbAdapter.close();
			setResult(Activity.RESULT_OK, null);
			finish();
		}
		
		data.putExtra("type","delete");
		
    	setResult(Activity.RESULT_OK, data);
		finish();
	}
	
	
	public void onCreate(Bundle savedInstanceState) 
	{
	        super.onCreate(savedInstanceState);
	        
	        dbAdapter=new DBAdapter(this);
	        
	        newPlant=getIntent().getStringExtra("type").equals("add");
	        
	    	LayoutInflater inflater = LayoutInflater.from(this);
	    	LinearLayout layout = new LinearLayout(this);
			inflater.inflate(R.layout.editlayout, layout);
			
			((Button) layout.findViewById(R.id.savebutton)).setOnClickListener(new SaveButtonHandler());
			
			if(newPlant)
			{
				((Button) layout.findViewById(R.id.deletebutton)).setText("Cancel");
			}       
			else
			{
				((Button) layout.findViewById(R.id.deletebutton)).setText("Delete");
			}		
			
			((Button) layout.findViewById(R.id.deletebutton)).setOnClickListener(new DeleteButtonHandler());
			
			
			
			startText=(EditText) layout.findViewById(R.id.startText);
			nameText=(EditText) layout.findViewById(R.id.nameText);
			periodText=(EditText) layout.findViewById(R.id.periodText);
			
			if(!newPlant)
			{
				dbAdapter.open();
				Plant plant=dbAdapter.getEntry(getIntent().getStringExtra("name"));
				dbAdapter.close();
				nameText.setText(plant.getName());
				startText.setText(""+plant.GetDaysToNextWatering(new Date(System.currentTimeMillis())));
				periodText.setText(""+plant.getWateringPeriod());
			}
			
	        setContentView(layout);
	}
}