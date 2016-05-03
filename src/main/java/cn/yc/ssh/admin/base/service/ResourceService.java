package cn.yc.ssh.admin.base.service;

import java.util.List;
import java.util.Set;

import cn.yc.ssh.admin.base.entity.Resource;

public interface ResourceService {


    public Resource createResource(Resource resource);
    public Resource updateResource(Resource resource);
    public void deleteResource(Long resourceId);

    Resource findOne(Long resourceId);
    List<Resource> findAll();

    /**
     * 得到资源对应的权限字符串
     * @param resourceIds
     * @return
     */
    Set<String> findPermissions(Set<Long> resourceIds);

    /**
     * 根据用户权限得到菜单
     * @param permissions
     * @return
     */
    List<Resource> findMenus(Set<String> permissions);
    
    String hasChildren(Long resourceId);
    
    public List<Resource> findMenus(Set<String> permissions,Long resourceId);
    /**
     * 用于
     * @return
     */
	public List<Resource> tree();
	public void updatePic(Resource r);
	public List<Resource> frontList(Integer start,Integer limit);
	public void removeByIds(String ids);
	
	
	public List<Resource> treeByPower(String power);
}