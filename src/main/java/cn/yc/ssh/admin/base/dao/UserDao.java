package cn.yc.ssh.admin.base.dao;

import java.util.List;

import cn.yc.ssh.admin.base.entity.Resource;
import cn.yc.ssh.admin.base.entity.Role;
import cn.yc.ssh.admin.base.entity.User;
import cn.yc.ssh.admin.base.entity.UserAllInfo;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.Pagination;

public interface UserDao {
    public User createUser(User user);
    public User updateUser(User user);
    public void deleteUser(Long userId);
    User findOne(Long userId);
    List<User> findAll();
    User findByUsername(String username);
    /**
     * 分页查询
     * @param user
     * @param cascade
     * @param page
     * @return
     */
	public PageResult<User> find(User user, Boolean cascade, Pagination page);
	
	/**
	 * 页面显示
	 * @param page 页码信息
	 * @param loginName 登陆名称
	 * @param realName 姓名
	 * @param locked 账户状态
	 * @param syURoleId 角色分配状态
	 * @param organizationId
	 * @return
	 */
	public PageResult<UserAllInfo> pageSelect(Pagination page , String loginName , String realName , Integer locked ,String syURoleId ,Integer organizationId);
	
	
    //根据用户ID，获取页面显示的一条数据
    public UserAllInfo findOneUseInfo(Long userId);
    
    //根据用户Id和账户状态 ，进行账户激活 或者删除
    public int lockedOrDeleteUser(String userId ,String locdedId);
    
    //根据用户ID 修改用户姓名
    public int updateUserByUserId(String userId,String userName,String loginName);
    
    //根据用户ID,将该用户的所有角色状态改为有效
    public int sysRoleUpdate(String userId);
    
    //根据用户id修改用户状态
    public int updateLockedByUseerID (String userId , int locked);
	
	
	List<Role> findRolesByUse(Long userId);
	List<Resource> findResByUse(Long userId);
	public void saveRoles(Long id, String roles);
	public List<Role> findRolesByUser(Long id);
	
	void updatePwd(User user);
	List<User> findWithOutAdmin();
	
	public void updateState(int state,String username);
	public void updatePwdReset(User user);

}
