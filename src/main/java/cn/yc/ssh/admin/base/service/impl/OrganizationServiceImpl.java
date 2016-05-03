package cn.yc.ssh.admin.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.yc.ssh.admin.base.dao.OrganizationDao;
import cn.yc.ssh.admin.base.entity.Organization;
import cn.yc.ssh.admin.base.service.OrganizationService;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.Pagination;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    @Autowired
    private OrganizationDao organizationDao;

    @Override
    public Organization createOrganization(Organization organization) {
        return organizationDao.createOrganization(organization);
    }

    @Override
    public Organization updateOrganization(Organization organization) {
        return organizationDao.updateOrganization(organization);
    }

    @Override
    public void deleteOrganization(Long organizationId) {
        organizationDao.deleteOrganization(organizationId);
    }

    @Override
    public Organization findOne(Long organizationId) {
        return organizationDao.findOne(organizationId);
    }

    @Override
    public List<Organization> findAll() {
        return organizationDao.findAll();
    }

	@Override
	public List<Organization> findByParent(Long parentId) {
		return organizationDao.findByParent(parentId);
	}

	@Override
	public PageResult<Organization> find(Organization organization,
			Pagination page) {
		return organizationDao.find(organization, page);
	}

	@Override
	public PageResult<Organization> findByPID(Long pId, Pagination page) {
		
		return organizationDao.findByPID(pId,page);
	}

}