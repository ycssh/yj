package cn.yc.ssh.admin.base.service;

import java.util.List;

import cn.yc.ssh.admin.base.entity.IPBlackList;

public interface IPBlackListService {

	List<IPBlackList> list();
	
	List<String> getStringList();

	void add(String ip);

	void delete(String id);

}
