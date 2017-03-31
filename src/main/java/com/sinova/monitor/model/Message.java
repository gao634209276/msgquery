package com.sinova.monitor.model;

/**
 * 业务处理对象
 * 通过message中的日志代码行数,在详情处理时用于message排序
 * Created by Noah on 2017/3/31.
 */
public class Message implements Comparable<Message> {
	private int code = 0;
	private String message;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int compareTo(Message o) {
		return this.getCode() - o.getCode();
	}
}
