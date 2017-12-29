package com.vanhack.forum.domain;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class User {

	private Integer id;
	
	@NotNull
	@Size(max=45)
	private String name;

	@NotNull
	@Size(max=45)
	private String login;

	@NotNull
	@Size(max=45)
	private String password;
	
	@NotNull
	@Size(max=45)
	private String email;
	
	private Date creationDate;

	public User() {}
	
	public User(Integer pId, String pName, String pLogin, String pPassword, Date pCreationDate, String pEmail) {
		this.id = pId;
		this.name = pName;
		this.login = pLogin;
		this.password = pPassword;
		this.creationDate = pCreationDate;
		this.email = pEmail;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}
