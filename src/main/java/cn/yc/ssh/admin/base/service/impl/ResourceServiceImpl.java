package cn.yc.ssh.admin.base.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.yc.ssh.admin.base.dao.ResourceDao;
import cn.yc.ssh.admin.base.entity.Resource;
import cn.yc.ssh.admin.base.service.ResourceService;

@Service
public class ResourceServiceImpl implements ResourceService {

	@Autowired
	private ResourceDao resourceDao;

	@Override
	public Resource createResource(Resource resource) {
		return resourceDao.createResource(resource);
	}

	@Override
	public Resource updateResource(Resource resource) {
		return resourceDao.updateResource(resource);
	}

	@Override
	public void deleteResource(Long resourceId) {
		resourceDao.deleteResource(resourceId);
	}

	@Override
	public Resource findOne(Long resourceId) {
		return resourceDao.findOne(resourceId);
	}

	@Override
	public List<Resource> findAll() {
		return resourceDao.findAll();
	}

	@Override
	public Set<String> findPermissions(Set<Long> resourceIds) {
		Set<String> permissions = new HashSet<String>();
		for (Long resourceId : resourceIds) {
			Resource resource = findOne(resourceId);
			if (resource != null
					&& !StringUtils.isEmpty(resource.getPermission())) {
				permissions.add(resource.getPermission());
			}
		}
		return permissions;
	}

	@Override
	public List<Resource> findMenus(Set<String> permissions) {
		List<Resource> allResources = findAll();
		List<Resource> menus = new ArrayList<Resource>();
		for (Resource resource : allResources) {
			if (resource.isRootNode()) {
				continue;
			}
			if (resource.getType() != Resource.ResourceType.menu) {
				continue;
			}
			if (!hasPermission(permissions, resource)) {
				continue;
			}
			menus.add(resource);
		}
		return menus;
	}

	@Override
	public List<Resource> findMenus(Set<String> permissions, Long resourceId) {
		List<Resource> allResources = resourceDao.findByParent(resourceId);
		List<Resource> menus = new ArrayList<Resource>();
		for (Resource resource : allResources) {
			// if(resource.isRootNode()) {
			// continue;
			// }
			if (resource.getType() != Resource.ResourceType.menu) {
				continue;
			}
			if (!hasPermission(permissions, resource)) {
				continue;
			}
			if (!StringUtils.hasLength(resource.getUrl())) {
				resource.setIsParent(true);
			}
			menus.add(resource);
		}
		return menus;
	}

	private boolean hasPermission(Set<String> permissions, Resource resource) {
		if (StringUtils.isEmpty(resource.getPermission())) {
			return true;
		}
		for (String permission : permissions) {
			WildcardPermission p1 = new WildcardPermission(permission);
			WildcardPermission p2 = new WildcardPermission(
					resource.getPermission());
			if (p1.implies(p2) || p2.implies(p1)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String hasChildren(Long resourceId) {
		return resourceDao.hasChildren(resourceId);
	}

	@Override
	public List<Resource> tree() {
		List<Resource> resources = resourceDao.findAll();

		List<Resource> resources2 = new ArrayList<Resource>();
		Set<Long> set = new HashSet<Long>();
		for (Resource resource : resources) {
			while (!set.contains(resource.getId())) {
				set.add(resource.getId());
				List<Resource> children = new ArrayList<Resource>();
				for (Resource resource2 : resources) {
					if (resource2.getParentId().equals(resource.getId())) {
						children.add(resource2);
					}
				}
				resource.setChildren(children);
			}
			if (resource.getParentId() == 0) {
				resources2.add(resource);
			}
		}
		return resources2;
	}

	@Override
	public void updatePic(Resource r) {
		
		resourceDao.updatePic(r);
	}

	@Override
	public List<Resource> frontList(Integer start,Integer limit) {
		
		List<Resource> resources = resourceDao.findFrontList(start==null?0:start,limit==null?1000:limit);
		
		List<Resource> resources2 = new ArrayList<Resource>();
		Set<Long> set = new HashSet<Long>();
		for (Resource resource : resources) {
			while (!set.contains(resource.getId())) {
				set.add(resource.getId());
				List<Resource> children = new ArrayList<Resource>();
				for (Resource resource2 : resources) {
					if (resource2.getParentId().equals(resource.getId())) {
						children.add(resource2);
					}
				}
				resource.setChildren(children);
			}
			if (resource.getParentId() == 0) {
				resources2.add(resource);
			}
		}
		return resources2;
	}

	@Override
	public void removeByIds(String ids) {
		
		resourceDao.deleteByIds(ids);
	}

	@Override
	public List<Resource> treeByPower(String power) {
		List<Resource> resources = resourceDao.findAllByPower(power);

		List<Resource> resources2 = new ArrayList<Resource>();
		Set<Long> set = new HashSet<Long>();
		for (Resource resource : resources) {
			while (!set.contains(resource.getId())) {
				set.add(resource.getId());
				List<Resource> children = new ArrayList<Resource>();
				for (Resource resource2 : resources) {
					if (resource2.getParentId().equals(resource.getId())) {
						children.add(resource2);
					}
				}
				resource.setChildren(children);
			}
			if (resource.getParentId() == 0) {
				resources2.add(resource);
			}
		}
		return resources2;
	}

}
