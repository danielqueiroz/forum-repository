package com.vanhack.forum.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.vanhack.forum.domain.User;

@Component
public class UserRepositoryImpl implements UserRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	Object obj = new Object(); 
	
	@Override
	public User insert(User pObject) {
		
		StringBuilder lSql = new StringBuilder();
		lSql.append("insert into forum.user (name, login, password,creation_date, email) ");
		lSql.append("values (?,?,?,now(), ?) ");
		
		synchronized(obj) {
			
			jdbcTemplate.update(lSql.toString(), pObject.getName(), pObject.getLogin(), pObject.getPassword(), pObject.getEmail());
			pObject.setId(getLastInsertedID());
		}
		
		return pObject;
	}

	@Override
	public void insertDefaultRole(User pObject) {
		
		StringBuilder lSql = new StringBuilder();
		lSql.append("insert into forum.user_role (id_user, id_role) ");
		lSql.append("values (?,(select role.id_role from forum.role where upper(role.name_role) = upper('user'))) ");
		
		jdbcTemplate.update(lSql.toString(), pObject.getId());
	}

	
	@Override
	public void update(User pObject) {

		StringBuilder lSql = new StringBuilder();
		lSql.append("update forum.user set name = ?, login = ? , password=?, email=?  ");
		lSql.append("where id_user=? ");
		
		jdbcTemplate.update(lSql.toString(), pObject.getName(), pObject.getLogin(), pObject.getPassword(), pObject.getEmail(), pObject.getId());

	}

	@Override
	public void delete(User pObject) {

		StringBuilder lSql = new StringBuilder();
		lSql.append("delete from forum.user ");
		lSql.append("where id_user=? ");
		
		jdbcTemplate.update(lSql.toString(), pObject.getId());
	}

	@Override
	public void deleteRoles(User pObject) {

		StringBuilder lSql = new StringBuilder();
		lSql.append("delete from forum.user_role ");
		lSql.append("where id_user=? ");
		
		jdbcTemplate.update(lSql.toString(), pObject.getId());
	}

	
	@Override
	public User findByPrimaryKey(User pObject) {
		
		StringBuilder lSql = new StringBuilder();
		lSql.append(getDefaultQuery());
		lSql.append("where id_user= ? ");
		
		return jdbcTemplate.queryForObject(lSql.toString(), new Object[]{pObject.getId()},
				(rs, rownum) -> new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getTimestamp(5), rs.getString(6)) );
		
	}

	@Override
	public List<User> findByFilter(User pObject) {
		
		StringBuilder lSql = new StringBuilder();
		lSql.append(getDefaultQuery());
		lSql.append("where ");
		lSql.append(" (? is null or upper(name) like concat('%', upper(?), '%')) and ");
		lSql.append(" (? is null or upper(login) like concat('%', upper(?), '%')) ");
		
		return jdbcTemplate.query(lSql.toString(), new Object[]{pObject.getName(), pObject.getName(), pObject.getLogin(),pObject.getLogin()},
				(rs, rownum) -> new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getTimestamp(5),rs.getString(6)) );

	}

	private String getDefaultQuery() {
		StringBuilder lSql = new StringBuilder();
		lSql.append("select id_user, name, login, password,creation_date, email ");
		lSql.append("from forum.user ");
		return lSql.toString();
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	private Integer getLastInsertedID() {
		
		return jdbcTemplate.queryForObject("select max(id_user) from forum.user", Integer.class);
	}
}
