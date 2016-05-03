package cn.yc.ssh.admin.base.entity;

import java.io.Serializable;
import java.util.List;

public class Role implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9181183791953594338L;
	private Long id; // 编号
	private String role; // 角色标识 程序中判断使用,如"admin"
	private String name; // 角色描述,UI界面显示使用
	private String state = "1";//用户角色是否有效，0：有效    1：无效    2：待删除
	private String stateName;//用户角色状态名称（0：有效    1：无效    2：待删除）
	private List<Long> resourceIds; // 拥有的资源


	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Role role = (Role) o;

		if (id != null ? !id.equals(role.id) : role.id != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Long> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(List<Long> resourceIds) {
		this.resourceIds = resourceIds;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	
	
	

}
