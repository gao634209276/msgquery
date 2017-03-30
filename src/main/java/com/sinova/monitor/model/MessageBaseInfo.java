package com.sinova.monitor.model;

/**
 * <td data-bind="text:timestamp" class="errorTime">&nbsp;</td>
 * <td data-bind="text:type">&nbsp;</td>
 * <td data-bind="text:mobile">&nbsp;</td>
 * <td data-bind="text:intercode">&nbsp;</td>
 * <td data-bind="text:inter">&nbsp;</td>
 * <td data-bind="text:transid">&nbsp;</td>
 */
public class MessageBaseInfo implements Comparable<MessageBaseInfo> {
	private String timestamp = "";
	private String type = "";
	private String mobile = "";
	private String intercode = "";
	private String inter = "";
	private String transid = "";
	//private int flag;

	/*public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}*/

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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

	public String getTransid() {
		return transid;
	}

	public void setTransid(String transid) {
		this.transid = transid;
	}

	@Override
	public String toString() {
		return "MessageBaseInfo{" +
				"timestamp='" + timestamp + '\'' +
				", type='" + type + '\'' +
				", mobile='" + mobile + '\'' +
				", intercode='" + intercode + '\'' +
				", inter='" + inter + '\'' +
				", transid='" + transid + '\'' +
				'}';
	}

	@Override
	public int compareTo(MessageBaseInfo o) {
		return -this.getTimestamp().compareTo(o.getTimestamp());
	}
}
