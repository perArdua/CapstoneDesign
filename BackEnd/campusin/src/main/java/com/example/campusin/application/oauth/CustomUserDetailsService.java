package com.example.campusin.application.oauth;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import com.example.campusin.domain.user.Users;
import com.example.campusin.infra.user.UserRepository;
import com.example.campusin.domain.oauth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepository.findByUserId(username);
        if (users == null) {
            throw new UsernameNotFoundException("Can not find username.");
        }
        return UserPrincipal.create(users);
    }
}
