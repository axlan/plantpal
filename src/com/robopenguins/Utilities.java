package com.robopenguins;

import java.util.Calendar;
import java.util.Date;

public class Utilities {

	static Date SkipAheadNDays(Date date, int n) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(date.getYear(), date.getMonth(), date.getDate()));
		cal.add(Calendar.DAY_OF_MONTH, n);
		return cal.getTime();
	}
}
