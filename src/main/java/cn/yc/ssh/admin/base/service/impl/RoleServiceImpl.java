package cn.yc.ssh.admin.base.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.yc.ssh.admin.base.dao.RoleDao;
import cn.yc.ssh.admin.base.entity.Resource;
import cn.yc.ssh.admin.base.entity.Role;
import cn.yc.ssh.admin.base.service.ResourceService;
import cn.yc.ssh.admin.base.service.RoleService;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.Pagination;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDao roleDao;
	@Autowired
	private ResourceService resourceService;

	public Role createRole(Role role) {
		return roleDao.createRole(role);
	}

	public Role updateRole(Role role) {
		return roleDao.updateRole(role);
	}

	public void deleteRole(Long roleId) {
		roleDao.deleteRole(roleId);
	}

	@Override
	public Role findOne(Long roleId) {
		return roleDao.findOne(roleId);
	}

	@Override
	public List<Role> findAll() {
		return roleDao.findAll();
	}

	@Override
	public Set<String> findRoles(Long... roleIds) {
		Set<String> roles = new HashSet<String>();
		for (Long roleId : roleIds) {
			Role role = findOne(roleId);
			if (role != null) {
				roles.add(role.getRole());
			}
		}
		return roles;
	}

	@Override
	public Set<String> findPermissions(Long[] roleIds) {
		Set<Long> resourceIds = new HashSet<Long>();
		for (Long roleId : roleIds) {
			Role role = findOne(roleId);
			if (role != null) {
				resourceIds.addAll(role.getResourceIds());
			}
		}
		return resourceService.findPermissions(resourceIds);
	}

	public List<Resource> findByRole(Long roleId) {
		return roleDao.findByRole(roleId);
	}

	@Override
	public PageResult<Role> find(Pagination page, Role role) {
		return roleDao.find(page, role);
	}

	@Override
	public List<Resource> findResources(Long roleId) {
		return roleDao.findResources(roleId);
	}

	@Override
	public void saveResources(Long roleId, String resources ,String power) {
		 roleDao.saveResources(roleId, resources, power);
	}

	@Override
	public List<Role> listAll() {
		
		return roleDao.findAll();
	}

	@Override
	public String resTypeForNowUser(long userId) {
		
		return roleDao.resTypeForNowUser(userId);
	}

	@Override
	public Role getRoleByroleId(Long roleId) {
		
		return roleDao.getRoleByroleId(roleId);
	}

	@Override
	public int rolePass(Long roleId, String stateId) {
		
		return roleDao.rolePass(roleId, stateId);
	}
}