package cn.yc.ssh.admin.base.web.shiro.filter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import cn.yc.ssh.admin.Constants;
import cn.yc.ssh.admin.base.dao.ISyslogDAO;
import cn.yc.ssh.admin.base.entity.Message;
import cn.yc.ssh.admin.base.entity.User;
import cn.yc.ssh.admin.base.service.IMessageService;
import cn.yc.ssh.admin.base.service.UserService;
import cn.yc.ssh.admin.log.SysOperLog;
import cn.yc.ssh.common.CommonUtils;

public class SysUserFilter extends PathMatchingFilter {

    @Autowired
    private UserService userService;
	@Resource
	private IMessageService messageService; 
    
    @Resource
    private ISyslogDAO syslogDAO;

	@Resource
	private JdbcTemplate jdbcTemplate;
    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        String username = (String)SecurityUtils.getSubject().getPrincipal();
        Subject subject = SecurityUtils.getSubject(); 
        Session session = subject.getSession();

        Map<String,String> inits = (Map<String, String>) Constants.cache.get("sys_init");
        String sessionTimeout = inits.get("sessionTimeout");
        session.setTimeout(Integer.parseInt(sessionTimeout)*60*1000);
        if(session.getAttribute(Constants.CURRENT_USER)==null){
        	String currentIP = request.getRemoteHost();
        	if("0:0:0:0:0:0:0:1".equals(currentIP)){
        		currentIP = "127.0.0.1";
        	}
        	User user = userService.findByUsername(username);
        	session.setAttribute(Constants.CURRENT_USER, user);
			String sql = "select ip from sysoper_log where userid=? and content='登录' order by opertime desc";
			List<SysOperLog> logs = jdbcTemplate.query(CommonUtils.getPavSql(sql, 0, 5), new BeanPropertyRowMapper<SysOperLog>(SysOperLog.class),user.getUsername());
			if(!CollectionUtils.isEmpty(logs)){
				String[] ip = currentIP.split("\\.");
				boolean iPUnusually = true;
				for(SysOperLog log:logs){
					if(log.getIp().equals("0:0:0:0:0:0:0:1")){
						log.setIp("127.0.0.1");
					}
					String[] ipArr = log.getIp().split("\\.");
					if(ipArr[0].equals(ip[0])&&ipArr[1].equals(ip[1])){
						iPUnusually = false;
						break;
					}
				}
				if(iPUnusually){
					SysOperLog log = new SysOperLog();
					log.setTitle("IP异常");
					log.setOperType(1);
					log.setIp(currentIP);
					log.setUname(user.getName());
					log.setUserid(user.getUsername());
					log.setContent("IP异常");
					log.setOpertime(new Date());
					log.setLogType(Constants.SYSLOG_SYS);
					log.setResult(Constants.SYSLOG_RESULT_FAIL);
					syslogDAO.add(log);
	
					Message message = new Message();
					message.setContent("IP异常");
					message.setIp(session.getHost());
					message.setMsgTime(new Date());
					message.setRead(0);
					message.setUserName(user.getName());
					messageService.save(message);
				}
			}

			SysOperLog _log = new SysOperLog();
			_log.setTitle("登录");
			_log.setOperType(1);
			
			_log.setIp(currentIP);
			_log.setUname(user.getName());
			_log.setUserid(user.getUsername());
			_log.setContent("登录");
			_log.setOpertime(new Date());
			_log.setLogType(Constants.SYSLOG_SYS);
			_log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
			syslogDAO.add(_log);
        }else{
        	User user = (User) session.getAttribute(Constants.CURRENT_USER);
        	request.setAttribute(Constants.CURRENT_USER, user);
        }
        return true;
    }
}
