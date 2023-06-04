package com.example.campusin.infra.badge;

import com.example.campusin.domain.badge.Badge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by kok8454@gmail.com on 2023-06-05
 * Github : http://github.com/perArdua
 */

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    public Page<Badge> findAllByUserId(Long userId, Pageable pageable);
}
