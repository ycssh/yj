package cn.yc.ssh.admin.base.entity;

public class UserAllInfo {
	private Integer userId;
	
	private String loginName;
	
	private String realName;
	
	private Integer orgId;
	
	private String orgName;
	
	private Integer locked;
	
	private String userState;
	
	private String roleName;
	
	private String syURoleId;
	
	private String syURoleName;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Integer getLocked() {
		return locked;
	}

	public void setLocked(Integer locked) {
		this.locked = locked;
	}

	public String getUserState() {
		return userState;
	}

	public void setUserState(String userState) {
		this.userState = userState;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getSyURoleId() {
		return syURoleId;
	}

	public void setSyURoleId(String syURoleId) {
		this.syURoleId = syURoleId;
	}

	public String getSyURoleName() {
		return syURoleName;
	}

	public void setSyURoleName(String syURoleName) {
		this.syURoleName = syURoleName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	
}
