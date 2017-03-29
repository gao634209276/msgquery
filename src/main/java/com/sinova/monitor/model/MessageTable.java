package com.sinova.monitor.model;

/**
 */
public class MessageTable {
	private String timestamp = "";
	private String message = "";
	private String path = "";
	private String host = "";
	private String type = "";
	private String detailmessage = "";
	private String mobile = "";
	private String inter = "";
	private String intercode = "";
	private String busiorder = "";
	private String transid = "";
	private String serverip = "";
	//短厅类型分类字段 接口平台：sms  老服务层：ssp  新服务层：ecs
	private String smstype = "";

	public String getSmstype() {
		return smstype;
	}

	public void setSmstype(String smstype) {
		this.smstype = smstype;
	}

	public String getServerip() {
		return serverip;
	}

	public void setServerip(String serverip) {
		this.serverip = serverip;
	}

	public String getTransid() {
		return transid;
	}

	public void setTransid(String transid) {
		this.transid = transid;
	}

	public String getBusiorder() {
		return busiorder;
	}

	public void setBusiorder(String busiorder) {
		this.busiorder = busiorder;
	}

	public String getIntercode() {
		return intercode;
	}

	public void setIntercode(String intercode) {
		this.intercode = intercode;
	}

	public String getInter() {
		return inter;
	}

	public void setInter(String inter) {
		this.inter = inter;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	private int flag;

	public String getDetailmessage() {
		return detailmessage;
	}

	public void setDetailmessage(String detailmessage) {
		this.detailmessage = detailmessage;
	}


	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
