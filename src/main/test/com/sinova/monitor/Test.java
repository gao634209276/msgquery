package com.sinova.monitor;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

		//System.out.println(sdf.hits2Detail(today).getTime() - sdf.hits2Detail(day).getTime());
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
		String test = "a||b";
		System.out.println(test.indexOf("||"));
		System.out.println(Arrays.asList(test.split("\\|\\|")));
		String xml;
		if (!StringUtils.isEmpty(xml = test)) {
			System.out.println("test");
			System.out.println(xml);
		}

	}

	@org.junit.Test
	public void testMatch() {
		String s = "a\nb\nc\nd\ne\nf\ng";
		Pattern p = Pattern.compile("\\n");
		Matcher m = p.matcher(s);
		//boolean b = m.matches();
		//boolean b = m.find(100);
		if (m.matches()) {
			System.out.println(m.end());
			if (m.end() == 0) {
				System.out.println("yes");
			}
			System.out.println(m.start());
		}
	/*	while (m.find()) {
			System.out.println(m.end());
			System.out.println(m.start());
		}*/
	}


}
