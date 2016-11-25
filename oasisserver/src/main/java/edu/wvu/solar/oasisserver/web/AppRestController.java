package edu.wvu.solar.oasisserver.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class will contain all REST calls that an app or
 * web client will make to the server.
 *
 */
@RestController
public class AppRestController {

	 @RequestMapping("/register")
	    public Account account(@RequestParam(value="username") String username,@RequestParam(value="password") String password,@RequestParam(value="email") String email) {
			
	        return new Account(username,email,password);
	 }
	 @RequestMapping("/password")
	    public Account password(@RequestParam(value="username") String username,@RequestParam(value="password") String password) {
	        return new Account(username, password);
	    }
}
