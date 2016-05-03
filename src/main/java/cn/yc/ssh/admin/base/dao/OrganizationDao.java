package cn.yc.ssh.admin.base.dao;

import java.util.List;

import cn.yc.ssh.admin.base.entity.Organization;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.Pagination;

/** 
 * @author 作者姓名 yc  E-mail: ycssh2@163.com
 * @version 创建时间：2014-5-5 下午01:54:39 
 * 类说明 
 */
public interface OrganizationDao {

    public Organization createOrganization(Organization organization);
    public Organization updateOrganization(Organization organization);
    public void deleteOrganization(Long organizationId);

    Organization findOne(Long organizationId);
    List<Organization> findAll();

    public List<Organization> findByParent(Long parentId);
	public PageResult<Organization> find(Organization organization,
			Pagination page);
	public PageResult<Organization> findByPID(Long pId, Pagination page);
}
