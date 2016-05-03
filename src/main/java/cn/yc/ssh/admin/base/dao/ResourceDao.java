package cn.yc.ssh.admin.base.dao;

import java.util.List;

import cn.yc.ssh.admin.base.entity.Resource;

public interface ResourceDao {

    public Resource createResource(Resource resource);
    public Resource updateResource(Resource resource);
    public void deleteResource(Long resourceId);

    Resource findOne(Long resourceId);
    List<Resource> findAll();
	public String hasChildren(Long resourceId);
	
	List<Resource> findByParent(Long resourceId);
	public void updatePic(Resource r);
	public List<Resource> findFrontList(int start,int limit);
	public void deleteByIds(String ids);
	
	
	List<Resource> findAllByPower(String power);
}