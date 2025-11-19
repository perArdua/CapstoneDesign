package com.example.campusin.infra.user;

import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserRepository")
class UserRepositoryTest extends DataJpaTestSupport {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("loginId로 사용자를 조회한다")
    void findByLoginId() {
        // given
        User user = TestEntityFactory.persistUser(em, "login-user");

        // when
        User found = userRepository.findByLoginId("login-user");

        // then
        assertThat(found.getId()).isEqualTo(user.getId());
    }
}
