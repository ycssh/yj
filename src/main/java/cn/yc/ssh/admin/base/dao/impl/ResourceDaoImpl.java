package cn.yc.ssh.admin.base.dao.impl;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.yc.ssh.admin.Constants;
import cn.yc.ssh.admin.base.dao.ResourceDao;
import cn.yc.ssh.admin.base.entity.Resource;
import cn.yc.ssh.common.CommonUtils;

@Repository
public class ResourceDaoImpl implements ResourceDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Resource createResource(final Resource resource) {
		final String sql = "insert into sys_resource(id,name, type, url, permission, parentId,SHOWINFRONT,PICNAME) "
				+ "values(seq_sys_resource.nextval,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, resource.getName(), resource.getType().name(),
				resource.getUrl(), resource.getPermission(),
				resource.getParentId(), resource.getShowInFront(),
				resource.getPicName());
		return resource;
	}

	@Override
	public Resource updateResource(Resource resource) {
		final String sql = "update sys_resource set name=?, type=?, url=?, permission=?, parentId=?,SHOWINFRONT=? where id=?";
		jdbcTemplate.update(sql, resource.getName(), resource.getType().name(),
				resource.getUrl(), resource.getPermission(),
				resource.getParentId(), resource.getShowInFront(),
				resource.getId());
		return resource;
	}

	public void deleteResource(Long resourceId) {
		final String deleteSelfSql = "delete from sys_resource where id=?";
		jdbcTemplate.update(deleteSelfSql, resourceId);
	}

	@Override
	public Resource findOne(Long resourceId) {
		final String sql = "select id, name, type, url, permission, parentId,SHOWINFRONT,PICNAME from sys_resource where id=?";
		List<Resource> resourceList = jdbcTemplate
				.query(sql,
						new BeanPropertyRowMapper<Resource>(Resource.class),
						resourceId);
		if (resourceList.size() == 0) {
			return null;
		}
		return resourceList.get(0);
	}

	@Override
	public List<Resource> findAll() {
		final String sql = "select id, name, type, url, permission, parentid,SHOWINFRONT,PICNAME from sys_resource order by id asc";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Resource>(Resource.class));
	}

	@Override
	public String hasChildren(Long resourceId) {
		String state = Constants.TREE_CLOSE;
		final String sql = "select count(*) from sys_resource where parent_id=? and type='menu'";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class,
				resourceId);
		if (count == 0) {
			state = Constants.TREE_OPEN;
		}
		return state;
	}

	@Override
	public List<Resource> findByParent(Long resourceId) {
		final String sql = "select id, name, type, url, permission, parent_id, parent_ids, available from "
				+ "sys_resource where parent_id=? order by concat(parent_ids, id) asc";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Resource>(
				Resource.class), resourceId);
	}

	@Override
	public void updatePic(Resource r) {

		final String sql = "update sys_resource set picName=? where id=?";
		jdbcTemplate.update(sql, r.getPicName(), r.getId());
	}

	@Override
	public List<Resource> findFrontList(int start, int limit) {
		String username = SecurityUtils.getSubject().getPrincipal().toString();
		String sql = "select distinct t.* from sys_resource t,sys_role_resource t1,sys_user_role t2,sys_user t3 where t.id=t1.resource_id and t1.role_id=t2.role_id and t2.user_id=t3.id and t3.username=?  and t1.state=0 and t2.state=0 and t.showinfront='1' order by t.id";
		List<Resource> resources = jdbcTemplate.query(
				CommonUtils.getPavSql(sql, start, limit),
				new BeanPropertyRowMapper<Resource>(Resource.class),username);
		return resources;
	}

	@Override
	public void deleteByIds(String ids) {
		String[] arr = ids.split(",");
		String sql = "delete from sys_resource where id =?";
		for(String id:arr){
			jdbcTemplate.update(sql, id);
		}
	}

	@Override
	public List<Resource> findAllByPower(String power) {

		// 获取该用户的资源表资源
		String sql = "select id, name, type, url, permission, parentid,SHOWINFRONT,PICNAME from sys_resource where 1=1 ";
		// power 为"no" 代表无访问权限 ， "XTZY"代表系统资源, "YWZY"代办业务资源,"all"代表全部资源
		if (power.equals("no")) { // 无访问资源
			return null;
		} else if (power.equals("XTZY")) {// 系统资源
			sql = sql + " and RES_TYPE in ('0')";
		} else if (power.equals("YWZY")) {// 业务资源
			sql = sql + " and RES_TYPE in ('1')";
		} else if (power.equals("all")) {// 所有资源
		}

		// 加上根目录的几个菜单将其 并集 合在一起
		sql = "SELECT * from ( "
				+ sql
				+ " UNION select id, name, type, url, permission, parentid,SHOWINFRONT,PICNAME from sys_resource  where parentid = '0' )order by id asc";

		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Resource>(
				Resource.class));

	}

}