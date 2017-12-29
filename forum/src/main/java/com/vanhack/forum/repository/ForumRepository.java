package com.vanhack.forum.repository;

import java.util.List;

public interface ForumRepository<T> {

	public T insert(T pObject);
	
	public void update(T pObject);
	
	public void delete(T pObject);
	
	public T findByPrimaryKey(T pObject);
	
	public List<T> findByFilter(T pObject);
}
