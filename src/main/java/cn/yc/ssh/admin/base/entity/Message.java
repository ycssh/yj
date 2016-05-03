package cn.yc.ssh.admin.base.entity;

import java.util.Date;

public class Message {
	private int id;
	private int read;
	private String content;
	private Date msgTime;
	private String userName;
	private String ip;
	
	private String timeString;//用于显示格式化后的时间
	private String readString;//是否已经读字符串  

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(Date msgTime) {
		this.msgTime = msgTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}

	public String getReadString() {
		return readString;
	}

	public void setReadString(String readString) {
		this.readString = readString;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", read=" + read + ", content=" + content
				+ ", msgTime=" + msgTime + ", userName=" + userName + ", ip="
				+ ip + ", timeString=" + timeString + ", readString="
				+ readString + "]";
	}

}
