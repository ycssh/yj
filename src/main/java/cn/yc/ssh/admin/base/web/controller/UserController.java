package cn.yc.ssh.admin.base.web.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cn.yc.ssh.admin.Constants;
import cn.yc.ssh.admin.base.entity.Role;
import cn.yc.ssh.admin.base.entity.User;
import cn.yc.ssh.admin.base.entity.UserAllInfo;
import cn.yc.ssh.admin.base.realm.AES;
import cn.yc.ssh.admin.base.realm.MD5Util;
import cn.yc.ssh.admin.base.realm.UserRealm;
import cn.yc.ssh.admin.base.service.OrganizationService;
import cn.yc.ssh.admin.base.service.RoleService;
import cn.yc.ssh.admin.base.service.UserService;
import cn.yc.ssh.admin.base.util.Message;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.Pagination;
import cn.yc.ssh.admin.log.SysOperLog;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private RoleService roleService;

	@Autowired
	private UserRealm userRealm;

	@Autowired
	private CredentialsMatcher credentialsMatcher;

	@RequestMapping(value = "index", method = RequestMethod.GET)
	@RequiresPermissions("user:index")
	public String index(Model model) {
		return "user/index";
	}

	@RequestMapping("list")
	@RequiresPermissions("user:index")
	public @ResponseBody
	PageResult<User> list(Model model, User user, Boolean cascade,
			Pagination page) {
		return userService.find(user, cascade, page);
	}

	@RequestMapping("listall")
	//@RequiresPermissions("user:index")
	@RequiresPermissions(value={"user:index","admin:workflowmgt:index"},logical=Logical.OR)
	public @ResponseBody
	List<User> list(Model model) {
		return userService.findAll();
	}

	@RequestMapping(value = "/add/{organizationId}", method = RequestMethod.GET)
	@RequiresPermissions("user:index")
	public String showCreateForm(Model model,
			@PathVariable("organizationId") Long organizationId) {
		User user = new User();
		user.setOrganizationId(organizationId);
		model.addAttribute("user", user);
		return "user/edit";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@RequiresPermissions("user:index")
	public void create(User user, RedirectAttributes redirectAttributes,String md5,
			HttpServletResponse response, HttpServletRequest request)
			throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		SysOperLog log = new SysOperLog();
		if (user.getId() == null) {
			User u = userService.findByUsername(user.getUsername());
			if (u != null) {
				out.print("{\"success\":false,\"msg\":\"用户名为"
						+ user.getUsername() + "的用户已经存在，请输入其他用户名！\"}");
				out.flush();
				return;
			}
			if(!MD5Util.MD5(user.getPassword()).equalsIgnoreCase(md5)){
				out.print("密码完整性遭到破坏");
				out.flush();
				return;
			}
			String password = AES.Decrypt(user.getPassword(), SecurityUtils.getSubject().getSession().getAttribute("aesKey").toString());
			if (password.length() < 8 || password.length() > 20) {
				response.setContentType("text/html;charset=UTF-8");
				out.print("密码必须在8-20位.");
				out.flush();
				return;
			}
			user.setPassword(password);
			userService.createUser(user);
			log.setContent("新增用户:" + user.getUsername());
			log.setOperType(Constants.SYSLOG_ADD);
			log.setTitle("新增用户");
		} else {
			log.setContent("编辑用户:" + user.getUsername());
			log.setOperType(Constants.SYSLOG_EDIT);
			log.setTitle("编辑用户");
			userService.updateUser(user);
		}
		log.setLogType(Constants.SYSLOG_SYS);
		log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
		request.setAttribute(Constants.LOG_RECORD, log);
		out.print("{\"success\":true,\"msg\":\"\"}");
		out.flush();
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	@RequiresPermissions("user:index")
	public String showUpdateForm(@PathVariable("id") Long id, Model model) {
		setCommonData(model);
		model.addAttribute("user", userService.findOne(id));
		return "user/edit";
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	@RequiresPermissions("user:index")
	public @ResponseBody
	Message delete(@PathVariable("id") Long id,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {
		userService.deleteUser(id);
		SysOperLog log = new SysOperLog();
		log.setContent("删除用户:" + id);
		log.setOperType(Constants.SYSLOG_DELETE);
		log.setTitle("删除用户");
		log.setLogType(Constants.SYSLOG_SYS);
		log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
		request.setAttribute(Constants.LOG_RECORD, log);
		return new Message(true, "");
	}

	@RequestMapping(value = "/{id}/changePassword", method = RequestMethod.GET)
	@RequiresPermissions("user:index")
	public String showChangePasswordForm(@PathVariable("id") Long id,
			Model model) {
		model.addAttribute("user", userService.findOne(id));
		model.addAttribute("op", "修改密码");
		return "user/changePassword";
	}

	@RequestMapping(value = "/resetPassword/{id}", method = RequestMethod.GET)
	@RequiresPermissions("user:index")
	public String resetPasswordForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", userService.findOne(id));
		model.addAttribute("op", "重置密码密码");
		return "user/resetPassword";
	}

	@RequestMapping(value = "/resetPassword/{id}", method = RequestMethod.POST)
	@RequiresPermissions("user:index")
	public void resetPassword(Model model, String passwordaa,String md55,
			@PathVariable("id") Long id, HttpServletResponse response, HttpServletRequest request)
			throws Exception {
			if(!MD5Util.MD5(passwordaa).equalsIgnoreCase(md55)){
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("密码完整性遭到破坏");
				out.flush();
				return;
			}
			passwordaa = AES.Decrypt(passwordaa, SecurityUtils.getSubject()
					.getSession().getAttribute("aesKey").toString());
			if (passwordaa.length() < 8 || passwordaa.length() > 20) {
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("密码必须在8-20位.");
				out.flush();
				return;
			}
			userService.changeresetPassword(id, passwordaa);
			SysOperLog log = new SysOperLog();
			log.setContent("重置密码:" + id);
			log.setOperType(Constants.SYSLOG_DELETE);
			log.setTitle("重置密码");
			log.setLogType(Constants.SYSLOG_SYS);
			log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
			request.setAttribute(Constants.LOG_RECORD, log);
	}

	/***
	 * 修改自己密码
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public String showChangePasswordForm(Model model) {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		model.addAttribute("user", session.getAttribute(Constants.CURRENT_USER));
		model.addAttribute("op", "修改密码");
		return "user/changePassword";
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public void changePassword(Model model, String passworda, String password,String md5,
			HttpServletResponse response, HttpServletRequest request)
			throws Exception {
			if(!MD5Util.MD5(passworda).equalsIgnoreCase(md5)){
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("密码完整性遭到破坏");
				out.flush();
				return;
			}
			Session session = SecurityUtils.getSubject().getSession();
			if(session.getAttribute("aesKey")==null){
		    	String key = null;
		    	key = new SecureRandomNumberGenerator().nextBytes().toHex().substring(0,16);
		    	session.setAttribute("aesKey", key);
			}
			passworda = AES.Decrypt(passworda, SecurityUtils.getSubject().getSession().getAttribute("aesKey").toString());
			if (passworda.length() < 8 || passworda.length() > 20) {
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("密码必须在8-20位.");
				out.flush();
				return;
			}
			String pwd = AES.Decrypt(password, SecurityUtils.getSubject()
					.getSession().getAttribute("aesKey").toString());
			if (pwd.equals(passworda)) {
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("新密码不能和原始密码一致，请重新输入");
				out.flush();
				return;
			}
			User user = (User) SecurityUtils.getSubject().getSession()
					.getAttribute(Constants.CURRENT_USER);

			Subject subject = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(subject
					.getPrincipal().toString(), password);

			SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
					user.getUsername(), user.getPassword(),
					ByteSource.Util.bytes(user.getCredentialsSalt()),
					userRealm.getName());
			boolean matchs = credentialsMatcher.doCredentialsMatch(token, info);
			if (matchs) {
				userService.changePassword(user.getId(), passworda);
				SysOperLog log = new SysOperLog();
				log.setContent("用户修改密码:" + user.toString());
				log.setOperType(Constants.SYSLOG_EDIT);
				log.setTitle("修改密码");
				log.setLogType(Constants.SYSLOG_BUSINESS);
				log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
				request.setAttribute(Constants.LOG_RECORD, log);
			} else {
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("原始密码不正确，请重新输入");
				out.flush();
			}
	}

	@RequestMapping(value = "/{id}/changePassword", method = RequestMethod.POST)
	@RequiresPermissions("user:index")
	public String changePassword(@PathVariable("id") Long id,
			String newPassword, RedirectAttributes redirectAttributes) {
		userService.changePassword(id, newPassword);
		redirectAttributes.addFlashAttribute("msg", "修改密码成功");
		return "redirect:/user";
	}

	private void setCommonData(Model model) {
		model.addAttribute("organizationList", organizationService.findAll());
		model.addAttribute("roleList", roleService.findAll());
	}



	/**
	 * 保存分配角色
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("saverole/{id}")
	@RequiresPermissions("user:index")
	public @ResponseBody
	Message saveRole(@PathVariable("id") Long id, String roles,
			HttpServletRequest request) {
		SysOperLog log = new SysOperLog();
		log.setContent("用户：" + id + "分配角色");
		log.setOperType(1);
		log.setTitle("分配角色");
		log.setLogType(Constants.SYSLOG_SYS);
		log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
		request.setAttribute(Constants.LOG_RECORD, log);
		userService.saveRoles(id, roles);
		return new Message(true, "");
	}

	@RequestMapping("listNew")
	@RequiresPermissions("user:index")
	public @ResponseBody
	PageResult<UserAllInfo> listNew(String isSelect, Pagination page,
			String loginName, String realName, Integer locked, String syURoleId,
			Integer organizationId) {
		// 防止easy-ui的重复查询访问
		if (isSelect == null || isSelect.length() == 0
				|| (!(isSelect.equals("1")))) {
			return null;
		}

		// 获取数据
		return userService.pageSelect(page, loginName, realName, locked,
				syURoleId, organizationId);
	}
	
	/**
	 * 跳转到分配角色
	 * 
	 * @param id
	 * @return
	 */
	@RequiresPermissions("admin:user:setRole")
	@RequestMapping("role/{id}")
	public String role(@PathVariable("id") Long id, Model model) {
		List<Role> roles = userService.findRolesByUser(id);
		List<Role> all = roleService.findAll();
		all.removeAll(roles);
		model.addAttribute("roles", roles);
		model.addAttribute("notIn", all);
		model.addAttribute("userId", id);
		return "user/role";
	}
	
	/**
	 * 弹出账号审核页面
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequiresPermissions("admin:user:pass")
	@RequestMapping(value = "/lockedIs/{id}", method = RequestMethod.GET)
	public String lockedIs(@PathVariable("id") Long id, Model model) {
		setCommonData(model);
		model.addAttribute("user", userService.findOneUseInfo(id));
		return "user/lockedPass";
	}

	/**
	 * 弹出角色分配审核页面
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequiresPermissions("admin:user:pass")
	@RequestMapping(value = "/userRoleIs/{id}", method = RequestMethod.GET)
	public String userRoleIs(@PathVariable("id") Long id, Model model) {
		setCommonData(model);
		model.addAttribute("user", userService.findOneUseInfo(id));
		return "user/userRolePass";
	}
	
	//用户状态 锁/解锁    激活 -》锁定    锁定->激活
	@RequestMapping("closeOrOpen")
	@RequiresPermissions("user:index")
	public @ResponseBody Integer closeOrOpen(String userId, String locked,HttpServletRequest request){
		//影响行数
		int sucRol = 0;
		
		//状态转为整型
		int lockedInt = Integer.valueOf(locked);
		
		//激活 -》锁定
		if(lockedInt == 0){
			sucRol = userService.updateLockedByUseerID(userId, 1);
			
			// 日志
			if (sucRol > 0) {
				SysOperLog log = new SysOperLog();
				log.setContent("锁定账号，用户id:" + userId);
				log.setOperType(Constants.SYSLOG_EDIT);
				log.setTitle("锁定账号");
				log.setLogType(Constants.SYSLOG_SYS);
				log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
				request.setAttribute(Constants.LOG_RECORD, log);
			}
		}
		
		//锁定->激活
		if(lockedInt == 1){
			sucRol = userService.updateLockedByUseerID(userId, 0);
			
			// 激活
			if (sucRol > 0) {
				SysOperLog log = new SysOperLog();
				log.setContent("激活账号，用户id:" + userId);
				log.setOperType(Constants.SYSLOG_EDIT);
				log.setTitle("激活账号");
				log.setLogType(Constants.SYSLOG_SYS);
				log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
				request.setAttribute(Constants.LOG_RECORD, log);
			}
		}
		
		return sucRol;
	}
	
	//用户状态 注销    激活 -》注销
	@RequestMapping("canNot")
	@RequiresPermissions("user:index")
	public @ResponseBody Integer canNot(String userId, String locked,HttpServletRequest request){		
		int sucRol = userService.updateLockedByUseerID(userId, 2);
		
		// 日志
		if (sucRol > 0) {
			SysOperLog log = new SysOperLog();
			log.setContent("注销账号，用户id:" + userId);
			log.setOperType(Constants.SYSLOG_EDIT);
			log.setTitle("注销账号");
			log.setLogType(Constants.SYSLOG_SYS);
			log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
			request.setAttribute(Constants.LOG_RECORD, log);
		}
		
		return sucRol;
	}

	/**
	 * 激活或删除账户
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping("lockedUpdate")
	@RequiresPermissions("user:index")
	public @ResponseBody
	Integer lockedUpdate(String userId, String locdedId,HttpServletRequest request) {

		int sucNum = userService.lockedOrDeleteUser(userId, locdedId);

		// 日志
		if (sucNum > 0) {
			SysOperLog log = new SysOperLog();
			log.setContent("激活或删除账号状态，用户id:" + userId);
			log.setOperType(Constants.SYSLOG_EDIT);
			log.setTitle("激活或删除账号状态");
			log.setLogType(Constants.SYSLOG_SYS);
			log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
			request.setAttribute(Constants.LOG_RECORD, log);
		}

		return sucNum;
	}

	/**
	 * 将用户角色分配状态该有有效
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping("sysRoleUpdate")
	@RequiresPermissions("user:index")
	public @ResponseBody
	Integer sysRoleUpdate(String userId,HttpServletRequest request) {
		int sucNum = userService.sysRoleUpdate(userId);

		// 日志
		if (sucNum > 0) {
			SysOperLog log = new SysOperLog();
			log.setContent("将用户角色分配状态修改为有效，用户id:" + userId);
			log.setOperType(Constants.SYSLOG_EDIT);
			log.setTitle("将用户角色分配状态修改为有效");
			log.setLogType(Constants.SYSLOG_SYS);
			log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
			request.setAttribute(Constants.LOG_RECORD, log);
		}

		return sucNum;
	}

	/**
	 * 用户修改
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping("userUpdate")
	@RequiresPermissions("user:index")
	public @ResponseBody
	Integer userUpdate(String userId, String userName, String loginName,HttpServletRequest request) {

		int sucNum = userService
				.updateUserByUserId(userId, userName, loginName);

		// 日志
		if (sucNum > 0) {
			SysOperLog log = new SysOperLog();
			log.setContent("编辑用户，用户id:" + userId);
			log.setOperType(Constants.SYSLOG_EDIT);
			log.setTitle("编辑用户");
			log.setLogType(Constants.SYSLOG_SYS);
			log.setResult(Constants.SYSLOG_RESULT_SUCCESS);
			request.setAttribute(Constants.LOG_RECORD, log);
		}

		return sucNum;
	}

}
