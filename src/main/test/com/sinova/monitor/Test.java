package com.sinova.monitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class Test {

	@org.junit.Test
	public void testDate() throws ParseException {
		String today = "2017.03.28";
		String day = "2017.02.20";
		//System.out.println(today.compareTo(day));
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY.MM.dd");

		long i = System.currentTimeMillis();
		Date date = new Date(0);

		System.out.println(sdf.parse(today).getTime());
		System.out.println(sdf.format(date));
		System.out.println(date.getTime());
	}
}
