package com.vanhack.forum.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vanhack.forum.domain.Post;
import com.vanhack.forum.domain.Topic;
import com.vanhack.forum.domain.User;

@Component
public class TopicRepositoryImpl implements TopicRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	Object obj = new Object(); 

	@Override
	public Topic insert(Topic pObject) {

		StringBuilder lSql = new StringBuilder();
		lSql.append("insert into forum.topic(title, creation_date, id_user, text) ");
		lSql.append("values (?,now(),(select u.id_user from user u where u.login=?),?) ");
		
		synchronized (obj) {
			jdbcTemplate.update(lSql.toString(), pObject.getTitle(), pObject.getAuthor().getLogin(), pObject.getText());
			pObject.setId(getLastInsertedID());
		}
		
		return pObject;
	}

	@Override
	public void update(Topic pObject) {}
	
	@Override
	public synchronized void updateTotalViews(Topic pObject) {
		
		StringBuilder lSql = new StringBuilder();
		lSql.append("update forum.topic set total_views= total_views+1 ");
		lSql.append("where topic.id_topic = ? ");
		
		jdbcTemplate.update(lSql.toString(), pObject.getId());
	}

	@Override
	public void delete(Topic pObject) {

		StringBuilder lSql = new StringBuilder();
		lSql.append("delete from forum.topic ");
		lSql.append("where id_topic=? ");
		
		jdbcTemplate.update(lSql.toString(), pObject.getId());
	}

	@Override
	public Topic findByPrimaryKey(Topic pObject) {

		StringBuilder lSql = new StringBuilder();
		lSql.append(getDefaultTopicQuery());
		lSql.append(" topic.id_topic= ?");
		
		return jdbcTemplate.queryForObject(lSql.toString(), new Object[]{pObject.getId()}, getRowMapper());

	}

	@Override
	public List<Topic> findByFilter(Topic pObject) {
		return null;
	}

	@Override
	public List<Topic> findByFilter(String pFilter) {
		
		StringBuilder lSql = new StringBuilder();
		lSql.append(getDefaultTopicQuery());
		lSql.append(" (? is null or ");
		lSql.append("	  upper(topic.title) like concat('%', upper(?), '%') or ");
		lSql.append("     upper(topic.text) like concat('%', upper(?), '%') or ");
		lSql.append("     exists(select 1 from forum.post where post.id_topic = topic.id_topic and upper(post.text) like concat('%', upper(?), '%')))");
		
		return jdbcTemplate.query(lSql.toString(), new Object[]{pFilter,pFilter,pFilter,pFilter}, getRowMapper());

	}
	
	@Override
	public List<Post> findAllPosts(Topic pObject) {
		
		return jdbcTemplate.query(getDefaultPostQuery(), new Object[]{pObject.getId()}, getPostRowMapper());
	}
	
	@Override
	public void insert(Post pObject) {

		StringBuilder lSql = new StringBuilder();
		lSql.append("insert into forum.post(id_topic, text, creation_date, id_user) ");
		lSql.append("values (?,?,now(),(select u.id_user from user u where u.login=?)) ");
		
		jdbcTemplate.update(lSql.toString(), pObject.getTopic().getId(), pObject.getText(), pObject.getAuthor().getLogin());
		
	}
	
	public void updatePostAsCorrect(Post pPost) {
		StringBuilder lSql = new StringBuilder();
		lSql.append("update forum.post set correct_answer=1  ");
		lSql.append("where ");
		lSql.append(" post.id_post=? and ");
		lSql.append(" post.id_topic=?");
		
		jdbcTemplate.update(lSql.toString(), pPost.getId(), pPost.getTopic().getId());
	}
	
	public void updatePostAsIncorrect(Post pPost) {
		StringBuilder lSql = new StringBuilder();
		lSql.append("update forum.post set correct_answer=0  ");
		lSql.append("where ");
		lSql.append(" post.id_post<> ? and ");
		lSql.append(" post.id_topic=?");
		
		jdbcTemplate.update(lSql.toString(), pPost.getId(), pPost.getTopic().getId());
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private String getDefaultPostQuery() {
		
		StringBuilder lSql = new StringBuilder();
		lSql.append("  ");
		lSql.append(" select ");
		lSql.append(" 	post.id_post, topic.id_topic, post.text, user.id_user, user.name, post.creation_date, post.correct_answer ");
		lSql.append(" from ");
		lSql.append(" 	forum.post, forum.topic, forum.user ");
		lSql.append(" where ");
		lSql.append(" 	post.id_topic = topic.id_topic and ");
		lSql.append(" 	post.id_user = user.id_user and ");
		lSql.append(" 	topic.id_topic = ? ");
		
		return lSql.toString();
	}

	private String getDefaultTopicQuery() {
		StringBuilder lSql = new StringBuilder();
		lSql.append("select id_topic, title, text, topic.creation_date, user.id_user, user.name, user.login,  ");
		lSql.append("(select count(post.id_post) from forum.post where post.id_topic = topic.id_topic) as total_posts, ");
		lSql.append("topic.total_views ");
		lSql.append("from forum.topic, forum.user ");
		lSql.append("where ");
		lSql.append(" topic.id_user = user.id_user and ");
		return lSql.toString();
	}
	
	private RowMapper<Post> getPostRowMapper() {
		return new RowMapper<Post> () {
			@Override
			public Post mapRow(ResultSet pRs, int arg1) throws SQLException {

				Post lPost = new Post();
				lPost.setId(pRs.getLong(1));
				lPost.setTopic(new Topic());
				lPost.getTopic().setId(pRs.getLong(2));
				lPost.setText(pRs.getString(3));
				lPost.setAuthor(new User());
				lPost.getAuthor().setId(pRs.getInt(4));
				lPost.getAuthor().setName(pRs.getString(5));
				lPost.setCreationDate(pRs.getTimestamp(6));
				lPost.setCorrectAnswer(pRs.getBoolean(7));
				
				return lPost;
			}
		};
	}


	private RowMapper<Topic> getRowMapper() {
		return new RowMapper<Topic>(){

			@Override
			public Topic mapRow(ResultSet pRs, int arg1) throws SQLException {
				Topic lTopic = new Topic();
				
				lTopic.setId(pRs.getLong(1));
				lTopic.setTitle(pRs.getString(2));
				lTopic.setText(pRs.getString(3));
				lTopic.setCreationDate(pRs.getTimestamp(4));
				lTopic.setAuthor(new User());
				lTopic.getAuthor().setId(pRs.getInt(5));
				lTopic.getAuthor().setName(pRs.getString(6));
				lTopic.getAuthor().setLogin(pRs.getString(7));
				lTopic.setTotalPosts(pRs.getInt(8));
				lTopic.setTotalViews(pRs.getLong(9));
				return lTopic;
			}
		};
	}

	private Long getLastInsertedID() {
		
		return jdbcTemplate.queryForObject("select max(id_topic) from forum.topic", Long.class);
	}

}
