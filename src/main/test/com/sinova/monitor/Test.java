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
}
