package com.listview.entity;

import java.io.Serializable;

public class Log implements Serializable {
	/**
	 * 防护日志实体类
	 */
	private static final long serialVersionUID = 1L;
	private String phoneNO;
	private String time;
	private String process;
	public String getPhoneNO() {
		return phoneNO;
	}
	public void setPhoneNO(String phoneNO) {
		this.phoneNO = phoneNO;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
}
