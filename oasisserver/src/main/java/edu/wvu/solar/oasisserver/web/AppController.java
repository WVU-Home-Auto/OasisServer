package edu.wvu.solar.oasisserver.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AppController {

    @RequestMapping("/hello")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }
    @RequestMapping("/account")
    public String account(@RequestParam(value="username") String username,@RequestParam(value="email") String email, Model model) {
		
    	model.addAttribute("username", username);
        model.addAttribute("email", email);
        return "account";
    }
}
