package com.vanhack.forum;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import com.vanhack.forum.domain.Topic;
import com.vanhack.forum.domain.User;
import com.vanhack.forum.exception.ForumApplicationException;
import com.vanhack.forum.service.TopicService;
import com.vanhack.forum.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TopicTests {

	private static final String THE_AUTHOR_DOES_NOT_EXIST_INTO_THE_DATABASE = "The author does not exist into the database.";

	private static final String THE_TITLE_CANNOT_BE_NULL = "The title cannot be empty.";

	private static final String THE_AUTHOR_CANNOT_BE_NULL = "The author cannot be empty.";
	
	private static final String THE_TEXT_CANNOT_BE_NULL = "The text cannot be empty.";

	private User user;
	
	@Autowired
	TopicService topicService;
	
	@Autowired
	UserService userService;
	
	@After
	public void eraseData() {
		List<Topic> lTopics = topicService.findByFilter("999Topic's Title999");
		if(!lTopics.isEmpty()) {
			topicService.delete(lTopics.get(0));
		}
		
		userService.delete(user);
	}
	
	@Before
	public void insertUser() {
		User lUser = new User();
		lUser.setLogin("testUserLogin");
		lUser.setPassword("testPassword");
		lUser.setName("Usuario de Teste");
		lUser.setEmail("testUserEmail@gmail.com");
		try {
			user = userService.insert(lUser);
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
	}
	
	
	@Test
	public void insert() {
		
		Topic lTopic = getNewTopic();
		
		try {
			lTopic = topicService.insert(lTopic);
		} catch (Exception e) {
			assert(false);
			return;
		}
		
		Topic lTopicInserted = null;
		
		try {
			lTopicInserted = topicService.findByPrimaryKey(lTopic);
			
			assertEquals(lTopic.getId(),lTopicInserted.getId());
			assertEquals(lTopic.getText(),lTopicInserted.getText());
			assertEquals(lTopic.getTitle(),lTopicInserted.getTitle());
			assertEquals(Integer.valueOf(0),lTopicInserted.getTotalPosts());
			assertEquals(Long.valueOf(0),lTopicInserted.getTotalViews());
		} catch (Exception e) {
			assert(false);
		}
		
	}
	
	@Test
	public void insertWithNullTitle() {
		
		Topic lTopic = getNewTopic();
		lTopic.setTitle(null);
		try {
			topicService.insert(lTopic);
			assert(false);
		} catch(Exception e) {
			assertEquals(THE_TITLE_CANNOT_BE_NULL,e.getMessage());
		}
	}
	
	@Test
	public void insertWithOneSpaceTitle() {
		
		Topic lTopic = getNewTopic();
		lTopic.setTitle(" ");
		try {
			topicService.insert(lTopic);
			assert(false);
		} catch(Exception e) {
			assertEquals(THE_TITLE_CANNOT_BE_NULL,e.getMessage());
		}
	}
	
	@Test
	public void insertWithNullAuthor() {
		
		Topic lTopic = getNewTopic();
		lTopic.setAuthor(null);
		
		try {
			topicService.insert(lTopic);
			assert(false);
		} catch(Exception e) {
			assertEquals(THE_AUTHOR_CANNOT_BE_NULL,e.getMessage());
		}
	}
	
	@Test
	public void insertWithNullAuthorLogin() {
		
		Topic lTopic = getNewTopic();
		lTopic.getAuthor().setLogin(null);
		
		try {
			topicService.insert(lTopic);
			assert(false);
		} catch(Exception e) {
			assertEquals(THE_AUTHOR_CANNOT_BE_NULL,e.getMessage());
		}
	}

	@Test
	public void insertWithOneSpaceAuthorLogin() {
		
		Topic lTopic = getNewTopic();
		lTopic.getAuthor().setLogin(" ");
		
		try {
			topicService.insert(lTopic);
			assert(false);
		} catch(Exception e) {
			assertEquals(THE_AUTHOR_CANNOT_BE_NULL,e.getMessage());
		}
	}

	@Test
	public void insertWithNullText() {
		
		Topic lTopic = getNewTopic();
		lTopic.setText(null);
		
		try {
			topicService.insert(lTopic);
			assert(false);
		} catch(Exception e) {
			assertEquals(THE_TEXT_CANNOT_BE_NULL,e.getMessage());
		}
	}

	@Test
	public void insertWithOneSpaceText() {
		
		Topic lTopic = getNewTopic();
		lTopic.setText(" ");
		
		try {
			topicService.insert(lTopic);
			assert(false);
		} catch(Exception e) {
			assertEquals(THE_TEXT_CANNOT_BE_NULL,e.getMessage());
		}
	}
	
	@Test
	public void insertWithUnexistingAuthor() {
		
		Topic lTopic = getNewTopic();
		lTopic.getAuthor().setLogin("unexistingLogin");
		
		try {
			topicService.insert(lTopic);
			assert(false);
		} catch(Exception e) {
			assert(e instanceof ForumApplicationException);
			assert(e.getCause() instanceof DataIntegrityViolationException);
			assertEquals(THE_AUTHOR_DOES_NOT_EXIST_INTO_THE_DATABASE,e.getMessage());
		}
	}
	
	@Test
	public void updateView() {
		
		Topic lTopic = getNewTopic();
		
		try {
			lTopic = topicService.insert(lTopic);
		} catch (Exception e) {
			assert(false);
			return;
		}
		
		topicService.updateTotalViews(lTopic);
		
		Topic lTopicInserted;
		
		try {
			lTopicInserted = topicService.findByPrimaryKey(lTopic);
			assertEquals(lTopic.getId(),lTopicInserted.getId());
			assertEquals(lTopic.getText(),lTopicInserted.getText());
			assertEquals(lTopic.getTitle(),lTopicInserted.getTitle());
			assertEquals(Integer.valueOf(0),lTopicInserted.getTotalPosts());
			assertEquals(Long.valueOf(1),lTopicInserted.getTotalViews());
		} catch (Exception e) {
			assert(false);	
		}
		
	}
	
	@Test
	public void findByPrimaryKeyWithWrongId() {
		Topic lTopic = new Topic();
		lTopic.setId(Long.valueOf(9876));
		
		try {
			topicService.findByPrimaryKey(lTopic);
			assert(false);
		} catch(ForumApplicationException e) {
			assertEquals("The selected entity does not exist in the database.",e.getMessage());
		} catch(Exception e) {
			
			assertEquals("The selected entity does not exist in the database.",e.getMessage());
		}
		
	}
	
	private Topic getNewTopic() {
		Topic lTopic = new Topic();
		lTopic.setTitle("999Topic's Title999");
		lTopic.setText("Which is the best programming language: Java or .NET?");
		lTopic.setAuthor(user);
		return lTopic;
	}
	

}
