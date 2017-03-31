package com.sinova.monitor.model;

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
