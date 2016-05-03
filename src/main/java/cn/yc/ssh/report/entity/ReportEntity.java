package cn.yc.ssh.report.entity;


/**
 * @author 作者姓名 yc E-mail: ycssh2@163.com
 * @version 创建时间：2014-5-13 下午02:57:03 类说明
 */
public class ReportEntity {
	private String name;
	private Object data;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public ReportEntity(String name, Object data) {
		super();
		this.name = name;
		this.data = data;
	}
	public ReportEntity() {
		super();
	}


}
