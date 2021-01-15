package com.controller;

import com.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/")
@SessionAttributes("user")
public class MainController {

    @GetMapping("/index")
    @RequestMapping("/index")
    String index() {
        return "index";
    }

    /**
     * This is used to return login page
     * @return String returns login
     */
    @RequestMapping("/login")
    public String userLogin(){
        return "login";
    }


    @RequestMapping(path = "/register")
    public String personForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }



}

