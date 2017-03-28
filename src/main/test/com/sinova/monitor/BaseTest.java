package com.sinova.monitor;

import com.sinova.monitor.service.SpringContextUtil;
import org.elasticsearch.client.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class BaseTest extends UnitTestBase {
	public BaseTest() {
		super("classpath:/spring/applicationContext.xml");
	}

	@Test
	public void testSpringConfig(){
		//JavaConfigApplicationContext ctx = new JavaConfigApplicationContext(ApplicationConfig.class);
		Client client = SpringContextUtil.getBean("client");
		System.out.println(client);
	}
}
