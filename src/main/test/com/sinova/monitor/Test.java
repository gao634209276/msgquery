package com.sinova.monitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 */
public class Test {

	@org.junit.Test
	public void testDate() throws ParseException {
		String today = "2017.03.28";
		String day = "2017.03.27";
		//System.out.println(today.compareTo(day));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		/*long i = System.currentTimeMillis();
		Date date = new Date(60);*/

		//System.out.println(sdf.parse(today).getTime() - sdf.parse(day).getTime());
		System.out.println(df.format(new Date(3600000 * 24)));
		//System.out.println(sdf.parse(day).getTime());
	}

	@org.junit.Test
	public void testCaledar() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		System.out.println(c);
		System.out.println(df.format(c.getTime()));
		c.setTime(new Date());
		c.add(Calendar.DATE, 1);
		System.out.println(df.format(c.getTime()));
		Calendar c2 = Calendar.getInstance();
		c2.setTime(new Date());
		System.out.println(df.format(c2.getTime()));
		c2.setTime(c.getTime());
		System.out.println(df.format(c2.getTime()));
	}

	@org.junit.Test
	public void testString() {
		String test = "2016-07-08 17:47:25.570Z";
		test = test.substring(test.indexOf("-") + 1);
		test = test.substring(0, test.indexOf("."));

		System.out.println(test);

	}
}
