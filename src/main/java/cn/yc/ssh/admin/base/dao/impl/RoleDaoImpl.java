package cn.yc.ssh.admin.base.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import cn.yc.ssh.admin.base.dao.RoleDao;
import cn.yc.ssh.admin.base.entity.Resource;
import cn.yc.ssh.admin.base.entity.Role;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.PageSqlUtil;
import cn.yc.ssh.admin.base.util.Pagination;

@Repository
public class RoleDaoImpl implements RoleDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate npjd;

	public Role createRole(final Role role) {
		final String sql = "insert into sys_role(id,role, name,state) values(seq_sys_role.nextval,?,?,'1')";

		jdbcTemplate.update(sql, role.getRole(), role.getName());
		return role;
	}

	@Override
	public Role updateRole(Role role) {
		final String sql = "update sys_role set state = '1',role=?, name=? where id=?";
		jdbcTemplate.update(sql, role.getRole(), role.getName(), role.getId());
		return role;
	}

	public void deleteRole(Long roleId) {
		//按要求，删除要改为状态为待删除
		//final String sql = "delete from sys_role where id=?";
		final String sql = "update sys_role set state = '2' where id=?";
		
		jdbcTemplate.update(sql, roleId);
	}

	@Override
	public Role findOne(Long roleId) {
		final String sql = "select * from sys_role where id=?";
		List<Role> roleList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Role>(Role.class), roleId);
		if (roleList.size() == 0) {
			return null;
		}
		return roleList.get(0);
	}

	@Override
	public List<Role> findAll() {
		final String sql = "select id, role, name from sys_role where role not in('TDMS001','TDMS002','TDMS003','TDMS004','TDMS005')";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Role>(Role.class));
	}

	@Override
	public List<Resource> findByRole(Long roleId) {
		String sql = "select * from sys_resource where id in(select resource_id from sys_role_resource where role_id=?)";
		return jdbcTemplate.query(sql,new BeanPropertyRowMapper<Resource>(Resource.class), roleId);
	}

	@Override
	public PageResult<Role> find(Pagination page, Role role) {
		//查询角色
		String sql = "select sys_role.* ,  "+
					"(CASE sys_role.STATE WHEN 0 THEN '生效' WHEN 1 THEN '无效' WHEN 2 THEN '待删除' ELSE '其他' END ) as stateName  "+ 
					"from sys_role  "+
					"where 1=1 ";
		Map<String,Object> paramMap = new HashMap<String, Object>();
		//角色代码筛选
		if(StringUtils.hasLength(role.getRole())){
			sql = sql + " and ROLE like :role";
			paramMap.put("role", "%"+role.getRole()+"%");
		}
		
		//角色名称筛选
		if(StringUtils.hasLength(role.getName())){
			sql = sql + " and NAME like :name";
			paramMap.put("name", "%"+role.getName()+"%");
		}
		//角色状态筛选
		if(StringUtils.hasLength(role.getState())){
			sql = sql + " and STATE =:state";
			paramMap.put("state", Integer.parseInt(role.getState()));
		}
		
		//排序
		sql = sql + " ORDER BY ROLE asc ,id asc ";
		
		//查询数据
		List<Role> userList = npjd.query(PageSqlUtil.getPageSql(sql,page),paramMap, new BeanPropertyRowMapper<Role>(Role.class));
		int count = npjd.queryForObject(PageSqlUtil.getCountSql(sql), paramMap, Integer.class);
		return new PageResult<Role>(userList, count);
	}

	@Override
	public List<Resource> findResources(Long roleId) {
		
		//获取该用户的资源表资源
		String sql = "select * from sys_resource where id in(select resource_id from sys_role_resource where role_id=?) ";
		
		return jdbcTemplate.query(sql,new BeanPropertyRowMapper<Resource>(Resource.class), roleId);
	}

	@Override
	public void saveResources(Long roleId, String resources,String power) {
		
		//用户角色状态改为无效
		String sql1 = "update sys_role set state = '1' where id=?";
		jdbcTemplate.update(sql1, roleId);
		
		//删除旧的与该用户有关的sys_role_resource,且删除根目录菜单
		String getResId = "";
		if(power.equals("XTZY")){//系统资源
			getResId = " and  RESOURCE_ID NOT IN ( SELECT ID from SYS_RESOURCE where RES_TYPE = 1 and PARENTID != '0' )";
		}else if(power.equals("YWZY")){//业务资源
			getResId = " and  RESOURCE_ID NOT IN ( SELECT ID from SYS_RESOURCE where RES_TYPE = 0 and PARENTID != '0' )";
		}		
		
		if(power.equals("no")){ //无访问资源
			
		}else{
			//删除资源
			String sql = "delete from sys_role_resource where role_id=? " + getResId;
			jdbcTemplate.update(sql, roleId);
		}		
		
		//插入 新的sys_role_resource
		String sql2 = "insert into sys_role_resource(role_id,resource_id,STATE) values(?,?,1)";
		String[] resources2 = resources.split(",");
		
		//将用户资源数组转为List,便于后续
		List<String> roleResIdList = new ArrayList<String>();
		for(String resource : resources2){
			roleResIdList.add(resource);
		}
		
		//获取跟菜单ID
		List<Resource> rootMenuList = new ArrayList<Resource>();
		String getRootSql = "select * from SYS_RESOURCE where PARENTID = '0' ";
		rootMenuList  = jdbcTemplate.query(getRootSql, new BeanPropertyRowMapper<Resource>(Resource.class));
		
		//判断用户资源中是否包含跟菜单，若不包含这几个根目录则加上这几个资源ID
		for(Resource root :rootMenuList){
			String rootId = String.valueOf(root.getId());
			if(!roleResIdList.contains(rootId)){
				roleResIdList.add(rootId);
			}
		}
			
		//插入数据库
		List<Object[]> params = new ArrayList<Object[]>();
		for(String resource :roleResIdList){
			Object[] param = new Object[2];
			param[0] = roleId;
			param[1] = resource;
			if (resource.matches("^\\d+$")) {
				params.add(param);
			}
		}
		jdbcTemplate.batchUpdate(sql2, params);
	}

	@Override
	public String resTypeForNowUser(long userId) {
		List<String> codeList = new ArrayList<String>();
		String sql = "SELECT SYS_ROLE.ROLE FROM   "+
					"SYS_USER LEFT JOIN SYS_USER_ROLE ON SYS_USER.ID = SYS_USER_ROLE.USER_ID  and SYS_USER_ROLE.STATE = '0'  "+
					"LEFT JOIN SYS_ROLE ON SYS_USER_ROLE.ROLE_ID = SYS_ROLE.ID   and SYS_ROLE.STATE = '0'  "+
					"where SYS_ROLE.ROLE IN ('TDMS001','TDMS003','B0306','TDMS002','TDMS005')  "+
					"and SYS_USER.ID = ?";
		codeList = jdbcTemplate.queryForList(sql,String.class,userId);
		
		//根据角色编码看用户可以访问sys_resource中那些资源，当前设置的课访问资源的角色为 培训教务部_项目管理员 ，身份管理员 ，业务管理员
		// "no" 代表无访问权限  ， "XTZY"代表系统资源, "YWZY"代办业务资源,"all"代表全部资源
		String powr = "no";
		//根据系统定义：TDMS001可以访问系统资源，TDMS003可以访问业务资源，B0306和TDMS002可以访问所有，
		if(codeList.contains("TDMS001") && codeList.contains("TDMS003")){
			powr = "all";
		}else if(codeList.contains("TDMS002")){
			powr = "all";
		}else if(codeList.contains("TDMS005")){
			powr = "all";
		}else if(codeList.contains("B0306")){
			powr = "all";
		}else if(codeList.contains("TDMS001")){
			powr = "XTZY";
		}else if(codeList.contains("TDMS003")){
			powr = "YWZY";
		}
		
		return powr;
	}

	@Override
	public Role getRoleByroleId(Long roleId) {
			
		
		//根据id，获取角色信息
		String sql = "select sys_role.* ,  "+
					"(CASE sys_role.STATE WHEN 0 THEN '生效' WHEN 1 THEN '无效' WHEN 2 THEN '待删除' ELSE '其他' END ) as stateName  "+ 
					"from sys_role where ID = ? ";
		List<Role> userList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Role>(Role.class),roleId);
		
		//返回角色
		if(userList.size()>0){
			return userList.get(0);
		}else{
			return null;
		}
	}

	@Override
	public int rolePass(Long roleId, String stateId) {
		
		int suc = 0;
		
		//根据用户状态进行不同操作
		if(stateId.equals("1")){//角色审核
			//sys_role表中该角色STATE修改为0，即有效
			String sysRoleUpdate = "update sys_role set state = '0' where id=? ";		
			int sysRoleUpdateCount = jdbcTemplate.update(sysRoleUpdate, roleId);
			
			//sys_role_resource表中与该角色关联的state置为0，即有效
			String sysRoleResUpdate = "update sys_role_resource set STATE = '0' where role_id = ? ";	
			int sysRoleResUpdateCount = jdbcTemplate.update(sysRoleResUpdate, roleId);
			
			//影响行数
			suc = sysRoleUpdateCount + sysRoleResUpdateCount;
			
		}else if(stateId.equals("2")){//角色删除	
			//删除sys_role_resource表中该roleID数据
			String sysRoleResDeleteSql ="delete from sys_role_resource where role_id = ? "; 
			int sysRoleResConut =  jdbcTemplate.update(sysRoleResDeleteSql, roleId);
			
			//删除sys_role表中该数据
			String sysRoleDeleteSql = "delete from sys_role where id=? ";
			int sysRoleCount = jdbcTemplate.update(sysRoleDeleteSql, roleId);
			
			//影响行数
			suc = sysRoleResConut + sysRoleCount;
		}
		
		return suc;
		
	}

}