package cn.yc.ssh.admin.base.service;

import java.util.List;

import cn.yc.ssh.admin.base.entity.Organization;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.Pagination;

public interface OrganizationService {


    public Organization createOrganization(Organization organization);
    public Organization updateOrganization(Organization organization);
    public void deleteOrganization(Long organizationId);

    Organization findOne(Long organizationId);
    List<Organization> findAll();


    public List<Organization> findByParent(Long parentId);
    
	public PageResult<Organization> find(Organization organization, Pagination page);
	public PageResult<Organization> findByPID(Long pId, Pagination page);
}
