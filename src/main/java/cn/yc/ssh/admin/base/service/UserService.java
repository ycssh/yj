package cn.yc.ssh.admin.base.service;

import java.util.List;
import java.util.Set;

import cn.yc.ssh.admin.base.entity.Resource;
import cn.yc.ssh.admin.base.entity.Role;
import cn.yc.ssh.admin.base.entity.User;
import cn.yc.ssh.admin.base.entity.UserAllInfo;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.Pagination;

public interface UserService {

    /**
     * 创建用户
     * @param user
     */
    public User createUser(User user);

    public User updateUser(User user);

    public void deleteUser(Long userId);

    /**
     * 修改密码
     * @param userId
     * @param newPassword
     */
    public void changePassword(Long userId, String newPassword);


    User findOne(Long userId);
    
    //根据用户ID，获取页面显示的一条数据
    UserAllInfo findOneUseInfo(Long userId);
    
    

    List<User> findAll();

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    public User findByUsername(String username);

    /**
     * 根据用户名查找其角色
     * @param username
     * @return
     */
    public Set<String> findRoles(String username);

    /**
     * 根据用户名查找其权限
     * @param username
     * @return
     */
    public Set<String> findPermissions(String username);

    /**
     * 条件查询
     * @param user
     * @param cascade 是否级联查询子部门
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
	
	List<Role> findRolesByUse(Long userId);
	
    //根据用户Id和账户状态 ，进行账户激活 或者删除
    public int lockedOrDeleteUser(String userId ,String locdedId);
    
    //根据用户ID 修改用户姓名
    public int updateUserByUserId(String userId,String userName,String loginName);
    
    //根据用户ID,将该用户的所有角色状态改为有效
    public int sysRoleUpdate(String userId);
	
	List<Resource> findResByUse(Long userId);

	/**
	 * 保存分配的角色
	 * @param id
	 * @param roles
	 */
	public void saveRoles(Long id, String roles);

	/**
	 * 查找某个人的所有角色
	 * @param id
	 * @return
	 */
	public List<Role> findRolesByUser(Long id);

	List<User> findWithOutAdmin();

	public void updateState(int state,String username);
	
    //根据用户id修改用户状态
    public int updateLockedByUseerID (String userId , int locked);

	public void changeresetPassword(Long id, String passwordaa);

}