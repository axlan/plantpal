package com.robopenguins;

import java.util.Calendar;
import java.util.Date;

public class Plant implements Comparable<Plant> {
	int wateringPeriod;
	Date nextWatering;
	Date lastCheck;
	String name;
	
	// If this < o, return a negative value
    // If this = o, return 0
    // If this > o, return a positive value
	public int compareTo(Plant o) {
		return this.nextWatering.compareTo(o.getNextWatering());
    } 
	
	public void Water()
	{
		switch( mood)
		{
		case CONTENT:
			mood=Mood.HAPPY;
			break;
		case BLUE:
			mood=Mood.CONTENT;
			break;
		case SAD:
			mood=Mood.BLUE;
			break;
		}
		
	    Date now=new Date(System.currentTimeMillis());
		nextWatering=Utilities.SkipAheadNDays(now, wateringPeriod);
	}
	
	//returns number of days B is after A
	private int DiffDays(Date A,Date B)
	{
		Date aTemp=new Date(A.getYear(), A.getMonth(), A.getDate());
		Date bTemp=new Date(B.getYear(), B.getMonth(), B.getDate());
		
		Calendar nextCal = Calendar.getInstance();
		nextCal.setTime(bTemp);
		Calendar todayCal = Calendar.getInstance();
		todayCal.setTime(aTemp);

		if (nextCal.before(todayCal)) {
			Integer daysBetween = 0;
			while (nextCal.before(todayCal)) {
				todayCal.add(Calendar.DAY_OF_MONTH, -1);
				daysBetween++;
			}
			return -daysBetween;

		}
		else if (nextCal.after(todayCal)) {
			Integer daysBetween = 0;
			while (nextCal.after(todayCal)) {
				todayCal.add(Calendar.DAY_OF_MONTH, 1);
				daysBetween++;
			}
			return daysBetween;
		} else {
			return 0;
		}
	}
	
	
	public void CheckWatering(Date now)
	{
		Date today = new Date(now.getYear(), now.getMonth(), now.getDate());
		Calendar nextCal = Calendar.getInstance();
		nextCal.setTime(getNextWatering());
		Calendar todayCal = Calendar.getInstance();
		todayCal.setTime(today);
		Calendar checkCal = Calendar.getInstance();
		checkCal.setTime(getLastCheck());
		if (checkCal.before(todayCal)) {
			int days=GetDaysToNextWatering(now);
			for( ;days<0;days++)
			{
				ForgetWater();
			}
			setLastCheck(today);
		}
	}
	
	public int GetDaysToNextWatering(Date now)
	{
		return DiffDays(now,getNextWatering());
	}

	public void ForgetWater()
	{
		switch( mood)
		{
		case HAPPY:
			mood=Mood.CONTENT;
			break;
		case CONTENT:
			mood=Mood.BLUE;
			break;
		case BLUE:
			mood=Mood.SAD;
			break;
		}
		
	}

	
	public Date getLastCheck() {
		return lastCheck;
	}

	public void setLastCheck(Date lastCheck) {
		this.lastCheck = lastCheck;
	}

	public Plant(int wateringPeriod, String name) {
		this.wateringPeriod = wateringPeriod;
		this.name = name;
		mood=Mood.CONTENT;
		Date now=new Date(System.currentTimeMillis());
		nextWatering=new Date(now.getYear(),now.getMonth(),now.getDate());
		lastCheck=new Date(0);
	}

	public int getWateringPeriod() {
		return wateringPeriod;
	}

	public void setWateringPeriod(int wateringPeriod) {
		this.wateringPeriod = wateringPeriod;
	}

	public Date getNextWatering() {
		return nextWatering;
	}

	public void setNextWatering(Date nextWatering) {
		this.nextWatering = nextWatering;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Mood getMood() {
		return mood;
	}

	public void setMood(Mood mood) {
		this.mood = mood;
	}

	enum Mood {
		HAPPY, CONTENT, BLUE, SAD
	}

	Mood mood;
}
