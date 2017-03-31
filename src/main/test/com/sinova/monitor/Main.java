package com.sinova.monitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class Main {
	public static void main(String[] args) {
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
	}
}
