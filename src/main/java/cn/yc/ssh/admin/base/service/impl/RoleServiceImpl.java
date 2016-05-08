package cn.yc.ssh.admin.base.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.yc.ssh.admin.base.mybatis.mapper.ResourceMapper;
import cn.yc.ssh.admin.base.mybatis.mapper.RoleMapper;
import cn.yc.ssh.admin.base.mybatis.mapper.RoleResourceMapper;
import cn.yc.ssh.admin.base.mybatis.model.Resource;
import cn.yc.ssh.admin.base.mybatis.model.Role;
import cn.yc.ssh.admin.base.mybatis.model.RoleResource;
import cn.yc.ssh.admin.base.mybatis.model.UserRole;
import cn.yc.ssh.admin.base.service.RoleService;
import cn.yc.ssh.admin.base.util.Pagination;

import com.github.pagehelper.Page;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private ResourceMapper resourceMapper;
	@Autowired
	private RoleResourceMapper roleResourceMapper;
	
	@Override
	public Role createRole(Role role) {
		roleMapper.insert(role);
		return role;
	}

	@Override
	public Role updateRole(Role role) {
		roleMapper.updateByPrimaryKeySelective(role);
		return role;
	}

	@Override
	public void deleteRole(Long roleId) {
		roleMapper.deleteByPrimaryKey(roleId);
	}

	@Override
	public Role findOne(Long roleId) {
		return roleMapper.selectByPrimaryKey(roleId);
	}

	@Override
	public List<Role> findAll() {
		 return roleMapper.findAll();
	}

	@Override
	public Set<String> findRoles(Long... roleIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> findPermissions(Long[] roleIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Resource> findResByRole(Long roleId) {
		return resourceMapper.selectByRole(roleId);
	}

	@Override
	public Page<Role> find(Pagination page, Role role) {
		Page<Role> pages = roleMapper.selectByPage(role, new RowBounds(page.getPage(), page.getRows()));
		return pages;
	}

	@Override
	public void saveResources(Long roleId, String resources) {
		roleResourceMapper.deleteByRole(roleId);
		if(StringUtils.hasLength(resources)){
			List<RoleResource> roleResources = new ArrayList<RoleResource>();
			for(String resource:resources.split(",")){
				if(StringUtils.hasLength(resource)){
					RoleResource roleResource = new RoleResource();
					roleResource.setRoleId(roleId);
					roleResource.setResourceId(Long.parseLong(resource));
					roleResources.add(roleResource);
				}
			}
			if(roleResources.size()>0)
			roleResourceMapper.insertBatch(roleResources);
		}
	}

	@Override
	public List<Role> listAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String resTypeForNowUser(long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role getRoleByroleId(Long roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int rolePass(Long roleId, String stateId) {
		// TODO Auto-generated method stub
		return 0;
	}
/*
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
	}*/
}