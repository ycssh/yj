package cn.yc.ssh.admin.base.web.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cn.yc.ssh.admin.Constants;
import cn.yc.ssh.admin.base.entity.Resource;
import cn.yc.ssh.admin.base.entity.User;
import cn.yc.ssh.admin.base.service.ResourceService;
import cn.yc.ssh.admin.base.service.RoleService;
import cn.yc.ssh.common.CommonUtils;
import cn.yc.ssh.common.FileUtils;

@Controller
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;
    
	@Autowired
	private RoleService roleService;

    @ModelAttribute("types")
    public Resource.ResourceType[] resourceTypes() {
        return Resource.ResourceType.values();
    }

    @RequestMapping(value="index", method = RequestMethod.GET)
    @RequiresPermissions("admin:resource:view")
    public String index(Model model) {
        return "resource/index";
    }
    
    @RequiresPermissions("admin:resource:view")
    @RequestMapping(value="list", method = RequestMethod.GET)
    public @ResponseBody List<Resource> list(Model model) {
        return resourceService.findAll();
    }
    

    @RequestMapping(value="tree", method = RequestMethod.GET)
    @RequiresPermissions("admin:resource:tree")
    public @ResponseBody List<Resource> tree() {
		//获取用户
		User user = (User) SecurityUtils.getSubject().getSession().getAttribute(Constants.CURRENT_USER);
		//获取用户ID
		Long userId = user.getId();
		//获取用户访问sys_resource权限 "no" 代表无访问权限  ， "XTZY"代表系统资源, "YWZY"代办业务资源,"all"代表全部资源
		String power = roleService.resTypeForNowUser(userId);
		//根据用户角色可访问的资源权限 获取数种应该显示的资源
    	return resourceService.treeByPower(power);
    }

    @RequiresPermissions("admin:resource:create")
    @RequestMapping(value = "/add/{parentId}", method = RequestMethod.GET)
    public String showAppendChildForm(@PathVariable("parentId") Long parentId, Model model) {
        Resource child = new Resource();
        child.setParentId(parentId);
        model.addAttribute("resource", child);
        return "resource/edit";
    }
    
    @RequiresPermissions("admin:resource:create")
    @RequestMapping(value = "/addRoot", method = RequestMethod.GET)
    public String showAddRoot(Model model) {
    	Resource child = new Resource();
    	child.setParentId((long) 0);
        model.addAttribute("resource", child);
        return "resource/edit";
    }

    @RequiresPermissions("admin:resource:create")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public @ResponseBody String save(Resource resource, RedirectAttributes redirectAttributes) {
    	
    	if(resource.getId()!=null){
    		resourceService.updateResource(resource);
            return "修改成功";
    	}else{
    		resourceService.createResource(resource);
            return "新增子节点成功";
    	}
    }

    @RequiresPermissions("admin:resource:create")
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("resource", resourceService.findOne(id));
        return "resource/edit";
    }

    @RequiresPermissions("admin:resource:delete")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public @ResponseBody String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        resourceService.deleteResource(id);
        return "删除成功";
    }
    
    @RequiresPermissions("admin:resource:delete")
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public @ResponseBody Map<String,Object> remove(String ids) {
        resourceService.removeByIds(ids);
        return CommonUtils.wrapObject("");
    }
    
    @RequestMapping("/frontList")
    public @ResponseBody List<Resource> frontList(Integer start,Integer limit){
    	List<Resource> resources = resourceService.frontList(start,limit);
    	
    	return resources;
    }
    
    @RequestMapping(value = "/uploadPic/{id}")
    public String uploadPicPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("resId", id);
        return "resource/uploadPic";
    }

    @SuppressWarnings("rawtypes")
	@RequestMapping("/uploadPic/upload")
    public @ResponseBody Map uploadPic(@RequestParam CommonsMultipartFile impFile,Long id){
    	
    	try {
			String fileName = FileUtils.saveFile(impFile);
			Resource r = resourceService.findOne(id);
			r.setPicName(fileName);
			
			resourceService.updatePic(r);
			return CommonUtils.wrapObject("");
		} catch (Exception e) {
			
			e.printStackTrace();
			return CommonUtils.wrapError("出错了");
		}
    }
    
    @RequestMapping("/uploadPic/showPic")
	public void readFile(Long resId,HttpServletResponse response){
		
		String fileFullName = resourceService.findOne(resId).getPicName();
		File f = new File(fileFullName);
		
		String fileName = fileFullName.substring(fileFullName.lastIndexOf("\\")+1);
		try {
			
			response.reset();
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
			response.addHeader("Content-Length", "" + f.length());
			response.setContentType("application/octet-stream");
			
			InputStream is = new FileInputStream(f);
			
			byte[] buff = new byte[1024];
			int len = 0;
			
			OutputStream os = new BufferedOutputStream(response.getOutputStream());
			while((len = is.read(buff))!=-1){
				os.write(buff, 0, len);
			}
			os.flush();
			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
