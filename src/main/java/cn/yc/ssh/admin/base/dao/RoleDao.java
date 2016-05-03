package cn.yc.ssh.admin.base.dao;

import java.util.List;

import cn.yc.ssh.admin.base.entity.Resource;
import cn.yc.ssh.admin.base.entity.Role;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.Pagination;

public interface RoleDao {

    public Role createRole(Role role);
    public Role updateRole(Role role);
    public void deleteRole(Long roleId);

    public Role findOne(Long roleId);
    public List<Role> findAll();
    
    /**
     * 查询某个角色的所有资源
     * @param roleId
     * @return
     */
    public List<Resource> findByRole(Long roleId);
	public PageResult<Role> find(Pagination page, Role role);
	public List<Resource> findResources(Long roleId);
	public void saveResources(Long roleId, String resources ,String power);
	
	//获取当前用户可看到资源类型
	public String resTypeForNowUser(long userId);
	
	//根据角色ID,获取角色信息
	public Role getRoleByroleId(Long roleId);
	
	//根据角色ID，角色状态，对角色进行审核
	public int rolePass(Long roleId ,String stateId);
	
	
}
