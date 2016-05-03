package cn.yc.ssh.admin.base.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import cn.yc.ssh.admin.base.dao.UserDao;
import cn.yc.ssh.admin.base.entity.Resource;
import cn.yc.ssh.admin.base.entity.Role;
import cn.yc.ssh.admin.base.entity.User;
import cn.yc.ssh.admin.base.entity.UserAllInfo;
import cn.yc.ssh.admin.base.util.PageResult;
import cn.yc.ssh.admin.base.util.PageSqlUtil;
import cn.yc.ssh.admin.base.util.Pagination;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate npjd;

	public User createUser(final User user) {
		String nowData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String sqlData = "to_date( '" + nowData + "' , 'YYYY-MM-DD HH24:MI:SS' )";
		final String sql = "insert into sys_user(id,name,organization_id, username, password, salt,CREATE_TIME,UPDATE_TIME,locked) values(seq_sys_user.nextval,?,?,?,?,?,"
				+ sqlData + "," + sqlData + ",3)";
		jdbcTemplate.update(sql, user.getName(), user.getOrganizationId(),
				user.getUsername(), user.getPassword(), user.getSalt());
		final String sql1 = "insert into sys_user_complete(username,password) values (?,?)";
		jdbcTemplate.update(sql1, user.getUsername(), user.getPassword());
		return user;
	}

	public void updateState(int state, String username) {
		String sql = "update sys_user set locked=? where username=?";
		jdbcTemplate.update(sql, state, username);
	}

	public User updateUser(User user) {
		String sql = "update sys_user set organization_id=?,username=?,name=? where id=?";
		jdbcTemplate.update(sql, user.getOrganizationId(), user.getUsername(),
				user.getName(), user.getId());
		return user;
	}

	public void updatePwd(User user) {
		String sql = "update sys_user set password=?,salt=?,update_time=sysdate where id=?";
		jdbcTemplate.update(sql, user.getPassword(), user.getSalt(),
				user.getId());
		String sql1 = "update sys_user_complete set password=? where username=?";
		jdbcTemplate.update(sql1, user.getPassword(), user.getUsername());
	}

	public void deleteUser(Long userId) {

		// 按要求不可以直接删除，要改为待删除。真删除要在账户审核中删除
		String sqlUpdate = "update sys_user set locked ='4' where ID = ?";
		jdbcTemplate.update(sqlUpdate, userId);
	}

	@Override
	public User findOne(Long userId) {
		String sql = "select * from sys_user where id=?";
		List<User> userList = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<User>(User.class), userId);
		if (userList.size() == 0) {
			return null;
		}
		return userList.get(0);
	}

	@Override
	public List<User> findAll() {
		String sql = "select id, organization_id, username, password, salt, locked from sys_user";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(
				User.class));
	}

	@Override
	public List<User> findWithOutAdmin() {
		String sql = "select id, organization_id, username, password, salt, locked from sys_user where username != 'sgtc_xmgl'";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(
				User.class));
	}

	@Override
	public User findByUsername(String username) {
		String sql = "select t1.*,t2.password pwd from sys_user t1,sys_user_complete t2 where t1.username = t2.username and t1.username=?";
		List<User> userList = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<User>(User.class), username);
		if (userList.size() == 0) {
			return null;
		}
		return userList.get(0);
	}

	@Override
	public PageResult<User> find(User user, Boolean cascade,
			Pagination pagination) {

		if (user == null) {
			return null;
		}
		if (user.getOrganizationId() == null || user.getOrganizationId() == 0) {
			return findAllUsers(pagination);
		}
		Map<String,Object> map = new HashMap<String, Object>();
		String sql = "select id,organization_id, username, locked,name from sys_user ";
		if (user.getOrganizationId() != null) {
			sql += " and organization_id=:organizationId";
			map.put("organizationId", user.getOrganizationId());
		}
		if (StringUtils.hasLength(user.getName())) {
			map.put("name", "%"+user.getName()+"%");
			sql += " and name like :name";
		}
		if (StringUtils.hasLength(user.getIdcard())) {
			sql += " and idcard like :idcard";
			map.put("idcard", "%"+user.getIdcard()+"%");
		}

		sql = sql.replaceFirst(" and ", " where ");
		List<User> userList = npjd.query(PageSqlUtil.getPageSql(sql, pagination), map,
				new BeanPropertyRowMapper<User>(User.class));
		int count = npjd.queryForObject(PageSqlUtil.getCountSql(sql),map,Integer.class);
		return new PageResult<User>(userList, count);
	}

	@Override
	public PageResult<UserAllInfo> pageSelect(Pagination page,
			String loginName, String realName, Integer locked,
			String syURoleId, Integer organizationId) {
		// 查询的sql语句
		String sql = "SELECT SYS_USER.ID AS userId,SYS_USER.USERNAME AS loginName,SYS_USER. NAME AS realName,SYS_ORGANIZATION. ID AS orgId,  "
				+ "SYS_ORGANIZATION. NAME AS orgName,SYS_USER. LOCKED,DICT. NAME AS userState,  "
				+ "WM_CONCAT(SYS_ROLE. NAME) AS roleName,WM_CONCAT(DISTINCT(SYS_USER_ROLE.STATE)) AS syURoleId,  "
				+ "WM_CONCAT(DISTINCT((CASE SYS_USER_ROLE.STATE WHEN 1 THEN '无效' WHEN 0 THEN '有效'	ELSE '其他' END ))) AS syURoleName  "
				+ "FROM SYS_USER  "
				+ "LEFT JOIN SYS_USER_ROLE ON SYS_USER. ID = SYS_USER_ROLE.USER_ID  "
				+ "LEFT JOIN SYS_ROLE ON SYS_USER_ROLE.ROLE_ID = SYS_ROLE. ID  "
				+ "LEFT JOIN SYS_ORGANIZATION ON SYS_USER.ORGANIZATION_ID = SYS_ORGANIZATION. ID  "
				+ "LEFT JOIN DICT ON DICT.VALUE= to_char(SYS_USER. LOCKED) AND DICT. TYPE = 'user_state'  "
				+ "WHERE 1 = 1 ";

		Map<String,Object> paramsMap = new HashMap<String, Object>();
		// 存在登陆账号筛选条件
		if (loginName != null && loginName.length() > 0) {
			sql = sql + " AND SYS_USER.USERNAME LIKE :loginName";
			paramsMap.put("loginName", "%" + loginName + "%");
		}
		// 存在姓名筛选条件
		if (realName != null && realName.length() > 0) {
			sql = sql + " and SYS_USER.NAME LIKE :realName";
			paramsMap.put("realName", "%" + realName + "%");
		}
		// 存在账户状态筛选条件
		if (locked != null) {
			sql = sql + " and SYS_USER.LOCKED = :locked";
			paramsMap.put("locked", locked);
		}

		// 存在部门筛选条件
		if (organizationId != null) {
			sql = sql + " and SYS_ORGANIZATION.ID = :organizationId";
			paramsMap.put("organizationId", organizationId);
		}
		// 加上按部门id进行排序条件
		sql = sql
				+ " GROUP BY SYS_USER.ID,SYS_USER.USERNAME,SYS_USER.NAME,SYS_ORGANIZATION.ID,SYS_ORGANIZATION.NAME,SYS_USER.LOCKED,DICT.NAME  "
				+ "ORDER BY SYS_ORGANIZATION.ID asc  ";

		// 存在角色分配状态筛选条件(比较特殊，需要放在后面)
		if (syURoleId != null && syURoleId.length() > 0) {
			if (Integer.valueOf(syURoleId) == 0) {// 有效
				sql = "SELECT * from (" + sql + ") where syURoleName in ('有效')";
			} else if (Integer.valueOf(syURoleId) == 1) {// 无效
				sql = "SELECT * from (" + sql + ") where syURoleName in ('无效')";
			} else {// 其他
				sql = "SELECT * from (" + sql + ") where syURoleName in ('其他')";
			}
		}
		// 获取分页需要显示的数据
		List<UserAllInfo> userList = npjd.query(PageSqlUtil.getPageSql(sql, page), paramsMap,
				new BeanPropertyRowMapper<UserAllInfo>(UserAllInfo.class));
		int count = npjd.queryForObject(PageSqlUtil.getCountSql(sql.toString()), paramsMap, Integer.class);
		return new PageResult<UserAllInfo>(userList, count);
	}

	@Override
	public UserAllInfo findOneUseInfo(Long userId) {

		// 查询的sql语句
		String sql = "SELECT SYS_USER.ID AS userId,SYS_USER.USERNAME AS loginName,SYS_USER. NAME AS realName,SYS_ORGANIZATION. ID AS orgId,   "
				+ "SYS_ORGANIZATION. NAME AS orgName,SYS_USER. LOCKED,DICT. NAME AS userState,  "
				+ "WM_CONCAT(SYS_ROLE. NAME) AS roleName,WM_CONCAT(DISTINCT(SYS_USER_ROLE.STATE)) AS syURoleId,  "
				+ "WM_CONCAT(DISTINCT((CASE SYS_USER_ROLE.STATE WHEN 1 THEN '无效' WHEN 0 THEN '有效'	ELSE '其他' END ))) AS syURoleName  "
				+ "FROM SYS_USER  "
				+ "LEFT JOIN SYS_USER_ROLE ON SYS_USER. ID = SYS_USER_ROLE.USER_ID  "
				+ "LEFT JOIN SYS_ROLE ON SYS_USER_ROLE.ROLE_ID = SYS_ROLE. ID  "
				+ "LEFT JOIN SYS_ORGANIZATION ON SYS_USER.ORGANIZATION_ID = SYS_ORGANIZATION. ID  "
				+ "LEFT JOIN DICT ON DICT.VALUE= SYS_USER. LOCKED AND DICT. TYPE = 'user_state'  "
				+ "WHERE 1 = 1  "
				+ "and SYS_USER.ID = ?  "
				+ "GROUP BY SYS_USER.ID,SYS_USER.USERNAME,SYS_USER.NAME,SYS_ORGANIZATION.ID,SYS_ORGANIZATION.NAME,SYS_USER.LOCKED,DICT.NAME  "
				+ "ORDER BY SYS_ORGANIZATION.ID asc ";

		List<UserAllInfo> userList = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<UserAllInfo>(UserAllInfo.class),
				userId);

		if (userList.size() == 0) {
			return null;
		}

		return userList.get(0);
	}

	@Override
	public int lockedOrDeleteUser(String userId, String locdedId) {

		// 影响行数
		int sucRow = 0;
		User user = findOne(Long.parseLong(userId));
		// 激活账号
		if (locdedId != null && locdedId.equals("3")) {
			String sqlUpdate = "update sys_user set locked ='0' where ID = ?";
			sucRow = jdbcTemplate.update(sqlUpdate, userId);
		}

		// 删除账户
		if (locdedId != null && locdedId.equals("4")) {
			String sql = "delete from sys_user where id=?";
			String sql1 = "delete from sys_user_role where user_id=?";
			String sql2 = "delete from sys_user_complete where username=?";
			int row1 = jdbcTemplate.update(sql, userId);
			int row2 = jdbcTemplate.update(sql1, userId);
			int row3 = jdbcTemplate.update(sql2, user.getUsername());
			if (row1 > 0 || row2 > 0 || row3 > 0) {
				sucRow = 1;
			}
		}
		return sucRow;
	}

	@Override
	public int sysRoleUpdate(String userId) {
		String sql = "update sys_user_role set state = '0' where user_id=? ";
		int sucRow = jdbcTemplate.update(sql, userId);
		return sucRow;
	}

	@Override
	public int updateUserByUserId(String userId, String userName,
			String loginName) {
		String sqlUpdate = "update sys_user set name =? ,locked ='3' where ID = ?";
		int sucRow = jdbcTemplate.update(sqlUpdate, userName, userId);
		return sucRow;
	}

	private PageResult<User> findAllUsers(Pagination pagination) {
		String sql = "select id,organization_id, username, locked,name from sys_user ";
		List<User> userList = npjd.query(
				PageSqlUtil.getPageSql(sql, pagination),
				new BeanPropertyRowMapper<User>(User.class));
		@SuppressWarnings("deprecation")
		int count = jdbcTemplate.queryForInt(PageSqlUtil.getCountSql(sql));
		return new PageResult<User>(userList, count);
	}

	@Override
	public List<Role> findRolesByUse(Long userId) {
		String sql = "select * from sys_role where id in(select role_id from sys_user_role where user_id=? and state=0)";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Role>(
				Role.class), userId);
	}

	@Override
	public List<Resource> findResByUse(Long userId) {
		String sql = "select * from sys_resource where id in(select resource_id from sys_role_resource where role_id in(select role_id from sys_user_role where user_id=?  and state=0) and state=0)";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Resource>(
				Resource.class), userId);
	}

	@Override
	public void saveRoles(Long id, String roles) {
		// 用户状态改为未激活
		String sqlUpdate = "update sys_user set locked ='3' where ID = ?";
		jdbcTemplate.update(sqlUpdate, id);

		// 分配用户角色
		String sql = "delete from sys_user_role where user_id=?";
		jdbcTemplate.update(sql, id);

		// 获取角色账户
		String[] rolesStrings = roles.split(",");
		List<Object[]> params = new ArrayList<Object[]>();
		for (String role : rolesStrings) {
			Object[] param = new Object[2];
			param[0] = id;
			param[1] = role;
			if (role.matches("^\\d+$")) {
				params.add(param);
			}
		}

		String sql2 = "insert into sys_user_role(user_id,role_id,state) values(?,?,1)";
		jdbcTemplate.batchUpdate(sql2, params);
	}

	@Override
	public List<Role> findRolesByUser(Long id) {
		String sql = "select * from sys_role where id in(select role_id from sys_user_role where user_id=?)";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Role>(
				Role.class), id);
	}

	@Override
	public int updateLockedByUseerID(String userId, int locked) {
		String sqlUpdate = "update sys_user set locked =? where ID = ?";
		int sucessRow = jdbcTemplate.update(sqlUpdate, locked, userId);
		return sucessRow;
	}

	@Override
	public void updatePwdReset(User user) {
		String sql = "update sys_user set password=?,salt=?,update_time=create_time where id=?";
		jdbcTemplate.update(sql, user.getPassword(), user.getSalt(),
				user.getId());
		String sql1 = "update sys_user_complete set password=? where username=?";
		jdbcTemplate.update(sql1, user.getPassword(), user.getUsername());
	}
}