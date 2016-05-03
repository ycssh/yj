package cn.yc.ssh.admin.base.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cn.yc.ssh.admin.base.service.SysInitService;

@Service
public class SysInitServiceImpl implements SysInitService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
	@Override
	public void update(int id, String value) {
		String sql = "update sys_init set value=? where id=?";
		jdbcTemplate.update(sql,value,id);
	}

}
