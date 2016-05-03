package cn.yc.ssh.admin.base.web.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.yc.ssh.admin.base.entity.Message;
import cn.yc.ssh.admin.base.service.IMessageService;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.Pagination;

@Controller
@RequestMapping("/message")
public class MessageController {
	
	@Autowired
	private IMessageService messageService;
	
	@RequiresPermissions("message:index")
	@RequestMapping(value="index", method = RequestMethod.GET)
	public String index(Model model) {
		return "message/index";
	}
	
	
	// 页面查询方法
	@RequestMapping("/list")
	@RequiresPermissions("message:index")
	public @ResponseBody
	PageResult<Message> list(String isSelect, Pagination page,String time ,Integer readString) {
		// 防止easy-ui的重复查询访问
		if (isSelect == null || isSelect.length() == 0
				|| (!(isSelect.equals("1")))) {
			return null;
		}

		// 获取数据
		return messageService.pageSelect(page, time, readString);
	}
	
	//标记为已读
	@RequestMapping("/toRead")
	@RequiresPermissions("message:index")
	public @ResponseBody Integer toRead(String messageId){		
		int sucRol = messageService.updateReadById(messageId, 1);	
		return sucRol;
	}

}
