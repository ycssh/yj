package cn.yc.ssh.admin.base.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cn.yc.ssh.admin.base.entity.TimeBlackList;
import cn.yc.ssh.admin.base.service.TimeBlackListService;

@Service
public class TimeBlackListServiceImpl implements TimeBlackListService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<TimeBlackList> list() {
		String sql = "select id,startHour,startMin,endHour,endMin from timeBlackList";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<TimeBlackList>(
				TimeBlackList.class));
	}

	@Override
	public List<String> getStringList() {
		return null;
	}

	@Override
	public void delete(String id) {
		String sql = "delete from timeBlackList where id=?";
		jdbcTemplate.update(sql, id);

	}

	@Override
	public void add(TimeBlackList time) {
		String sql = "insert into timeBlackList(id,startHour,startMin,endHour,endMin) values (SEQ_SYS_USER.nextval,?,?,?,?)";
		jdbcTemplate.update(sql, time.getStartHour(), time.getStartMin(),
				time.getEndHour(), time.getEndMin());
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isTimeBlackList() {
		List<TimeBlackList> list = list();
    	for(TimeBlackList black:list){
    		int startHour = black.getStartHour();
    		int endHour = black.getEndHour();
    		int startMin = black.getStartMin();
    		int endMin = black.getEndMin();
    		Date start = new Date();
    		Date now = new Date();
    		start.setHours(startHour);
    		start.setMinutes(startMin);
    		Date end = new Date();
    		end.setHours(endHour);
    		end.setMinutes(endMin);
    		if(start.compareTo(end)<0){
    			if(now.compareTo(end)<0 && now.compareTo(start)>0){
    				return true;
    			}
    		}else{
    			if(now.compareTo(end)<0||now.compareTo(start)>0){
    				return true;
    			}
    		}
    	}
    	return false;
	}

}
