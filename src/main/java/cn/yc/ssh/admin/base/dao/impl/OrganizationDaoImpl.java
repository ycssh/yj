package cn.yc.ssh.admin.base.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.yc.ssh.admin.base.dao.OrganizationDao;
import cn.yc.ssh.admin.base.entity.Organization;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.PageSqlUtil;
import cn.yc.ssh.admin.base.util.Pagination;

@Repository
public class OrganizationDaoImpl implements OrganizationDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Organization createOrganization(final Organization organization) {
		final String sql = "insert into sys_organization(id, name, parentId,power) values(seq_sys_Organization.nextval,?,?,?)";
		jdbcTemplate.update(sql, organization.getName(),
				organization.getParentId(),organization.getPower());
		return organization;
	}

	@Override
	public Organization updateOrganization(Organization organization) {
		final String sql = "update sys_organization set name=? , power =? where id=?";
		jdbcTemplate.update(sql, organization.getName(),organization.getPower(), organization.getId());
		return organization;
	}

	public void deleteOrganization(Long organizationId) {
		final String deleteSelfSql = "delete from sys_organization where id=?";
		jdbcTemplate.update(deleteSelfSql, organizationId);
	}

	@Override
	public Organization findOne(Long organizationId) {
		final String sql = "select id, name, parentId from sys_organization where id=?";
		List<Organization> organizationList = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<Organization>(Organization.class),
				organizationId);
		if (organizationList.size() == 0) {
			return null;
		}
		return organizationList.get(0);
	}

	@Override
	public List<Organization> findAll() {
		final String sql = "select id, name, parentId from sys_organization";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Organization>(
				Organization.class));
	}

	@Override
	public List<Organization> findByParent(Long parentId) {
		final String sql = "select id, name, parentId,(select count(*) from"
				+ " sys_organization where parentId=t.id) isParent from sys_organization t where parentId=?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Organization>(
				Organization.class), parentId);
	}

	public List<Organization> findRoot() {
		final String sql = "select id, name, parentId,(select count(*) from"
				+ " sys_organization where parentId=t.id) isParent from sys_organization t where parentId is null";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Organization>(
				Organization.class));
	}

	@Override
	public PageResult<Organization> find(Organization organization,
			Pagination page) {
		String sql = "select id,name  from sys_organization where id=? or parentId=?";
		List<Organization> list = jdbcTemplate.query(
				PageSqlUtil.getPageSql(sql, page),
				new BeanPropertyRowMapper<Organization>(Organization.class),
				organization.getId(), organization.getId());
		int count = jdbcTemplate.queryForObject(PageSqlUtil.getCountSql(sql), Integer.class,
				organization.getId(), organization.getId());
		return new PageResult<Organization>(list, count);
	}

	@Override
	public PageResult<Organization> findByPID(Long pId, Pagination page) {
		String sql = "select id,name,parentId,power from sys_organization where parentId=?";
		if (pId == null) {
			sql = "select id,name,parentId  from sys_organization where parentId is null";
			List<Organization> list = jdbcTemplate
					.query(PageSqlUtil.getPageSql(sql, page),
							new BeanPropertyRowMapper<Organization>(
									Organization.class));
			int count = jdbcTemplate.queryForObject(PageSqlUtil.getCountSql(sql), Integer.class);
			return new PageResult<Organization>(list, count);
		}
		List<Organization> list = jdbcTemplate.query(
				PageSqlUtil.getPageSql(sql, page),
				new BeanPropertyRowMapper<Organization>(Organization.class),
				pId);
		int count = jdbcTemplate.queryForObject(PageSqlUtil.getCountSql(sql), Integer.class, pId);
		return new PageResult<Organization>(list, count);
	}
}