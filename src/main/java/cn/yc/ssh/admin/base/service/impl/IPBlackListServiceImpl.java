package cn.yc.ssh.admin.base.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cn.yc.ssh.admin.base.entity.IPBlackList;
import cn.yc.ssh.admin.base.service.IPBlackListService;

@Service
public class IPBlackListServiceImpl implements IPBlackListService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
    
	@Override
	public List<IPBlackList> list() {
		String sql = "select id,ip from IPBlackList";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<IPBlackList>(IPBlackList.class));
	}

	@Override
	public void add(String ip) {
		String sql = "insert into IPBlackList(id,ip) values (SEQ_SYS_USER.nextval,?)";
		jdbcTemplate.update(sql, ip);
	}

	@Override
	public void delete(String id) {
		String sql = "delete from IPBlackList where id=?";
		jdbcTemplate.update(sql, id);

	}

	@Override
	public List<String> getStringList() {
		List<IPBlackList> list =  list();
		List<String> sList = new ArrayList<String>();
		for(IPBlackList ipBlackList:list){
			sList.add(ipBlackList.getIp());
		}
		return sList;
	}

}
