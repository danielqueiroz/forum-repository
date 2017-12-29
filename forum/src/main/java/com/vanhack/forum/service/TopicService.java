package com.vanhack.forum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.vanhack.forum.domain.Post;
import com.vanhack.forum.domain.Topic;
import com.vanhack.forum.exception.ForumApplicationException;
import com.vanhack.forum.repository.TopicRepository;

@Component
public class TopicService {

	@Autowired
	private TopicRepository topicRepository;
	
	@Transactional
	public Topic insert(Topic pObject) throws Exception {
		
		validate(pObject);
		
		try {
			return topicRepository.insert(pObject);
		} catch (DataIntegrityViolationException e) {
			if(e.getCause().getMessage().contains("id_user")) {
				throw new ForumApplicationException("The author does not exist into the database.", e);
			} else {
				throw new ForumApplicationException(e);
			}
		}
	}

	@Transactional
	public void delete(Topic pObject) {
		topicRepository.delete(pObject);
	}
	
	public Topic findByPrimaryKey(Topic pObject) throws Exception {
		
		Topic lTopic = null;
		
		try {
			lTopic = topicRepository.findByPrimaryKey(pObject);
		} catch(Exception e) {
			if(e instanceof EmptyResultDataAccessException) {
				throw new ForumApplicationException("The selected entity does not exist in the database.");
			}
		}
		
		return lTopic;
	}
	
	public Topic findByPrimaryKeyWithPosts(Topic pObject) {
		
		Topic lTopic = topicRepository.findByPrimaryKey(pObject);
		lTopic.setPosts(topicRepository.findAllPosts(lTopic));
		updateTotalViews(pObject);
		
		return lTopic;
	}

	private void validate(Topic pObject) throws ForumApplicationException {
		if(pObject == null) {
			throw new ForumApplicationException("It is not possible insert a null Topic.");
		} else if(pObject.getAuthor() == null 
				|| pObject.getAuthor().getLogin() == null
				|| "".equals(pObject.getAuthor().getLogin().trim())) {
			throw new ForumApplicationException("The author cannot be empty.");
		} else if(pObject.getTitle() == null || "".equals(pObject.getTitle().trim())) {
			throw new ForumApplicationException("The title cannot be empty.");
		} else if(pObject.getText() == null || "".equals(pObject.getText().trim())) {
			throw new ForumApplicationException("The text cannot be empty.");
		}
	}
	
	@Transactional
	public void insert(Post pPost) throws Exception {
		
		validate(pPost);
		
		topicRepository.insert(pPost);
	}

	private void validate(Post pPost) throws ForumApplicationException {
		if(pPost == null) {
			throw new ForumApplicationException("It is not possible insert a null Post.");
		} else if(pPost.getAuthor() == null 
				|| StringUtils.isEmpty(pPost.getAuthor().getLogin())) {
			throw new ForumApplicationException("The author cannot by empty.");
		} else if(StringUtils.isEmpty(pPost.getText())) {
			throw new ForumApplicationException("The text cannot be empty.");
		} else {
			
		}
	}

	
	@Transactional
	public void markPostAsCorrect(Post pPost) {
		//Fist set all posts, but the post received as parameter, as incorrect
		topicRepository.updatePostAsIncorrect(pPost);
		
		//Secondly set the post received as parameter as correct
		topicRepository.updatePostAsCorrect(pPost);
	}
	
	public List<Topic> findByFilter(String pFilter) {
		try {
			return topicRepository.findByFilter(pFilter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Transactional
	public void updateTotalViews(Topic pObject) {
		topicRepository.updateTotalViews(pObject);
	}
	
}
