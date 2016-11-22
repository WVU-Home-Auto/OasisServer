package edu.wvu.solar.oasisserver.web;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordController {


    @RequestMapping("/password")
    public Password password(@RequestParam(value="username") String username,@RequestParam(value="password") String password) {
        return new Password(username,
                            password);
    }
}