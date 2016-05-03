package cn.yc.ssh.admin.base.service;

import cn.yc.ssh.admin.base.entity.Message;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.Pagination;

public interface IMessageService {

	void save(Message message);
	
	//获取页面数据（分页）
	public PageResult<Message> pageSelect(Pagination page,String time ,Integer readString);
	
	//消息标记为已读
	public int updateReadById(String Id, int read);

}
