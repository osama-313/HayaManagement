package com.haya.service;

import com.haya.modles.User;
import com.haya.repository.UserRepository;
import com.haya.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository reop;

    public UserDetailServiceImpl(UserRepository reop) {
        this.reop = reop;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User Userinfo = reop.findByUserName(username);
        if (Userinfo == null) {
            throw new UsernameNotFoundException(username);
        }
        UserDetails user = org.springframework.security.core.userdetails.User
                .withUsername(Userinfo.getUserName())
                .password(Userinfo.getPassword())
                .authorities(Userinfo.getRole().getRole())
                .build();
        return user;
    }
}
