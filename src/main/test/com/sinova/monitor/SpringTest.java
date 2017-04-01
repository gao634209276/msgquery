package com.sinova.monitor;

import com.sinova.monitor.elasticsearch.SearchEnv;
import com.sinova.monitor.util.SpringContextUtil;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/spring/applicationContext.xml")
public class SpringTest {

	@org.junit.Test
	public void test() {
		String test = "a||b";
		SearchEnv env = SpringContextUtil.getBean("searchEnv");

	}
}
