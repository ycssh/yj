package cn.yc.ssh.admin.log;

import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class LogRecordListener implements ApplicationListener<LogRecordEvent> {
	private NamedParameterJdbcTemplate npjt;

	@Override
	public void onApplicationEvent(final LogRecordEvent event) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final SysOperLog log = event.getSysOperLog();			
				//==old
				//String sql = "INSERT INTO SYSOPER_LOG(DBID,IP,OPERTYPE,OPERTIME,CONTENT,TITLE,USERID,UNAME,logtype) "
				//		+ "VALUES(SEQ_SYS_OPER_LOG.NEXTVAL,:ip,:operType,:opertime,:content,:title,:userid,:uname,:logType)";
				
				//new:增加事件结果   2015/11/24 yangjian====begin
				//默认为成功
				if(log.getResult() == null){//若没有添加事件结果，默认为成功 ，即为 1
					log.setResult(1);
				}
				String sql = "INSERT INTO SYSOPER_LOG(DBID,IP,OPERTYPE,OPERTIME,CONTENT,TITLE,USERID,UNAME,logtype,RESULT) "
						+ "VALUES(SEQ_SYS_OPER_LOG.NEXTVAL,:ip,:operType,:opertime,:content,:title,:userid,:uname,:logType,:result)";
				//===================end
				
				npjt.update(sql, new BeanPropertySqlParameterSource(log));
			}
		}).start();
	}

	public NamedParameterJdbcTemplate getNpjt() {
		return npjt;
	}

	public void setNpjt(NamedParameterJdbcTemplate npjt) {
		this.npjt = npjt;
	}
	
	
}
