package cn.yc.ssh.admin.base.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import cn.yc.ssh.admin.base.entity.Message;
import cn.yc.ssh.admin.base.service.IMessageService;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.PageSqlUtil;
import cn.yc.ssh.admin.base.util.Pagination;

@Service
public class MessageServiceImpl implements IMessageService {

	@Autowired
	private NamedParameterJdbcTemplate npjd;
	
	@Override
	public void save(Message message) {
		String sql = "insert into message(id,read,content,msgTime,userName,ip)values(seq_msg.nextval,:read,:content,:msgTime,:userName,:ip)";
		npjd.update(sql, new BeanPropertySqlParameterSource(message));
	}

	@Override
	public PageResult<Message> pageSelect(Pagination page, String time,
			Integer readString) {
		//查询的sql语句
		String sql = "SELECT MESSAGE.* ,TO_CHAR(MSGTIME,'yyyy-mm-dd') as timeString ,  "+
					"(CASE read WHEN 0 THEN '未读' WHEN 1 THEN '已读' ELSE '其他' END ) as readString  "+
					"from MESSAGE where 1=1";
		Map<String,Object> map = new HashMap<String, Object>();
		//时间筛选
		if(time!=null && time.length()>0){
			sql = sql + " and TO_CHAR(MSGTIME,'yyyy-mm-dd') =:time ";
			map.put("time", time);
		}
		//是否已读筛选
		if(readString!=null){
			sql = sql + " AND READ =:readString";
			map.put("readString", readString);
		}
		
		//加上按部门id进行排序条件
		sql = sql + " ORDER BY MSGTIME DESC,ID DESC ";
		
		//获取分页需要显示的数据
		List<Message> list = npjd.query(PageSqlUtil.getPageSql(sql, page), map, new BeanPropertyRowMapper<Message>(Message.class));
        int count = npjd.query(sql.toString(), map, new BeanPropertyRowMapper<Message>(Message.class)).size();
        return new PageResult<Message>(list, count);
	}

	@Override
	public int updateReadById(String ids, int read) {
		
		int sucessRow = 0;
		
		
		//字符串转数组
		String[] idArr = ids.split(",");
		
		//循环数组 进行修改
		for(int index = 0 ;index < idArr.length ;index++){
			//获取当前ID,同时去除'号
			String idString = idArr[index].replace("'", "");
			
			//修改
			Map<String,Object> paramsMap = new HashMap<String, Object>();
			String sql = "UPDATE MESSAGE SET read = :read WHERE id =:id";
			paramsMap.put("read", read);
			paramsMap.put("id", idString);
			sucessRow =sucessRow + npjd.update(sql, paramsMap);		
		}	
		
		
		
		return sucessRow;
	}

}
