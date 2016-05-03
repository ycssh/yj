package cn.yc.ssh.admin.base.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.yc.ssh.admin.base.dao.UserDao;
import cn.yc.ssh.admin.base.entity.Resource;
import cn.yc.ssh.admin.base.entity.Role;
import cn.yc.ssh.admin.base.entity.User;
import cn.yc.ssh.admin.base.entity.UserAllInfo;
import cn.yc.ssh.admin.base.service.RoleService;
import cn.yc.ssh.admin.base.service.UserService;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.Pagination;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private PasswordHelper passwordHelper;
	@Autowired
	private RoleService roleService;

	/**
	 * 创建用户
	 * 
	 * @param user
	 */
	public User createUser(User user) {
//		user.setPassword("password");
		// 加密密码
		passwordHelper.encryptPassword(user);
		return userDao.createUser(user);
	}

	@Override
	public User updateUser(User user) {
		return userDao.updateUser(user);
	}

	@Override
	public void deleteUser(Long userId) {
		userDao.deleteUser(userId);
	}

	/**
	 * 修改密码
	 * 
	 * @param userId
	 * @param newPassword
	 */
	public void changePassword(Long userId, String newPassword) {
		User user = userDao.findOne(userId);
		user.setPassword(newPassword);
		passwordHelper.encryptPassword(user);
		userDao.updatePwd(user);
	}
	public void changeresetPassword(Long userId, String newPassword) {
		User user = userDao.findOne(userId);
		user.setPassword(newPassword);
		passwordHelper.encryptPassword(user);
		userDao.updatePwdReset(user);
	}

	@Override
	public User findOne(Long userId) {
		return userDao.findOne(userId);
	}

	@Override
	public List<User> findAll() {
		return userDao.findAll();
	}
	
	@Override
	public List<User> findWithOutAdmin() {
		return userDao.findWithOutAdmin();
	}

	/**
	 * 根据用户名查找用户
	 * 
	 * @param username
	 * @return
	 */
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	/**
	 * 根据用户名查找其角色
	 * 
	 * @param username
	 * @return
	 */
	public Set<String> findRoles(String username) {
		User user = findByUsername(username);
		if (user == null) {
			return Collections.emptySet();
		}
		return roleService.findRoles(user.getRoleIds().toArray(new Long[0]));
	}

	/**
	 * 根据用户名查找其权限
	 * 
	 * @param username
	 * @return
	 */
	public Set<String> findPermissions(String username) {
		User user = findByUsername(username);
		if (user == null) {
			return Collections.emptySet();
		}
		return roleService.findPermissions(user.getRoleIds().toArray(
				new Long[0]));
	}

	@Override
	public PageResult<User> find(User user, Boolean cascade, Pagination page) {
		return userDao.find(user, cascade, page);
	}
	
	@Override
	public PageResult<UserAllInfo> pageSelect(Pagination page,
			String loginName, String realName, Integer locked, String syURoleId ,Integer organizationId) {
		
		return userDao.pageSelect(page, loginName, realName, locked, syURoleId ,organizationId);
	}

	public List<Role> findRolesByUse(Long userId) {
		return userDao.findRolesByUse(userId);
	}

	public List<Resource> findResByUse(Long userId) {
		return userDao.findResByUse(userId);
	}

	@Override
	public void saveRoles(Long id, String roles) {
		userDao.saveRoles(id, roles);
	}

	@Override
	public List<Role> findRolesByUser(Long id) {
		return userDao.findRolesByUser(id);
	}

	@Override
	public UserAllInfo findOneUseInfo(Long userId) {
		
		return userDao.findOneUseInfo(userId);
	}

	@Override
	public int lockedOrDeleteUser(String userId ,String locdedId) {
		
		return userDao.lockedOrDeleteUser(userId, locdedId);
	}

	@Override
	public int updateUserByUserId(String userId, String userName,
			String loginName) {
		
		return userDao.updateUserByUserId(userId, userName, loginName);
	}

	@Override
	public int sysRoleUpdate(String userId) {
		
		return  userDao.sysRoleUpdate(userId);
	}
	public void updateState(int state,String username){
		userDao.updateState(state, username);
	}

	@Override
	public int updateLockedByUseerID(String userId, int locked) {
		
		return userDao.updateLockedByUseerID(userId, locked);
	}

}