package cn.yc.ssh.admin.base.web.exception;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import cn.yc.ssh.admin.Constants;
import cn.yc.ssh.admin.base.entity.Message;
import cn.yc.ssh.admin.base.entity.User;
import cn.yc.ssh.admin.base.service.IMessageService;
import cn.yc.ssh.admin.base.service.UserService;
import cn.yc.ssh.admin.log.SysOperLog;

public class DefaultExceptionHandler implements HandlerExceptionResolver {

	@Resource
	private UserService userService; 
	
	@Resource
	private IMessageService messageService; 
	
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		ex.printStackTrace();
		if(UnauthorizedException.class.getName().equals(ex.getClass().getName())){
			ModelAndView mv = new ModelAndView();
	        mv.addObject("exception", ex);
	        mv.setViewName("unauthorized");
	        SysOperLog log = new SysOperLog();
	        Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession();
			log.setIp(session.getHost());
			User user = (User) session.getAttribute(Constants.CURRENT_USER);
			log.setUname(user.getName());
			log.setUserid(user.getUsername());
			log.setContent("越权访问:"+ex.getMessage());
			log.setOperType(Constants.SYSLOG_ADD);
			log.setTitle("越权访问");
			log.setResult(Constants.SYSLOG_RESULT_FAIL);
//			syslogDAO.add(log);
			userService.updateState(1, user.getUsername());
			Message message = new Message();
			message.setContent("越权访问"+ex.getMessage());
			message.setIp(session.getHost());
			message.setMsgTime(new Date());
			message.setRead(0);
			message.setUserName(user.getName());
			messageService.save(message);
	        return mv;
		}
		return new ModelAndView("error");
	}
}