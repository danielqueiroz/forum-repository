package com.vanhack.forum.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping("/")
    public String index() {
        return "redirect:/findTopicByFilter";
    }
	
	@RequestMapping("/loginForm")
    public String loginForm() {
        return "login";
    }
	
	@RequestMapping("/dashboard")
	public String dashboard() {
		return "dashboard";
	}
}
