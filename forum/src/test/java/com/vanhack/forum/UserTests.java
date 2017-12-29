package com.vanhack.forum;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import com.vanhack.forum.domain.User;
import com.vanhack.forum.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTests {

	@Autowired
	UserService userService;
	
	@After
	public void eraseData() {
		User lUser = new User();
		lUser.setLogin("testUserLogin");
		List<User> lUsers = userService.findByFilter(lUser);
		if(!lUsers.isEmpty()) {
			userService.delete(lUsers.get(0));
		}	
	}
	
	@Test
	public void insertUser() {
		
		User lUser = getNewUser();
		
		try {
			lUser = userService.insert(lUser);
		} catch (Exception e) {
			assert(false);
		}
		
		User lUserDataBase = userService.findByPrimaryKey(lUser);
		
		assertEquals(lUser.getLogin(), lUserDataBase.getLogin());
	}
	
	@Test
	public void insertDuplicatedLogin() {
		
		User lUser = getNewUser();
		
		try {
			userService.insert(lUser);
			lUser.setEmail("testUser2@gmail.com"); // changing the email to test the login duplicated
			userService.insert(lUser);
			assert(false);
		} catch (Exception e) {
			assertEquals("This login already exists into the database.",e.getMessage());
		}
		
	}

	@Test
	public void insertDuplicatedEmail() {
		
		User lUser = getNewUser();
		
		try {
			userService.insert(lUser);
			lUser.setLogin("testUserLogin2");// changing the login to test the email duplicated
			userService.insert(lUser);
			assert(false);
		} catch (Exception e) {
			assertEquals("This email already exists into the database.",e.getMessage());
		}
		
	}

	
	@Test
	public void updateUser() {
		
		User lUser = getNewUser();
		
		try {
			lUser = userService.insert(lUser);
		} catch (Exception e) {
			assert(false);
		}
		
		User lUserDataBase = userService.findByPrimaryKey(lUser);
		lUserDataBase.setName("Updated name");
		
		userService.update(lUserDataBase);
		
		User lUserChanged = userService.findByPrimaryKey(lUserDataBase);
		
		assertEquals(lUserDataBase.getName(), lUserChanged.getName());
	}
	
	@Test
	public void deleteUser() {
		
		User lUser = getNewUser();
		
		try {
			lUser = userService.insert(lUser);

		} catch (Exception e) {
			assert(false);
			return;
		}

		User lUserDataBase = userService.findByPrimaryKey(lUser);
		
		userService.delete(lUserDataBase);
		
		try {
			userService.findByPrimaryKey(lUserDataBase);
			assert(false);
		} catch(EmptyResultDataAccessException e) {
			assert(true);
		} 

		
	}
	
	private User getNewUser() {
		User lUser = new User();
		lUser.setLogin("testUserLogin");
		lUser.setPassword("testPassword");
		lUser.setName("Usuario de Teste");
		lUser.setEmail("testUser@gmail.com");
		return lUser;
	}
	
	
}
