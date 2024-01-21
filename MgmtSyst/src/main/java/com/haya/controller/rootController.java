package com.haya.controller;


import com.haya.modles.User;
import com.haya.service.UserServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/")
@Controller
public class rootController {


    private final UserServices services;

    public rootController(UserServices services) {
        this.services = services;
    }

    @GetMapping
    String Dashbord(Principal principal){

        User user = services.findUserByUserName(principal.getName());


        return "Welcome  " + user.getFirstName();
    }

}
