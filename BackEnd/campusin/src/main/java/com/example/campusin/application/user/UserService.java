package com.example.campusin.application.user;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import com.example.campusin.domain.user.User;
import com.example.campusin.domain.user.dto.response.NickResponse;
import com.example.campusin.domain.user.dto.response.UserIdResponse;
import com.example.campusin.infra.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(String loginId) {
        return userRepository.findByLoginId(loginId);
    }
    public NickResponse nicknameCheck(String loginId) {
        User user = userRepository.findByLoginId(loginId);
        if (Objects.isNull(user.getNickname())) {
            return new NickResponse("해당 닉네임을 설정하지 않았습니다.");
        }
        else {
            return new NickResponse(user.getNickname());
        }
    }
    public User createNickname(String loginId, String nickname) {
        User user = userRepository.findByLoginId(loginId);
        user.setNickname(nickname);
        return userRepository.save(user);
    }

    public UserIdResponse getUserId(String loginId) {
        User user = userRepository.findByLoginId(loginId);
        return new UserIdResponse(user.getId());
    }

    public User makeAdmin(String loginId) {
        User user = userRepository.findByLoginId(loginId);
        user.setAdmin();
        return userRepository.save(user);
    }
}
