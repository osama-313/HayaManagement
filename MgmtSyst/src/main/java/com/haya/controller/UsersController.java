package com.haya.controller;


import com.haya.modles.User;
import com.haya.service.UserServices;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
@Controller
public class UsersController {

    private final UserServices services;

    public UsersController(UserServices services) {
        this.services = services;
    }

    // Find all Users
    @GetMapping("/user")
    List<User> GetAllUsers(){
        return services.findAllUsers();
    }

    // add a user
    @PostMapping("/user")
    User insertUser(@RequestBody User newCustomer) {
        return services.insertUser(newCustomer);
    }

    // update a user
    @PutMapping("/user/{id}")
    User updateCustomer(@PathVariable int id, @RequestBody User updateUser) {
        return services.updateUser(id ,updateUser );
    }

    // delete a user
    @DeleteMapping("/user/{id}")
    void deleteUser(@PathVariable int id){
        services.delete(id);
    }

    //find a user by his Id
    @GetMapping("/user/{id}")
    User findUserById(@PathVariable int id){
        return services.findUserById(id);
    }


}
