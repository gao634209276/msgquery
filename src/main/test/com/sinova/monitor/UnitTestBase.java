package com.sinova.monitor;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

/**
 * 完成对spring配置文件的加载，销毁。
 * 所有的单元测试类都集成子UnitTestBase，通过他的getBean方法获取想要的对象
 * 子类加注解：@RunWith(Block
 */
public class UnitTestBase {

	private ClassPathXmlApplicationContext context;
	private String springXmlpath;

	public UnitTestBase() {
	}

	public UnitTestBase(String springXmlpath) {
		this.springXmlpath = springXmlpath;
	}

	@Before
	public void before() {
		if (StringUtils.isEmpty(springXmlpath)) {
			springXmlpath = "classpath*:/spring/applicationContext.xml";
		}
		try {
			//context = new ClassPathXmlApplicationContext(springXmlpath.split("[,\\s]+"));
			context = new ClassPathXmlApplicationContext(springXmlpath);
			context.start();
		} catch (BeansException e) {
			e.printStackTrace();
		}
	}

	@After
	public void after() {
		context.destroy();
	}

	@SuppressWarnings("unchecked")
	protected <T> T getBean(String beanId) {
		try {
			return (T) context.getBean(beanId);
		} catch (BeansException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected <T> T getBean(Class<T> clazz) {
		try {
			return context.getBean(clazz);
		} catch (BeansException e) {
			e.printStackTrace();
		}
		return null;
	}
}
