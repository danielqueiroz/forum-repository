package com.vanhack.forum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.vanhack.forum.domain.User;
import com.vanhack.forum.exception.ForumApplicationException;
import com.vanhack.forum.repository.UserRepository;

@Component
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Transactional
	public User insert(User pObject) throws Exception {
		
		validate(pObject); 
		
		try {
			
			pObject.setPassword(new BCryptPasswordEncoder().encode(pObject.getPassword()));
			
			pObject = userRepository.insert(pObject);
			userRepository.insertDefaultRole(pObject);
			return pObject;
		} catch (DuplicateKeyException e) {
			
			if(e.getMessage().contains("login_UNIQUE")) {
				throw new Exception("This login already exists into the database.",e);
			} else if(e.getMessage().contains("email_UNIQUE")){
				throw new Exception("This email already exists into the database.",e);
			} else {
				throw new Exception("This user already exists into the database.",e);
			}
			
			
		}
	}

	private void validate(User pObject) throws ForumApplicationException {
		if(pObject == null) {
			throw new ForumApplicationException("It is not possible insert a null User.");
		} else if(StringUtils.isEmpty(pObject.getEmail())) {
			throw new ForumApplicationException("The email cannot be empty.");
		} else if(StringUtils.isEmpty(pObject.getName().trim())) {
			throw new ForumApplicationException("The name cannot be empty.");
		} else if(StringUtils.isEmpty(pObject.getName().trim())) {
			throw new ForumApplicationException("The name cannot be empty.");
		} else if(StringUtils.isEmpty(pObject.getPassword().trim())) {
			throw new ForumApplicationException("The password cannot be empty.");
		}
	}

	@Transactional
	public void update(User pObject) {

		userRepository.update(pObject);
	}

	@Transactional
	public void delete(User pObject) {
		
		userRepository.deleteRoles(pObject);
		userRepository.delete(pObject);
	}

	public User findByPrimaryKey(User pObject) {
		
		return userRepository.findByPrimaryKey(pObject);
	}

	public List<User> findByFilter(User pObject) {
		
		return userRepository.findByFilter(pObject);

	}
	
}
