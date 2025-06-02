package com.example.campusin.infra.user;
/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import com.example.campusin.domain.user.UserRefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRefreshTokenRepository extends CrudRepository<UserRefreshToken, String> {

    UserRefreshToken findByLoginId(String loginId);
    UserRefreshToken findByLoginIdAndRefreshToken(String loginId, String refreshToken);
}