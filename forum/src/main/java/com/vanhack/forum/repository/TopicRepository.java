package com.vanhack.forum.repository;

import java.util.List;

import com.vanhack.forum.domain.Post;
import com.vanhack.forum.domain.Topic;

public interface TopicRepository extends ForumRepository<Topic> {

	public List<Topic> findByFilter(String pFilter);
	
	public void updateTotalViews(Topic pObject);
	
	public List<Post> findAllPosts(Topic pObject);

	public void insert(Post pObject);
	
	public void updatePostAsCorrect(Post pPost);	
	
	public void updatePostAsIncorrect(Post pPost);
}
