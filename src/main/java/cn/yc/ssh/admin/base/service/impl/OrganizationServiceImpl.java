package cn.yc.ssh.admin.base.service.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.yc.ssh.admin.base.mybatis.mapper.OrganizationMapper;
import cn.yc.ssh.admin.base.mybatis.model.Organization;
import cn.yc.ssh.admin.base.service.OrganizationService;
import cn.yc.ssh.admin.base.util.Pagination;

import com.github.pagehelper.Page;

@Service
public class OrganizationServiceImpl implements OrganizationService {
	@Autowired
	private OrganizationMapper organizationMapper;

	@Override
	public Organization createOrganization(Organization organization) {
		organizationMapper.insert(organization);
		return organization;
	}

	@Override
	public Organization updateOrganization(Organization organization) {
		organizationMapper.updateByPrimaryKeySelective(organization);
		return organization;
	}

	@Override
	public void deleteOrganization(Long organizationId) {
		organizationMapper.deleteByPrimaryKey(organizationId);
	}

	@Override
	public Organization findOne(Long organizationId) {
		return organizationMapper.selectByPrimaryKey(organizationId);
	}

	@Override
	public List<Organization> findAll() {
		return organizationMapper.findAll();
	}

	@Override
	public List<Organization> findByParent(Long parentId) {
		return organizationMapper.findByParent(parentId);
	}

	@Override
	public Page<Organization> find(Organization organization, Pagination page) {
		return organizationMapper.find(organization, new RowBounds(page.getPage(), page.getRows()));
	}

	@Override
	public Page<Organization> findByPID(Long pId, Pagination page) {
		return organizationMapper.findByPID(pId, new RowBounds(page.getPage(), page.getRows()));
	}

}