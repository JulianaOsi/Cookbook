package com.example.cookbook.controller;

import com.example.cookbook.domain.User;
import com.example.cookbook.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class RegistrationController {

    @Autowired
    RegistrationService registrationService;

    @GetMapping("/registration")
    public ModelAndView registration() {
        return new ModelAndView("registration");
    }

    @PostMapping("/user/account")
    public ModelAndView getAccountPage(
            @AuthenticationPrincipal User user,
            Map<String, Object> model) {
        model.put("user", user);
        return new ModelAndView("/account");
    }

    @PostMapping("/registration")
    public ModelAndView addUser(User user, Map<String,Object> model) {
        if(registrationService.isUserExists(user)) {
            model.put("message", "User exits");
            return new ModelAndView("/registration", model);
        }
        registrationService.addUser(user);
        return new ModelAndView("/login");
    }
}
