package com.haya.config;

import com.haya.modles.User;
import com.haya.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserServices services;
    @Autowired
    public UserDetailService(UserServices services) {
        this.services = services;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User Userinfo = services.findUserByUserName(username);
        if (Userinfo == null) {
            throw new UsernameNotFoundException(username);
        }
        UserDetails user = org.springframework.security.core.userdetails.User.withUsername(Userinfo.getUserName()).password(Userinfo.getPassword()).authorities(Userinfo.getRole().getRole())
                .build();
        return user;
    }
}
