package cn.yc.ssh.admin.base.dao;

import cn.yc.ssh.admin.log.SysOperLog;
import cn.yc.ssh.common.Condition;
import cn.yc.ssh.common.MyPape;

public interface ISyslogDAO {

	MyPape find(Condition condition);

	void deleteByIds(String ids);
	
	void add(SysOperLog log);

	MyPape findAllTotal(Condition condition);

}
