package com.vanhack.forum.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vanhack.forum.domain.Post;
import com.vanhack.forum.domain.Topic;
import com.vanhack.forum.domain.User;
import com.vanhack.forum.service.TopicService;

@Controller
public class TopicController {

	@Autowired
	TopicService topicService;
	
	@RequestMapping("/createTopic")
	@ExceptionHandler()
    public String createTopic(@Valid final Topic topic, final BindingResult bindingResult, final ModelMap model) throws Exception {
		
		if(bindingResult.hasErrors()) {
            return "topicForm";
        }
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		topic.setAuthor(new User());
		topic.getAuthor().setLogin(currentPrincipalName);
		
		try {
			topicService.insert(topic);
			model.clear();
		} catch (Exception e) {
			model.addAttribute("exception", e.getMessage());
			return "topicForm";
		}
		
        return "redirect:/findTopicByFilter";
    }
	
	
	@RequestMapping("/newTopic")
    public String newTopic(final ModelMap model) {
        
		model.addAttribute("topic", new Topic());
		return "topicForm";
    }

	@RequestMapping("/findTopicByFilter")
    public String search(@RequestParam(value="filter", required=false) String filter, Model model) {
		model.addAttribute("topics", topicService.findByFilter(filter));
        return "topicSearch";
    }

	@RequestMapping("/detailTopic")
    public String detail(@RequestParam(value="id", required=true) Long id, Model model) {
		
		Topic lTopic = new Topic();
		lTopic.setId(id);
		Post lPost = new Post();
		lTopic = topicService.findByPrimaryKeyWithPosts(lTopic);
		lPost.setTopic(lTopic);
		model.addAttribute("topic", lTopic);
		model.addAttribute("post", lPost);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		model.addAttribute("isAuthor", lTopic.getAuthor().getLogin().equals(currentPrincipalName));
		
        return "topicDetail";
    }
	
	@RequestMapping("/createPost")
	public String createPost(@Valid final Post post, final BindingResult bindingResult, final ModelMap model) {

		String redirectUrl = "redirect:/detailTopic?id="+post.getTopic().getId();
		
		if(bindingResult.hasErrors()) {
            return redirectUrl;
        }

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		post.setAuthor(new User());
		post.getAuthor().setLogin(currentPrincipalName);

		try {
			topicService.insert(post);
		} catch (Exception e) {
			model.addAttribute("exception", e.getMessage());
		}
		
		return redirectUrl;
	}
	
	@RequestMapping("/markPostAsCorrect")
	public String markPostAsCorrect(@RequestParam(value="post", required=true) Long idPost, @RequestParam(value="topic", required=true) Long idTopic, Model model) {
		Post lPost = new Post();
		lPost.setId(idPost);
		lPost.setTopic(new Topic());
		lPost.getTopic().setId(idTopic);
		
		topicService.markPostAsCorrect(lPost);
		return "redirect:/detailTopic?id="+idTopic;
	}
	

}
