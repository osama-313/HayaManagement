package com.haya.security;

import com.haya.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public AppUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    @Transactional(readOnly = true)
    public AppUserDetails loadUserByUsername(String username) {
        return users.findByUserName(username)
                .map(AppUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user: " + username));
    }
}
