package com.vanhack.forum.repository;

import com.vanhack.forum.domain.User;

public interface UserRepository extends ForumRepository<User> {

	public void insertDefaultRole(User pObject);
	
	public void deleteRoles(User pObject);
}
