package com.example.campusin.application.user;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import com.example.campusin.domain.user.Users;
import com.example.campusin.infra.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Users getUser(String userId) {
        return userRepository.findByUserId(userId);
    }
}
