package cn.yc.ssh.admin.base.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cn.yc.ssh.admin.Constants;

@Controller
@RequestMapping("/sessions")
public class SessionController {
	@Autowired
	private SessionDAO sessionDAO;

	@RequestMapping()
	@RequiresPermissions("admin:session")
	public String list(Model model) {
		Collection<Session> sessions = sessionDAO.getActiveSessions();
		List<Session> list = new ArrayList<Session>();
		for (Session s : sessions) {
			if (s.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) != null) {
				list.add(s);
			}
		}
		model.addAttribute("sessions", list);
		model.addAttribute("sessionCount", list.size());
		return "sessions/list";
	}

	@RequestMapping("/{sessionId}/forceLogout")
	@RequiresPermissions("admin:session")
	public String forceLogout(@PathVariable("sessionId") String sessionId,
			RedirectAttributes redirectAttributes) {
		try {
			Session session = sessionDAO.readSession(sessionId);
			if (session != null) {
				session.setAttribute(Constants.SESSION_FORCE_LOGOUT_KEY,
						Boolean.TRUE);
			}
		} catch (Exception e) {/* ignore */
			e.printStackTrace();
		}
		redirectAttributes.addFlashAttribute("msg", "强制退出成功！");
		return "redirect:/sessions";
	}

}
