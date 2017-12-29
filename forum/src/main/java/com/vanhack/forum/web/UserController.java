package com.vanhack.forum.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vanhack.forum.domain.User;
import com.vanhack.forum.service.UserService;

@Controller
public class UserController {

	@Autowired
	UserService userService;
	
	@RequestMapping("/signup")
    public String signup(final ModelMap model) {
        model.addAttribute("user", new User());
        return "signup";
    }
	
	@RequestMapping("/createUser")
	public String createUser(@Valid final User user, final BindingResult bindingResult, final ModelMap model) {
		
		if(bindingResult.hasErrors()) {
            return "signup";
        }

		try {
			userService.insert(user);
			model.clear();
		} catch (Exception e) {
			model.addAttribute("exception", e.getMessage());
			return "signup";
		}

		return "login";
	}
}
