package com.Carldevweb.archboard.security;

import com.Carldevweb.archboard.user.domain.User;
import com.Carldevweb.archboard.user.infra.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository users;

    public UserDetailsServiceImpl(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = users.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new AuthUserPrincipal(
                u.getId(),
                u.getEmail(),
                u.getPasswordHash(),
                u.isEnabled(),
                u.getRole().name()
        );
    }
}