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
import cn.yc.ssh.admin.base.entity.IPBlackList;
import cn.yc.ssh.admin.base.service.IPBlackListService;
import cn.yc.ssh.admin.log.SysOperLog;

@Controller
@RequestMapping("/ipblacklist")
/**
 * ip黑名单
 * @author ycssh
 *
 */
public class IPBlackListController {

	@Resource
	private IPBlackListService iPBlackListService;

	@RequestMapping("/list")
	@RequiresPermissions("ipblacklist")
	public String list(Model model) {
		List<IPBlackList> list = iPBlackListService.list();
		model.addAttribute("list",list);
		return "sys/ipblacklist";
	}
	
	@RequestMapping(value="/add")
	@RequiresPermissions("ipblacklist")
	@ResponseBody
	public String add(IPBlackList ip, HttpServletRequest request) {
		List<String> list = iPBlackListService.getStringList();
		if(list.contains(ip.getIp())){
			return "2";
		}

		SysOperLog log = new SysOperLog();
		log.setContent("新增IP黑名单:"+ip.getIp());
		log.setOperType(Constants.SYSLOG_ADD);
		log.setTitle("新增IP黑名单");
		log.setLogType(Constants.SYSLOG_SYS);
		log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
		request.setAttribute(Constants.LOG_RECORD, log);
		iPBlackListService.add(ip.getIp());
		return "1";
	}
	
	@RequestMapping(value="/delete/{id}")
	@RequiresPermissions("ipblacklist")
	@ResponseBody
	public String delete(@PathVariable("id") String id, HttpServletRequest request) {
		iPBlackListService.delete(id);
		SysOperLog log = new SysOperLog();
		log.setContent("删除IP黑名单:"+id);
		log.setOperType(Constants.SYSLOG_DELETE);
		log.setTitle("删除IP黑名单");
		log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
		log.setLogType(Constants.SYSLOG_SYS);
		request.setAttribute(Constants.LOG_RECORD, log);
		return "1";
	}

}
