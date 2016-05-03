package cn.yc.ssh.admin.base.entity;

import java.io.Serializable;

public class Organization implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6146178164305081071L;
	private Long id; //编号
    private String name; //组织机构名称
    private Long parentId; //父编号
    private String parentIds; //父编号列表，如1/2/
    private Boolean available = Boolean.FALSE;
    private Boolean isParent;
    
    private String power;//用户组织权限，若为*号代表为全部，若为部门名称id代表只有该部门权限

    public Organization(){
    	super();
    }
    
    
    
    
    public String getPower() {
		return power;
	}




	public void setPower(String power) {
		this.power = power;
	}




	public static long getSerialversionuid() {
		return serialVersionUID;
	}




	public Organization(Long id,String name){
    	this.id = id;
    	this.name = name;
    }
    public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }


    public String makeSelfAsParentIds() {
        return getParentIds() + getId() + "/";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Organization that = (Organization) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

	@Override
	public String toString() {
		return "Organization [id=" + id + ", name=" + name + ", parentId="
				+ parentId + ", parentIds=" + parentIds + ", available="
				+ available + ", isParent=" + isParent + "]";
	}
    
}
