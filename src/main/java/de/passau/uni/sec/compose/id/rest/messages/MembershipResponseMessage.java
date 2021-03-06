package de.passau.uni.sec.compose.id.rest.messages;

import java.util.Date;

import de.passau.uni.sec.compose.id.core.persistence.entities.Membership;

public class MembershipResponseMessage implements EntityResponseMessage
{
	private String id;
	
	private String user_id;
	
	private String role;
	
	private String group_id;
	
	private Date lastModified;

	public MembershipResponseMessage(Membership mem)
	{
		id = mem.getId();
		group_id = mem.getGroup().getId();
		role = mem.getRole().getName();
		lastModified = mem.getLastModified();
		user_id=mem.getUser().getId();
		
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
	
}
