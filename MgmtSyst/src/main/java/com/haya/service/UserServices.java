package com.haya.service;

import com.haya.modles.User;
import com.haya.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServices {

    private UserRepository repo;

    private PasswordEncoder passwordEncoder;


    public UserServices(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    // list all users
    public List<User> findAllUsers(){
        return repo.findAll();
    }



    //add a user  (could be used for signup)
    public User insertUser(User user){

//        if (user.getRole().getId() == 1){
//            user.getRole().setRole("Admin");
//        }else if (user.getRole().getId() == 2){
//            user.getRole().setRole("User");
//        }
        String encodedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return repo.save(user);
    }


    // update a user info
    public User updateUser(int id, User updatedUser){

        User user = new User();
        user = repo.findById(id).orElse(null);

        if (user != null) {
            // Update the existing customer with the provided data
            user.setFirstName(user.getFirstName());
            user.setLastName(user.getLastName());
            user.setUserName(user.getUserName());
            user.setEmail(updatedUser.getEmail()); // you can update email
            // encode the udated password
            String encodedPassword = this.passwordEncoder.encode(updatedUser.getPassword());
            user.setPassword(encodedPassword); // you can update password
            //
            user.setAge(updatedUser.getAge()); // you can update age
            user.setProficiency(updatedUser.getProficiency()); // you can update proficiency
            user.setRole(user.getRole()); // you can update role

            return repo.save(user);
        } else {
            // Handle the case where the customer with the given ID is not found
            return null;
        }



    }

    // delete method
    public void delete(int id){
         repo.deleteById(id);
    }

    // find User by ID
    public User findUserById(int id){
        return repo.findById(id).orElse(null);
    }

    // find user by username
    public User findUserByUserName(String userName){
        return repo.findByUserName(userName);
    }










}
