package cn.yc.ssh.admin.base.service;

import java.util.List;

import cn.yc.ssh.admin.base.entity.TimeBlackList;

public interface TimeBlackListService {

	List<TimeBlackList> list();

	List<String> getStringList();

	void add(TimeBlackList time);

	void delete(String id);

	//判断当前时间是否在黑名单中
	boolean isTimeBlackList();

}
