package cn.yc.ssh.admin.base.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import cn.yc.ssh.admin.base.dao.ISyslogDAO;
import cn.yc.ssh.admin.log.SysOperLog;
import cn.yc.ssh.admin.log.SysOperTotalLog;
import cn.yc.ssh.common.CommonUtils;
import cn.yc.ssh.common.Condition;
import cn.yc.ssh.common.MyPape;

@Component
public class SyslogDAO implements ISyslogDAO {

	@Autowired
	@Qualifier("npjd")
	private NamedParameterJdbcTemplate npjd;
	
	@SuppressWarnings("deprecation")
	@Override
	public MyPape find(Condition condition) {
		
		int start = 0;
		int limit = 10;
		String where = "";
		
		String sql = "select * from sysoper_log  ";
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String order = " order by opertime desc";
		if(condition!=null){
			
			if(condition.getStart()!=null){
				start = condition.getStart();
			}
			if(condition.getLimit()!=null){
				limit = condition.getLimit();
			}
			if(condition.getFilter()!=null){
				String filter = condition.getFilter();
				
				String title = CommonUtils.getKeyValue(filter, "title");
				String userid = CommonUtils.getKeyValue(filter, "userid");
				String startTime = CommonUtils.getKeyValue(filter, "startTime");
				String endTime = CommonUtils.getKeyValue(filter, "endTime");
				String logType = CommonUtils.getKeyValue(filter, "logType");
				String orderType = CommonUtils.getKeyValue(filter, "orderType");
				if("2".equals(orderType)){
					order = " order by userid";
				}else{
					order = " order by opertime desc";
				}
				
				if(StringUtils.hasLength(title)){
					where += " and title like :title";
					paramMap.put("title", "%"+title+"%");
				}
				if(StringUtils.hasLength(logType)&&!"0".equals(logType)){
					where += " and logType =:logType";
					paramMap.put("logType", logType);
				}
				if(StringUtils.hasLength(userid)){
					where += " and userid like :userid";
					paramMap.put("userid", "%"+userid+"%");
				}
				if(StringUtils.hasLength(startTime)){
					where += " and to_char(opertime,'yyyy-MM-dd')>=:startTime";
					paramMap.put("startTime", startTime);
				}
				if(StringUtils.hasLength(endTime)){
					where += " and to_char(opertime,'yyyy-MM-dd')<=:endTime";
					paramMap.put("endTime", endTime);
				}
			}
		}
		if(!"".equals(where)){
			where = where.substring(4);
			sql += " where "+where;
		}
		sql += order;
		
		String countSql = "select count(*) from ("+sql+") t";
		int total = npjd.queryForInt(countSql, paramMap);
		
		RowMapper<SysOperLog> rowMapper = BeanPropertyRowMapper.newInstance(SysOperLog.class);
		List<SysOperLog> results = npjd.query(CommonUtils.getPavSql(sql,start,limit),paramMap, rowMapper);
		
		MyPape myPape = new MyPape(total,results);
		return myPape;
	}

	@Override
	public void deleteByIds(String ids) {
		//字符串转数组
		String[] idArr = ids.split(",");
		
		//循环数组删除
		for(int index = 0 ;index < idArr.length ;index++){
			//获取当前ID,同时去除'号
			String idString = idArr[index].replace("'", "");
			
			//删除
			Map<String,Object> paramsMap = new HashMap<String, Object>();
			String sql = "delete from sysoper_log where dbid =:idString";
			paramsMap.put("idString", idString);
			npjd.update(sql, paramsMap);		
		}	
		
	}

	@Override
	public void add(SysOperLog log) {
		//old:
		//String sql = "INSERT INTO SYSOPER_LOG(DBID,IP,OPERTYPE,OPERTIME,CONTENT,TITLE,USERID,UNAME) "
		//		+ "VALUES(SEQ_SYS_OPER_LOG.NEXTVAL,:ip,:operType,:opertime,:content,:title,:userid,:uname)";
		
		
		//new:增加事件结果   2015/11/24 yangjian====begin
		//默认为成功
		if(log.getResult() == null){//若没有添加事件结果，默认为成功 ，即为 1
			log.setResult(1);
		}
		String sql = "INSERT INTO SYSOPER_LOG(DBID,IP,OPERTYPE,OPERTIME,CONTENT,TITLE,USERID,UNAME,RESULT) "
				+ "VALUES(SEQ_SYS_OPER_LOG.NEXTVAL,:ip,:operType,:opertime,:content,:title,:userid,:uname,:result)";
		//===================end
		
		npjd.update(sql, new BeanPropertySqlParameterSource(log));
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public MyPape findAllTotal(Condition condition) {
		
		int start = 0;
		int limit = 10;
		String where = "";
		Map<String,Object> paramsMap = new HashMap<String, Object>();
		
		String sql = "select title, logType, count(dbid) operTime from sysoper_log where title is not null ";
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if(condition!=null){
			
			if(condition.getStart()!=null){
				start = condition.getStart();
			}
			if(condition.getLimit()!=null){
				limit = condition.getLimit();
			}
			if(condition.getFilter()!=null){
				String filter = condition.getFilter();
				String logType = CommonUtils.getKeyValue(filter, "logType");
				String startTime = CommonUtils.getKeyValue(filter, "startTime");
				String endTime = CommonUtils.getKeyValue(filter, "endTime");

				if(StringUtils.hasLength(startTime)){
					where += " and to_char(opertime,'yyyy-MM-dd')>=:startTime ";
					paramsMap.put("startTime", startTime);
				}
				if(StringUtils.hasLength(endTime)){
					where += " and to_char(opertime,'yyyy-MM-dd')<=:endTime ";
					paramsMap.put("endTime", endTime);
				}
				if(StringUtils.hasLength(logType)){
					where += " and logType =:logType ";
					paramsMap.put("logType", logType);
				}
			}
		}
		if(!"".equals(where)){
			sql += where;
		}
		sql += " group by title, logType order by logType";
		
		String countSql = "select count(*) from ("+sql+") t";
		int total = npjd.queryForInt(countSql, paramMap);
		
		RowMapper<SysOperTotalLog> rowMapper = BeanPropertyRowMapper.newInstance(SysOperTotalLog.class);
		List<SysOperTotalLog> results = npjd.query(CommonUtils.getPavSql(sql,start,limit),paramMap, rowMapper);
		
		MyPape myPape = new MyPape(total,results);
		return myPape;
	}

}
