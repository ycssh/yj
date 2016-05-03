package cn.yc.ssh.admin.base.web.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.yc.ssh.admin.Constants;
import cn.yc.ssh.admin.base.entity.TimeBlackList;
import cn.yc.ssh.admin.base.service.TimeBlackListService;
import cn.yc.ssh.admin.base.util.Message;
import cn.yc.ssh.admin.log.SysOperLog;

@Controller
@RequestMapping("/timeblacklist")
/**
 * 登录时间段黑名单
 * @author ycssh
 *
 */
public class TimeBlackListController {

	@Resource
	private TimeBlackListService timeBlackListService;

	@RequestMapping("/list")
	@RequiresPermissions("timeblacklist")
	public String list(Model model) {
		List<TimeBlackList> list = timeBlackListService.list();
		model.addAttribute("list",list);
		return "sys/timeblacklist";
	}
	
	@RequestMapping(value="/add")
	@RequiresPermissions("timeblacklist")
	public  @ResponseBody Message add(TimeBlackList time, HttpServletRequest request) {
		timeBlackListService.add(time);
		SysOperLog log = new SysOperLog();
		log.setContent("添加访问时间黑名单:"+time);
		log.setOperType(Constants.SYSLOG_ADD);
		log.setTitle("添加访问时间黑名单");
		log.setLogType(Constants.SYSLOG_SYS);
		log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
		request.setAttribute(Constants.LOG_RECORD, log);
		return new Message(true, "");
	}
	@RequestMapping(value="/delete/{id}")
	@ResponseBody
	@RequiresPermissions("timeblacklist")
	public String delete(@PathVariable("id") String id, HttpServletRequest request) {
		timeBlackListService.delete(id);
		SysOperLog log = new SysOperLog();
		log.setContent("删除访问时间黑名单:"+id);
		log.setOperType(Constants.SYSLOG_DELETE);
		log.setTitle("删除访问时间黑名单");
		log.setLogType(Constants.SYSLOG_SYS);
		log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
		request.setAttribute(Constants.LOG_RECORD, log);
		return "1";
	}

}
